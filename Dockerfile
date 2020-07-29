FROM openjdk:14-slim
COPY ./cue-cards-0.0.1-SNAPSHOT.jar /app/
WORKDIR /app
EXPOSE 8088
CMD ["java", "-jar", "cue-cards-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=prod"]
