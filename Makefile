install:
	mvn clean install

start:
	mvn spring-boot:run

build:
	mvn clean compile

package:
	mvn clean package

kill:
	taskkill /f /im java.exe