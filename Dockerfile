# 建立 maven 環境並打包
FROM maven:3.8.4-openjdk-11-slim AS build

WORKDIR /workdir

COPY . .

RUN ["mvn", "package", "-Dmaven.test.skip"]

# 建立運行容器
FROM openjdk:11.0.14-jre-slim-buster AS prod

LABEL \
  version="1.0" \
  maintainer="yuus271828@gmail.com" \
  build-date="2022-02-04"

COPY --from=build /workdir/target/OnlineStore_ohiji_backEnd-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 80

CMD ["java", "-jar", "app.jar"]