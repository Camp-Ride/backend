FROM openjdk:17
CMD ["./gradlew", "clean", "build"]
COPY ./build/libs/campride-0.0.1-SNAPSHOT.jar /app.jar
COPY src/main/resources/firebase/campride-87f0d-firebase-adminsdk-mtnnn-a480ac194b.json /app/firebase/
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar","/app.jar"]
