FROM openjdk:14-alpine
COPY build/libs/graphql-*-all.jar graphql.jar
EXPOSE 9090
CMD ["java", "-Dcom.sun.management.jmxremote", "-Xmx128m", "-jar", "graphql.jar"]