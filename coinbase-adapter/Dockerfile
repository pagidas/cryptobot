FROM openjdk:14-alpine
COPY . /project/src/

RUN cd /project/src \
    && ./gradlew build shadowJar -i \
    && mkdir -p /project/app \
    && cp /project/src/build/libs/coinbase-adapter-1.0-SNAPSHOT-all.jar /project/app/app.jar \
    && rm -rf /project/src/ \
    && rm -rf ~/.gradle \
    && rm -rf ~/.kotlin

WORKDIR /project/app
ENTRYPOINT ["java", "-jar", "app.jar"]