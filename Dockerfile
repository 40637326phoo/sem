FROM openjdk:latest
COPY ./target/seMethods-v0.1-alpha-4-jar-with-dependencies.jar /tmp
WORKDIR /tmp
ENTRYPOINT ["java", "-jar", "seMethods-v0.1-alpha-4-jar-with-dependencies.jar"]