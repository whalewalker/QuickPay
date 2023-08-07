# QuickPay
QuickPay is a digital wallet system designed to facilitate quick and convenient financial transactions. It allows users to store their money digitally and perform various financial operations such as making payments, transferring funds, checking balances, and more.
***The API documentation is hosted [here](https://documenter.getpostman.com/view/18385063/2s9XxySZiY)***


## Setting up the Spring Boot Application with Gradle

This is a guide to setting up the Spring Boot application with Maven.

## Prerequisites

- Java 17 or higher
- Sprinboot 3.1.2 or higher
- Gradle 5.6 or higher
- MySQL 5.6 or higher

## Installation

1. Clone this repository to your local machine:

    ```bash
    git clone https://github.com/whalewalker/QuickPay
    ```

2. Create MySQL database

   ```bash
   mysql> create database quick_pay
   ```

3. Configure database username and password

     ```properties
       # src/main/resources/application.properties
      spring.datasource.url=jdbc:mysql://localhost:3306/quick_pay
      spring.datasource.username=<YOUR_DB_USERNAME>
      spring.datasource.password=<YOUR_DB_PASSWORD>
     ```
   
4.  Navigate to the root directory of the project.
    ```bash
    cd project_directory/
    ```

5. Build the project using Gradle.
   ```bash
   mvn clean 
    ```
6. Run the project 
   ```bash
   mvn spring-boot:run
    ```
7. The Spring Boot application should start running and you should be able to access it at http://localhost:8080

## Functional requirement
1. User should be able to sign up with their name, email, password and bio
2. An account number should be generated for the user.
3. Their account must be authenticated before they can perform any action.
4. They should be able to perform local fund transfers.
5. They should be able to perform a local name enquiry on another customer’s
wallet
6. They should be able to check their balances.
7. They should be able to view their transactions

## Non-Functional Requirements
The following non-functional requirements must be met:
- **Availability**: The application must be highly available, with minimal downtime and interruptions in service.
- **Performance**: The application must be optimized for low latency, with fast response times to user requests.

