FROM eclipse-temurin:21-jdk
VOLUME /tmp
COPY ./target/scrapper.jar scrapper.jar
ENTRYPOINT ["java","-jar","/scrapper.jar"]
