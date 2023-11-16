FROM adoptopenjdk/openjdk11

COPY ./build/libs/yogiyo-0.0.1-SNAPSHOT.jar app.jar
RUN chmod 755 app.jar

ENTRYPOINT ["sudo", "nohup", "java", "-jar", "app.jar"]
