# ---------- Build stage (Gradle) ----------
FROM gradle:8.14.3-jdk21 AS build
WORKDIR /home/gradle/app

# 전체 복사 (캐시 최적화보다 단순 안정성 우선)
COPY . .

# 느린 네트워크 대비: Gradle Wrapper/HTTP 타임아웃 확대
RUN printf "systemProp.org.gradle.internal.http.connectionTimeout=120000\nsystemProp.org.gradle.internal.http.socketTimeout=120000\n" >> gradle.properties

# 테스트 생략하고 부트 JAR 빌드
RUN gradle --no-daemon clean bootJar -x test

# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre
ENV TZ=Asia/Seoul
WORKDIR /app

# 앱 JAR 복사
COPY --from=build /home/gradle/app/build/libs/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java","-XX:+UseG1GC","-XX:MaxRAMPercentage=75","-jar","/app/app.jar"]
