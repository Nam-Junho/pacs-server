FROM gradle:8.0.2-jdk17-alpine as builder
WORKDIR /build

COPY build.gradle settings.gradle ./
RUN gradle build -x test --parallel --continue > /dev/null 2>&1 || true

COPY . .
RUN gradle clean bootjar
RUN java -Djarmode=layertools -jar ./build/libs/*.jar extract

FROM eclipse-temurin:17.0.6_10-jdk-alpine

VOLUME [ "/tmp", "/logs", "/files" ]

COPY --from=builder /build/dependencies/ ./
COPY --from=builder /build/spring-boot-loader/ ./
COPY --from=builder /build/snapshot-dependencies/ ./
RUN true
COPY --from=builder /build/application/ ./

CMD [ \
    "java" \
    ,"-Djava.security.egd=file:/dev/./urandom" \
    ,"-Xms1000m" \
    ,"-Xmx1000m" \
    ,"org.springframework.boot.loader.JarLauncher" \
]