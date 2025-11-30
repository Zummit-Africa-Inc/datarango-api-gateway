package com.datarango.gateway.middleware;

import com.datarango.gateway.service.MicroserviceClient;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

@RequiredArgsConstructor
public class RateLimitFilter implements Filter {

    private final RedisTemplate<String, String> redisTemplate;
    private final MicroserviceClient microserviceClient;

    @Value("${rate-limit.requests-per-minute}")
    private int requestsPerMinute;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (hasSubscription(httpRequest)) {
            chain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIp(httpRequest);
        String key = "rate_limit:" + clientIp;

        String count = redisTemplate.opsForValue().get(key);
        int currentCount = count != null ? Integer.parseInt(count) : 0;

        if (currentCount >= requestsPerMinute) {
            httpResponse.setStatus(429);
            httpResponse.getWriter().write("{\"error\":\"Rate limit exceeded\"}");
            return;
        }

        redisTemplate.opsForValue().increment(key);
        Duration expiration = Duration.ofMinutes(1);
        if (expiration != null) {
            redisTemplate.expire(key, expiration);
        }

        chain.doFilter(request, response);
    }

    private boolean hasSubscription(HttpServletRequest request) {
        String userId = getUserId(request);
        if (userId == null)
            return false;

        String cacheKey = "subscription:active:" + userId;
        String cached = redisTemplate.opsForValue().get(cacheKey);

        if ("true".equals(cached)) {
            return true;
        }

        if ("false".equals(cached)) {
            return false;
        }

        try {
            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> userResponse = microserviceClient.callUserService("/users/" + userId,
                    HttpMethod.GET,
                    null, Map.class);
            @SuppressWarnings("unchecked")
            Map<String, Object> user = (Map<String, Object>) userResponse.getBody();
            if (user == null) {
                cacheSubscriptionStatus(cacheKey, false, 60);
                return false;
            }

            String subscriptionId = (String) user.get("subscriptionId");
            if (subscriptionId == null) {
                cacheSubscriptionStatus(cacheKey, false, 60);
                return false;
            }

            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> subResponse = microserviceClient.callUserService(
                    "/users/subscriptions/" + subscriptionId,
                    HttpMethod.GET, null, Map.class);
            @SuppressWarnings("unchecked")
            Map<String, Object> subscription = (Map<String, Object>) subResponse.getBody();
            if (subscription == null) {
                cacheSubscriptionStatus(cacheKey, false, 60);
                return false;
            }

            String expiryStr = (String) subscription.get("expiryDate");
            boolean isActive = expiryStr != null && LocalDateTime.parse(expiryStr).isAfter(LocalDateTime.now());

            cacheSubscriptionStatus(cacheKey, isActive, isActive ? 300 : 60);

            return isActive;
        } catch (Exception e) {
            return false;
        }
    }

    private void cacheSubscriptionStatus(String cacheKey, boolean status, int seconds) {
        if (cacheKey == null || cacheKey.isEmpty()) {
            return;
        }
        try {
            String value = String.valueOf(status);
            Duration duration = Duration.ofSeconds(seconds);
            if (value != null && duration != null) {
                redisTemplate.opsForValue().set(cacheKey, value, duration);
            }
        } catch (Exception e) {
        }
    }

    private String getUserId(HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null)
            return null;

        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        return header != null && header.startsWith("Bearer ") ? header.substring(7) : null;
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        return xForwardedFor != null ? xForwardedFor.split(",")[0] : request.getRemoteAddr();
    }
}