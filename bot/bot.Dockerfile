FROM eclipse-temurin:21-jdk as builder
ENV RELEASE=21
WORKDIR /opt/build
COPY ./target/bot.jar ./application.jar

RUN java -Djarmode=layertools -jar application.jar extract
RUN $JAVA_HOME/bin/jlink \
         --add-modules `jdeps --ignore-missing-deps -q -recursive --multi-release ${RELEASE} --print-module-deps -cp 'dependencies/BOOT-INF/lib/*':'snapshot-dependencies/BOOT-INF/lib/*' application.jar` \
         --strip-debug \
         --no-man-pages \
         --no-header-files \
         --output jdk

FROM debian:buster-slim
ARG BUILD_PATH=/opt/build
ENV JAVA_HOME=/opt/jdk
ENV PATH "${JAVA_HOME}/bin:${PATH}"

RUN groupadd --gid 1000 bot \
  && useradd --uid 1000 --gid bot --shell /bin/bash --create-home bot

USER bot:bot
WORKDIR /opt/workspace
COPY --from=builder $BUILD_PATH/jdk $JAVA_HOME
COPY --from=builder $BUILD_PATH/spring-boot-loader/ ./
COPY --from=builder $BUILD_PATH/dependencies/ ./
COPY --from=builder $BUILD_PATH/application/ ./
EXPOSE 8090
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
