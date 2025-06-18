# QTrip API Testing

This project is a REST API test automation framework for the QTrip application using **Java**, **REST Assured**, **TestNG**, and **Gradle**.

## 📁 Project Structure
Qtrip-Api-Testing/
```
├── app/
│ └── src/
│ ├── main/
│ │ └── java/
│ │ └── qtrip/api/testing/
│ │ └── App.java
│ └── test/
│ └── java/
│ └── qtrip/api/testing/
│   ├── apiTestCase01.java
│   ├── apiTestCase02.java
│   ├── apiTestCase03.java
│   └── apiTestCase04.java
├── build.gradle
└── README.md
```
## 🛠 Tech Stack

- **Java 11+**
- **REST Assured 5.3.0**
- **TestNG 7.5**
- **Gradle 7.6.4**
- **org.json:json** for JSON handling

## 📦 Dependencies

All dependencies are managed via Gradle and declared in `build.gradle`.

```groovy
testImplementation 'org.testng:testng:7.5'
implementation 'com.google.guava:guava:32.1.3-jre'
testImplementation 'io.rest-assured:rest-assured:5.3.0'
implementation 'org.json:json:20230618'
```
## 🚀 How to Run the Tests

Clone the project:
```
git clone https://github.com/Avdhoot-Kurhe/API_Testing.git
```
Navigate to project folder:
```
cd Qtrip-Api-Testing
```
Run tests using Gradle:
```
gradle test
```
### Expected Output:
Gradle will display test results in the terminal. If the testLogging block is enabled, detailed output of test execution will be visible.
