# Shopping cart microservice

## Prerequisites

```bash
docker run -d --name pg-shopping-cart -e POSTGRES_USER=dbuser -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=shopping-cart -p 5432:5432 postgres:13
```

## Build and run commands
```bash
mvn clean package
cd api/target
java -jar shopping-cart-api-1.0.0-SNAPSHOT.jar
```
Available at: localhost:8080/v1/shopping-cart

## Run in IntelliJ IDEA
Add new Run configuration and select the Application type. In the next step, select the module api and for the main class com.kumuluz.ee.EeApplication.

Available at: localhost:8080/v1/shopping-cart

## Docker commands
```bash
docker build -t shopping-cart .   
docker images
docker run shopping-cart    
docker tag shopping-cart anzeo/shopping-cart   
docker push anzeo/shopping-cart
docker ps
```

```bash
docker network create primerjalnik
docker run -d --name pg-shopping-cart -e POSTGRES_USER=dbuser -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=shopping-cart -p 5432:5432 --network primerjalnik postgres:13
docker inspect pg-shopping-cart
docker run -p 8080:8080 --network primerjalnik -e KUMULUZEE_DATASOURCES0_CONNECTIONURL=jdbc:postgresql://pg-shopping-cart:5432/shopping-cart anzeo/shopping-cart
```

## Kubernetes
```bash
kubectl version
kubectl --help
kubectl get nodes
kubectl create -f deployment.yaml 
kubectl apply -f deployment.yaml
kubectl get services 
kubectl get deployments
kubectl get pods
kubectl logs shopping-cart-deployment-6f59c5d96c-rjz46
kubectl delete pod shopping-cart-deployment-6f59c5d96c-rjz46
```