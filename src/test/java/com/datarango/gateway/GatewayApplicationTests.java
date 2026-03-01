package com.datarango.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
	"eureka.client.enabled=false",
	"spring.cloud.discovery.enabled=false",
	"spring.cloud.loadbalancer.enabled=false",
	"spring.data.redis.repositories.enabled=false",
	"management.health.redis.enabled=false",
	"azure.storage.connection-string=DefaultEndpointsProtocol=https;AccountName=test;AccountKey=dGVzdA==;EndpointSuffix=core.windows.net",
	"azure.storage.container-name=test"
})
class GatewayApplicationTests {

	@Test
	void contextLoads() {
	}

}

