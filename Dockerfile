FROM adoptopenjdk/openjdk11

COPY ./build/libs/yogiyo-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["nohup", "java", "-jar", "app.jar"]
