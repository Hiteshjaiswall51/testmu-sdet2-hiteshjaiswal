# testmu-sdet2-hiteshjaiswal

Hybrid automation framework built for UI, API, and integration testing using Selenium, RestAssured, and TestNG.

The goal of this framework is to create something maintainable and scalable instead of only writing test scripts. The framework is structured in a way where UI, API, reporting, utilities, and integrations can grow independently without tightly coupling everything together.

---

# Tech Stack

- Java
- Maven
- Selenium WebDriver
- RestAssured
- TestNG
- ExtentReports
- Log4j

---

# Framework Structure

```text
src
├── main
│   ├── base
│   ├── helper
│   ├── constants
│   ├── listener
│   ├── pages
│   ├── report
│   └── utils
│
├── resources
│   ├── testdata
│   └── project.properties
│
├── test
│   ├── ui
│   ├── api
│   └── integration
```

---

# Framework Design

The framework is divided into 3 major layers:

## UI Layer
- Built using Page Object Model
- Reusable page methods
- Selenium helper methods abstracted from tests
- Cross-browser execution support planned

## API Layer
- RestAssured based API automation
- Service layer structure for reusable API methods
- Response validation and schema validation support
- Authentication and CRUD flow coverage

## Integration Layer
- Combines API + UI validations
- Example:
    - Create data through API
    - Validate the same data from UI

This reduces UI dependency and speeds up execution.

---

# Features Implemented

- Page Object Model structure
- Selenium helper utilities
- Retry mechanism
- Config driven execution
- Extent report integration
- Screenshot support on failure
- API framework setup
- Base classes for reusable setup
- Cross-suite execution support using TestNG XML

---

# Reporting

ExtentReports is used for execution reporting.

Current report features:
- Pass/fail status
- Logs attached to report
- Screenshot on failure
- Execution timestamps

Reports are generated under:

/Reports
```

---

# Running the Framework

Run using Maven:

mvn clean test -DsuiteXmlFile=testng.xml
```

Example:

mvn clean test -DsuiteXmlFile=ui-suite.xml
```

---

# Planned Improvements

Some improvements still planned for the framework:

- Selenium Grid support
- Docker setup
- Parallel execution
- GitHub Actions pipeline
- RemoteWebDriver execution
- Better reporting dashboards
- AI-assisted flaky test analysis

---

# Design Decisions

## Why POM?
To keep locators and page actions separated from test logic.

## Why helper classes?
To avoid duplicated Selenium code across tests.

## Why combined UI + API framework?
Keeping everything in one repo makes integration testing and CI/CD easier to manage.

---

# Future Scope

If extended further, the framework can support:
- Full CI/CD execution
- Cloud execution
- Dashboard analytics
- Distributed execution
- Performance testing integration

---

# Author

Hitesh Jaiswal
```


```
steps to run : in future we can variablise it but for ease we will manage everything from project.properties
1) dockerfile is defined in the root
2) build the dockerfile via this command docker build -t hiteshjaiswalsdet/framework:testmudemo .
3) after build push this file docker push hiteshjaiswalsdet/framework:testmudemo
4) the gitaction has a workflow written in .github which will run this file docker run hiteshjaiswalsdet/framework:testmudemo .
5) all the steps in the file will be executed

```

clone repo
mvn clean test  -DsuiteXmlFile=testrum.xml
report will be generated 

