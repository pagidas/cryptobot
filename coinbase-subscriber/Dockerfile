ARG VERSION=14
FROM openjdk:${VERSION}-alpine as BUILD

# Get gradle distribution
COPY *.gradle gradle.* gradlew /src/
COPY gradle /src/gradle
WORKDIR /src
RUN ./gradlew --version

COPY . .
RUN ./gradlew --no-daemon shadowJar -i

# Stage 2, distribution container
FROM openjdk:${VERSION}-alpine
COPY --from=BUILD /src/build/libs/coinbase-subscriber-*-all.jar /bin/app.jar
CMD ["java", "-jar", "/bin/app.jar"]
