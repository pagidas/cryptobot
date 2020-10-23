ARG VERSION=14
FROM openjdk:${VERSION}-alpine
COPY build/libs/coinbase-adapter-*-all.jar /bin/app.jar
CMD ["java", "-Dcom.sun.management.jmxremote", "-Xmx128m", "-jar", "/bin/app.jar"]
