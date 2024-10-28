FROM amazoncorretto:17
LABEL authors="shakik"

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} application.jar

CMD apt-get update -y

ENTRYPOINT sleep 150 && java -Xmx2048M -jar /application.jar
