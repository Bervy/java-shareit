FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar app.jar
#RUN apk add --no-cache tzdata
#ENV TZ Asia/Almaty
ENTRYPOINT ["java" , "-jar", "/app.jar"]