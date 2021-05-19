ARG VERSION=14
FROM openjdk:${VERSION}-alpine
RUN apk --no-cache add curl
COPY build/libs/*-all.jar /bin/app.jar
CMD ["java", "-Dcom.sun.management.jmxremote", "-Xmx128m", "-jar", "/bin/app.jar"]
