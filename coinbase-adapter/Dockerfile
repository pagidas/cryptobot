FROM openjdk:14-alpine
COPY . /project/src/
RUN mkdir -p /project/app/
WORKDIR /project/src/
RUN  ./gradlew build shadowJar -i
RUN  cp build/libs/coinbase-adapter-1.0-SNAPSHOT-all.jar /project/app/app.jar
WORKDIR /project/app
RUN rm -rf /project/src/
ENTRYPOINT ["java", "-jar", "app.jar"]