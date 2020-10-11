ARG VERSION=14
FROM openjdk:${VERSION}-alpine
COPY build/libs/coinbase-subscriber-*-all.jar /bin/app.jar
CMD ["java", "-jar", "/bin/app.jar"]
