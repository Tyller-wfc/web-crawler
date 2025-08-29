# ========== 构建阶段 ==========
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# 拷贝 pom.xml 和源码
COPY pom.xml .
COPY src ./src

# 打包 Spring Boot 应用
RUN mvn clean package -DskipTests

# ========== 运行阶段 ==========
FROM eclipse-temurin:17-jdk

WORKDIR /app

# 安装依赖（Ubuntu 24.04 用 libasound2t64 替代 libasound2）
RUN apt-get update && apt-get install -y \
    wget gnupg unzip curl ca-certificates fonts-liberation libappindicator3-1 \
    libasound2t64 libatk-bridge2.0-0 libatk1.0-0 libcups2 libdbus-1-3 \
    libgdk-pixbuf2.0-0 libnspr4 libnss3 libx11-xcb1 libxcomposite1 \
    libxdamage1 libxrandr2 xdg-utils \
    && rm -rf /var/lib/apt/lists/*

# 安装 Google Chrome 稳定版
RUN wget -q https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb \
    && apt-get install -y ./google-chrome-stable_current_amd64.deb \
    && rm google-chrome-stable_current_amd64.deb
# 将 jar 拷贝到运行容器
COPY --from=build /app/target/*.jar app.jar

# 暴露 Spring Boot 默认端口
EXPOSE 8080

# 运行 Spring Boot 应用
ENTRYPOINT ["java", "-jar", "app.jar"]
