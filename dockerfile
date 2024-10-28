FROM openjdk:17
ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
CMD ["./gradlew", "clean", "build"]
COPY ./build/libs/campride-0.0.1-SNAPSHOT.jar /app.jar
COPY src/main/resources/firebase/campride-87f0d-firebase-adminsdk-mtnnn-a480ac194b.json /app/firebase/
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar","/app.jar"]
