FROM openjdk:11
ARG JAR_FILE=build/libs/pharmacy-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} ./pharmacy-0.0.1-SNAPSHOT.jar
ENV TZ=Asia/Seoul
ENTRYPOINT ["java", "-jar", "./pharmacy-0.0.1-SNAPSHOT.jar"]