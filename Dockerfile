FROM node:21-alpine AS build-spa

COPY ./ui .

RUN npm ci && npm run build

FROM amazoncorretto:21-alpine
WORKDIR /app

COPY ./api .
COPY --from=build-spa ./build/ ./src/main/resources/public/

RUN ./gradlew clean build -x test && \
    mv ./build/libs/url_shortener-0.0.1-SNAPSHOT.jar . && \
    rm -rf /tmp/* /root/.gradle ./.gradle ./build ./src/main/kotlin

CMD ["java", "-jar", "./url_shortener-0.0.1-SNAPSHOT.jar"]
