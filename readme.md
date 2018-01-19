# Notes Api

This Rest API was implemented with [Spring Boot](https://projects.spring.io/spring-boot/) and uses the [Gradle](https://gradle.org/) build tool. Persistence is done with [h2](http://www.h2database.com/html/main.html) an in-memory database.

## Building the application

Windows:
```
gradlew.bat build
```
*nix:
```
./gradlew build
```

## Running the application

The Spring Boot application is running on port 8080 locally. The base URL of the api is http://localhost:8080/api/notes

Windows:
```
gradlew.bat bootRun
```
*nix:
```
./gradlew bootRun
```

## Running tests

Windows:
```
gradlew.bat test
```
*nix:
```
./gradlew test
```