# Use official Eclipse Temurin OpenJDK 21 image as base
FROM openjdk:21-jdk-slim

# 작업 디렉터리 설정
WORKDIR /app

# 빌드된 jar 파일 복사
COPY build/libs/*.jar app.jar

# Make port 8080 available to the world outside this container
EXPOSE 8080

# 컨테이너 시작 시 실행할 명령어
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
