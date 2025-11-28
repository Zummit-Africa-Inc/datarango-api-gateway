install:
	mvn clean install

start:
	mvn spring-boot:run

build:
	mvn clean compile

kill:
	taskkill /f /im java.exe