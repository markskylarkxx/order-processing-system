# Order Processing System

## Overview

This is a microservices-based **Order Processing System** built with **Spring Boot** for the backend and **Angular** for the frontend. The project demonstrates how to build a modern, scalable, and decoupled architecture using:

-Order Service** (Spring Boot, gRPC client)
-Inventory Service** (Spring Boot, gRPC server)
-gRPC for inter-service communication
-WebSocket (STOMP over SockJS)** for real-time updates to the frontend
-Angular for a user-friendly UI
-Docker Compose for orchestrating and running the entire stack

## Architecture

- Order Service  
  - Manages customer orders  
  - Communicates with Inventory Service over gRPC to validate stock  
  - Sends order notifications over WebSocket to notify clients of real-time updates  

- Inventory Service
  - Provides stock data via gRPC  
  - Updates stock levels  

- Frontend (Angular)
  - Lets users create orders and see order history  
  - Subscribes to real-time order notifications via WebSocket

  Docker Compose 
  - Spins up the Order Service, Inventory Service, and Frontend in a single command
  - Makes local development and testing easy

## Features

 Create new orders  
 Validate stock in real-time via gRPC  
 Broadcast order events to connected clients over WebSocket  
 Angular client with product selection and order list  
 Clean, modular microservices architecture
 Docker Compose for simplified orchestration  


## Technologies

- Java 17
- Spring Boot 3
- gRPC
- WebSocket (STOMP, SockJS)
- Angular 17
- Docker (optional)
- Maven

## How to run (without docker compose)

1. **Backend**
   - Build with Maven:
     ```bash
     mvn clean install
     ```
   - Run Inventory Service
  
     cd inventory-service
     mvn spring-boot:run
     ```
   - Run Order Service
     
     cd order-service
     mvn spring-boot:run
     ```
2. **Frontend**
   - Install dependencies:
     npm install
     ```
   - Start Angular:
     ng serve
     ```
   - Access via [http://localhost:4200](http://localhost:4200)
  
With Docker Compose

1. Build the images:
   docker-compose build
2. Start the services 
   docker-compose up
3. The Angular app will be available at http://localhost/
The Order and Inventory services will be available on their respective containers, with gRPC and WebSocket working internally.

## WebSocket Testing

- When a new order is created, the Angular app will immediately receive an update via the WebSocket connection (`/ws-orders`).
- This ensures that order lists are refreshed in real time without reloading the page.




---

