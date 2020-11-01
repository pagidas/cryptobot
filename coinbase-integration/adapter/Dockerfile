ARG VERSION=14
FROM openjdk:${VERSION}-alpine
COPY build/libs/adapter*-all.jar /bin/app.jar
CMD ["java", "-jar", "/bin/app.jar"]
