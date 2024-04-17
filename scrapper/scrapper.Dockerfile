FROM eclipse-temurin:21-jdk
COPY /home/runner/work/tin_back_hws/tin_back_hws/scrapper/target/scrapper.jar scrapper.jar
ENTRYPOINT ["java","-jar","/scrapper.jar"]
