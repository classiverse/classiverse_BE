# 1. 자바 21 버전 환경을 준비해라
FROM openjdk:21-jdk

# 2. 도커 안에 /app 이라는 작업 공간을 만들어라
WORKDIR /app

# 3. 빌드된 jar 파일(*.jar)을 도커 안으로 가져와서 'app.jar'라고 이름 붙여라
COPY build/libs/*.jar app.jar

# 4. 도커가 켜지면 이 명령어를 실행해라 (prod 프로필로 서버 실행)
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]