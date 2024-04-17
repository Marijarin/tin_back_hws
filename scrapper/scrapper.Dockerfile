FROM eclipse-temurin:21-jdk
VOLUME /temp
COPY ./target/scrapper.jar scrapper.jar
ENTRYPOINT ["java","-jar","/scrapper.jar"]
