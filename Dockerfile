FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY . .
RUN ./gradlew bootJar -Djava-profiles-active=dev

FROM eclipse-temurin:21-jre-alpine
COPY --from=builder /app/build/libs/ddd-12-moyorak-api-0.0.1-SNAPSHOT.jar moyorak-application.jar
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=70", "-Duser.timezone=Asia/Seoul", "-jar", "/moyorak-application.jar"]
