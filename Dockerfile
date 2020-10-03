FROM openjdk:8-alpine

COPY target/uberjar/book-ll.jar /book-ll/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/book-ll/app.jar"]
