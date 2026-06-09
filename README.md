# Cryptocurrency Exchange QA Framework

Professional test automation framework for cryptocurrency trading platform with UI, API, and performance testing.

## Features

- UI Testing with Selenium 4 (Page Object Model)
- API Testing with REST Assured
- TestNG Test Framework
- CI/CD with GitHub Actions
- Comprehensive Security Testing
- Data-Driven Testing
- Parallel Test Execution
- Professional Test Reporting

## Tech Stack

Technology    | Version | Purpose
--------------|---------|----------
Java          | 11+     | Main language
Selenium      | 4.15.0  | UI automation
TestNG        | 7.8.1   | Test framework
REST Assured  | 5.3.2   | API testing
Maven         | 3.8+    | Build management
WebDriverMgr  | 5.6.3   | Driver management

## Project Structure

```
cryptocurrency-exchange-qa-framework/
├── src/
│   ├── main/java/com/trading/
│   │   ├── base/
│   │   │   ├── BaseTest.java
│   │   │   └── APIBaseTest.java
│   │   ├── ui/
│   │   │   ├── pages/
│   │   │   │   ├── LoginPage.java
│   │   │   │   ├── DashboardPage.java
│   │   │   │   ├── CartPage.java
│   │   │   │   └── CheckoutPage.java
│   │   │   └── models/
│   │   │       └── User.java
│   │   └── api/
│   └── test/
│       ├── java/com/trading/
│       │   ├── ui/
│       │   │   ├── LoginTests.java
│       │   │   └── PurchaseFlowTests.java
│       │   └── api/
│       │       └── TradingAPITests.java
│       └── resources/
│           └── testng.xml
├── .github/workflows/
│   ├── ui-tests.yml
│   ├── api-tests.yml
│   └── all-tests.yml
├── pom.xml
└── README.md
```

## Installation

### Prerequisites

