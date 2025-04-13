# Read Me: Currency Converter Billing API

### Reference Documentation

A Spring Boot application that calculates the final bill amount by applying discounts and converting currencies using an external API.

---

### Tech Stack

- Java 21
- Spring Boot 3.3.1
- Spring Security (Basic Auth)
- Maven
- REST APIs
- JUnit 5

---

### Authentication

This API is secured with **Basic Authentication** using in-memory credentials:

- **Username:** `admin`
- **Password:** `admin`

---

## How to Run

bash
git clone https://github.com/your-username/currencyconverter.git
cd currencyconverter
mvn spring-boot:run

## Test the API using Postman
A ready-to-use Postman collection is included in the root directory:
File: CurrencyConverter.postman_collection.json


## How to Use
Open Postman

Click Import and select CurrencyConverter.postman_collection.json
Set Basic Auth:

Username: admin
Password: admin

Run the request: POST /api/calculate

#### Sample Request
curl --location --request POST 'http://localhost:8080/api/bill/calculate' \
--header 'Content-Type: application/json' \
--header 'Accept: application/json' \
--header 'Authorization: Basic YWRtaW46YWRtaW4=' \
--data '{
"userType": "EMPLOYEE",
"customerSince": "2022-01-01",
"originalCurrency": "USD",
"targetCurrency": "EUR",
"items": [
{
"name": "TV",
"category": "OTHER",
"price": 1000
},
{
"name": "Bread",
"category": "GROCERY",
"price": 100
}
]
}'

#### Sample Response

{
"originalAmount": 1100,
"payableAmount": 657.02,
"currency": "EUR"
}


## Build & Test
Build the project: mvn clean install
Run all test cases: mvn test