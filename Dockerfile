## 建立 maven 環境並打包
# FROM 用來引入一個(己完成的) basic image(映像)
# 可以在 basic image 注入自己的 src code，在其之上建構 image
# AS 用來區分執行的階段，as build 表示為 docker 創建中的 build 階段
FROM maven:3.8.4-openjdk-11-slim AS build

# 建立 Docker 工作目錄
WORKDIR /workdir

# 複製檔案到 Docker 工作目錄
# 可省略 /workdir/
# . . 表示把根目錄中的檔案全部複製到工作目錄中
COPY . .

# RUN docker engine command 將會在你的 image 中執行一個進程，並且將結果保留在你的 image 中。
# 運行 mvn package -Dmaven.test.skip(跳過test)
RUN ["mvn", "package", "-Dmaven.test.skip"]

## 建立運行容器
FROM openjdk:11.0.14-jre-slim-buster AS prod

## 簽名檔
# LABEL 用來設定參數，也可以當作簽名檔(可用 Docker inspect 查看)
LABEL \
  version="1.0" \
  maintainer="yuus271828@gmail.com" \
  build-date="2022-02-04"

COPY --from=build /workdir/target/OnlineStore_ohiji_backEnd-0.0.1-SNAPSHOT.jar app.jar

# 輸出的 ports
EXPOSE 80

# 設定在 CMD 指令中的指令，在構建 image 時不會被執行
# CMD 中的設定，表示當容器在運行 image 時，要運行的指令(構建完 image，再執行的指令)
CMD ["java", "-jar", "app.jar"]