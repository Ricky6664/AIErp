# ===== 阶段1：构建 =====
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build

# 先复制pom.xml，利用Docker层缓存加速依赖下载
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 再复制源码并构建
COPY src ./src
RUN mvn clean package -DskipTests -B

# JAR分层：解压Spring Boot分层jar
RUN java -Djarmode=layertools -jar target/*.jar extract --destination target/extracted

# ===== 阶段2：运行 =====
FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="erp-team"
LABEL description="ERP Backend Service"
WORKDIR /app

# 安装wget用于健康检查
RUN apk add --no-cache wget

# 时区设置
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

# 安全：创建非root用户（UID/GID=1001）
RUN addgroup -S appgroup && adduser -S appuser -G appgroup -u 1001

# 按顺序逐层COPY，不常变动的依赖层在前，频繁变动的应用层在后
COPY --from=builder /build/target/extracted/dependencies/ ./
COPY --from=builder /build/target/extracted/spring-boot-loader/ ./
COPY --from=builder /build/target/extracted/snapshot-dependencies/ ./
COPY --from=builder /build/target/extracted/application/ ./

# 设置文件权限并切换到非root用户
RUN chown -R appuser:appgroup /app
USER appuser

# 环境变量与JVM参数
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+HeapDumpOnOutOfMemoryError"
ENV SPRING_PROFILES_ACTIVE=prod
ENV TZ=Asia/Shanghai
ENV LANG=C.UTF-8

EXPOSE 8080

# 健康检查（start-period=60s给Spring Boot足够启动时间）
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget -q --spider http://localhost:8080/actuator/health || exit 1

# 使用sh -c形式以便环境变量展开，JarLauncher启动分层jar
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.JarLauncher --spring.profiles.active=$SPRING_PROFILES_ACTIVE"]