- Java JDK 11+ (https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
- Maven 3.8+ (https://maven.apache.org/download.cgi)
- Git (https://git-scm.com/downloads)
- Chrome or Firefox browser

### Setup Steps

1. Clone the repository
   ```bash
   git clone https://github.com/your-username/cryptocurrency-exchange-qa-framework.git
   cd cryptocurrency-exchange-qa-framework
   ```

2. Verify installations
   ```bash
   java -version
   mvn -version
   git --version
   ```

3. Install Maven dependencies
   ```bash
   mvn clean install
   ```

## Running Tests

### All Tests
```bash
mvn clean test
```

### Smoke Tests Only (Fast)
```bash
mvn test -Dgroups=smoke
```

### Regression Tests
```bash
mvn test -Dgroups=regression
```

### API Tests Only
```bash
mvn test -Dgroups=api
```

### UI Tests Only
```bash
mvn test -Dgroups=ui
```

### Specific Test Class
```bash
mvn test -Dtest=LoginTests
```

### Specific Test Method
```bash
mvn test -Dtest=LoginTests#testLoginWithValidCredentials
```

### With Firefox Browser
```bash
mvn test -Dbrowser=firefox
```

### Run Tests in Parallel
```bash
mvn test -T 1C
```

## Test Reports

After running tests, HTML report is generated at:
```
target/surefire-reports/index.html
```

Open in your browser to view:
- Test summary (passed, failed, skipped)
- Test execution time
- Detailed test results
- Error messages and stack traces

## GitHub Actions CI/CD

Tests run automatically on:
- Every push to main or develop branch
- Every pull request to main or develop

### View Workflow Results

1. Go to your GitHub repository
2. Click on "Actions" tab
3. Select a workflow run
4. View detailed logs and test results
5. Download test reports from Artifacts

### Available Workflows

- **UI Tests** (.github/workflows/ui-tests.yml)
  - Runs UI and smoke tests
  - Executes on every push/PR

- **API Tests** (.github/workflows/api-tests.yml)
  - Runs API tests only
  - Executes on every push/PR

- **All Tests** (.github/workflows/all-tests.yml)
  - Runs complete test suite
  - Executes on every push/PR

## Adding New Tests

### Create New UI Test

1. Create test class in `src/test/java/com/trading/ui/`
2. Extend `BaseTest`
3. Add `@Test` annotation with group

Example:
```java
package com.trading.ui;

import com.trading.base.BaseTest;
import org.testng.annotations.Test;

public class NewFeatureTests extends BaseTest {
    
    @Test(description = "Test description", groups = {"smoke"})
    public void testFeature() {
        // Test code here
    }
}
```

### Create New API Test

1. Create test class in `src/test/java/com/trading/api/`
2. Extend `APIBaseTest`
3. Use REST Assured for assertions

Example:
```java
package com.trading.api;

import com.trading.base.APIBaseTest;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;

public class NewAPITests extends APIBaseTest {
    
    @Test(description = "Test API", groups = {"api"})
    public void testAPI() {
        given()
            .when()
            .get("/endpoint")
            .then()
            .statusCode(200);
    }
}
```

## Test Coverage

Current test coverage:

- LoginTests: 4 tests (login scenarios)
- PurchaseFlowTests: 5 tests (shopping flow)
- TradingAPITests: 7 tests (API endpoints)

Total: 16 tests covering smoke and regression scenarios

## Troubleshooting

### Tests fail locally but pass in CI

1. Clear Maven cache: `mvn clean`
2. Update dependencies: `mvn clean install -U`
3. Verify Java version: `java -version`

### WebDriver not found

WebDriverManager should auto-download. If it fails:
1. Check internet connection
2. Restart IDE
3. Run: `mvn clean install`

### Element not found exception

1. Verify element exists with Chrome DevTools (F12)
2. Check XPath/CSS selector
3. Add explicit waits in BaseTest
4. Increase TIMEOUT_SECONDS if needed

### Tests running slowly

1. Run specific test group instead of all: `mvn test -Dgroups=smoke`
2. Reduce timeout: `TIMEOUT_SECONDS = 5`
3. Close other applications
4. Use parallel execution: `mvn test -T 1C`

## Best Practices

1. Always use Explicit Waits (never implicit)
2. One assertion per test when possible
3. Descriptive test names (testLoginWithValidCredentials)
4. Use Page Object Model for UI tests
5. Keep test data in separate methods
6. Log important steps
7. Clean up resources in @AfterClass
8. Use meaningful commit messages

Example Commit Messages:
- "Add: Login tests with valid credentials"
- "Fix: Flaky dashboard test"
- "Update: Selenium to 4.20"
- "Refactor: DashboardPage methods"

## Performance Baselines

Expected response times:

| Operation      | Expected Time |
|---|---|
| Login          | 2-3 seconds   |
| Dashboard load | 3-4 seconds   |
| Add to cart    | 1-2 seconds   |
| Checkout       | 5-6 seconds   |

## Contributing

1. Create a new branch: `git checkout -b feature/your-feature`
2. Make changes and test locally
3. Commit changes: `git commit -m "Add: description"`
4. Push to branch: `git push origin feature/your-feature`
5. Create Pull Request

## Author

Fabiana Grisel González
- GitHub: https://github.com/Grisel86
- LinkedIn: https://linkedin.com/in/fabiana-grisel-gonzalez/

## License

MIT License - feel free to use this framework in your projects

## Support

For issues or questions:
1. Check existing GitHub Issues
2. Review troubleshooting section above
3. Create new GitHub Issue with:
   - Test class name
   - Error message
   - Steps to reproduce
   - Screenshots if applicable

## Changelog

### Version 1.0.0
- Initial release
- UI testing with Selenium
- API testing with REST Assured
- GitHub Actions CI/CD
- 16 comprehensive tests
- Full documentation

---

**Happy Testing!**
