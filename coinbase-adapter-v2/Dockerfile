FROM openjdk:14-alpine
COPY build/libs/coinbase-adapter-v2-*-all.jar coinbase-adapter-v2.jar
EXPOSE 8080
CMD ["java", "-Dcom.sun.management.jmxremote", "-Xmx128m", "-jar", "coinbase-adapter-v2.jar"]