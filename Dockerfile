FROM amazoncorretto:18

RUN mkdir /app

WORKDIR /app

ADD ./api/target/shopping-cart-api-1.0.0-SNAPSHOT.jar /app

EXPOSE 8080

CMD ["java", "-jar", "shopping-cart-api-1.0.0-SNAPSHOT.jar"]
#ENTRYPOINT ["java", "-jar", "shopping-cart-api-1.0.0-SNAPSHOT.jar"]
#CMD java -jar shopping-cart-api-1.0.0-SNAPSHOT.jar