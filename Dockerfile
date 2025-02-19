FROM amazoncorretto:21
MAINTAINER adx.ambrygen.com
#RUN addgroup spring; adduser  --ingroup spring --disabled-password spring
#USER spring
EXPOSE 8080
WORKDIR /app
COPY ./target/adx-api*SNAPSHOT.jar /app/adx-api.jar
CMD ["java", "-jar", "/app/adx-api.jar"]
