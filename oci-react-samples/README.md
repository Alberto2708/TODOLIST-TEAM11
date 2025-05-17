# MyTodoList Spring Boot + React Application

## Prerequisites

- Java 11+
- Maven
- Node.js & npm
- Oracle Database (Autonomous)
- Docker Desktop

## 1. Backend Setup

### Step 1: Download and Prepare the Wallet

- Download the Oracle Wallet from your Autonomous Database.
- Unzip the contents of the wallet.
- Move the unzipped wallet directory into the backend folder:
```sh
oci-react-samples/MtdrSpring/backend/<WALLET_FOLDER>
```


### Step 2: Configure Database Connection

- Edit [`application.properties`](MtdrSpring/backend/src/main/resources/application.properties) to update the database credentials. (URL and password)

- Set the correct `TNS_ADMIN` path to match the folder where you placed the wallet.

### Step 3: Install Dependencies

- Before running the app, perform a clean install:
```sh
cd oci-react-samples/MtdrSpring/backend
mvn clean install
```

### Step 4: Open Docker Desktop

- Before running the app, make sure Docker is running.



### Step 5: Run the service
- You must stay in the /backend directory. Otherwise, it won't work.
```sh
mvn spring-boot:run
```

- The backend will be available at: http://localhost:8081