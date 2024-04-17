FROM maven as build
WORKDIR /build
COPY src src
COPY pom.xml pom.xml
RUN  mvn clean package dependency:copy-dependencies -DincludeScope=runtime
