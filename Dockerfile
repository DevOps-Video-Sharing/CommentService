# # Use a base image with JDK 17
# FROM openjdk:17-jdk-slim

# # Set the working directory
# WORKDIR /app

# # Copy the jar file
# COPY target/streaming-0.0.1-SNAPSHOT.jar comment-service.jar

# # Expose the port
# EXPOSE 8082

# # Run the application
# ENTRYPOINT ["java", "-jar", "comment-service.jar"]

FROM ubuntu:latest

# Cài đặt OpenJDK 17
RUN apt-get update && \
    apt-get install -y openjdk-17-jdk && \
    apt-get -y install net-tools && \
    rm -rf /var/lib/apt/lists/*

# Thiết lập thư mục làm việc
WORKDIR /app

# Sao chép file jar vào thư mục làm việc trong container
COPY target/commentService-0.0.1-SNAPSHOT.jar /app.jar

# Mở cổng 8080 để giao tiếp với container bên ngoài
EXPOSE 8080

# Lệnh chạy ứng dụng Java
CMD [ "java", "-jar", "/app.jar" ]