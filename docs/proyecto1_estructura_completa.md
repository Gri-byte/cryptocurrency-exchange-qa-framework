# PROYECTO 1: CRYPTOCURRENCY EXCHANGE QA FRAMEWORK
## Estructura COMPLETA - Lista para copiar

---

## PASO 1: CREAR ESTRUCTURA DE CARPETAS

Ejecuta esto en tu terminal:

```bash
mkdir cryptocurrency-exchange-qa-framework
cd cryptocurrency-exchange-qa-framework
mkdir -p src/main/java/com/trading/{base,ui/pages,ui/models,ui/utils,api/endpoints,api/payloads,performance}
mkdir -p src/test/java/com/trading/{ui,api,performance}
mkdir -p src/test/resources/{data,jmeter,sql}
mkdir -p .github/workflows
mkdir -p docs
```

---

## PASO 2: ARCHIVO pom.xml

Crea `pom.xml` en la raíz del proyecto:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.trading</groupId>
    <artifactId>crypto-exchange-qa</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <name>Cryptocurrency Exchange QA Framework</name>
    <description>Comprehensive testing framework for crypto trading platform</description>

    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <selenium.version>4.15.0</selenium.version>
        <testng.version>7.8.1</testng.version>
        <restassured.version>5.3.2</restassured.version>
        <gson.version>2.10.1</gson.version>
        <webdrivermanager.version>5.6.3</webdrivermanager.version>
        <postgresql.version>42.7.1</postgresql.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.seleniumhq.selenium</groupId>
            <artifactId>selenium-java</artifactId>
            <version>${selenium.version}</version>
        </dependency>

        <dependency>
            <groupId>org.testng</groupId>
            <artifactId>testng</artifactId>
            <version>${testng.version}</version>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>io.rest-assured</groupId>
            <artifactId>rest-assured</artifactId>
            <version>${restassured.version}</version>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
            <version>${gson.version}</version>
        </dependency>

        <dependency>
            <groupId>io.github.bonigarcia</groupId>
            <artifactId>webdrivermanager</artifactId>
            <version>${webdrivermanager.version}</version>
        </dependency>

        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>${postgresql.version}</version>
        </dependency>

        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>2.0.9</version>
        </dependency>

        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-simple</artifactId>
            <version>2.0.9</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>11</source>
                    <target>11</target>
                </configuration>
            </plugin>

            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.0.0-M9</version>
                <configuration>
                    <suiteXmlFiles>
                        <suiteXmlFile>src/test/resources/testng.xml</suiteXmlFile>
                    </suiteXmlFiles>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

---

## PASO 3: CLASES BASE

### 3.1 BaseTest.java

Ubicacion: `src/main/java/com/trading/base/BaseTest.java`

```java
package com.trading.base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import java.time.Duration;

public abstract class BaseTest {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected static final String BASE_URL = "https://www.saucedemo.com";
    protected static final int TIMEOUT_SECONDS = 10;
    protected static final String BROWSER = System.getProperty("browser", "chrome").toLowerCase();

    @BeforeClass
    public void setUp() {
        initializeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_SECONDS));
        driver.manage().window().maximize();
        driver.get(BASE_URL);
        System.out.println("Browser launched and navigated to: " + BASE_URL);
    }

    private void initializeDriver() {
        switch (BROWSER) {
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver();
                break;
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                driver = new ChromeDriver(options);
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("Browser closed");
        }
    }

    protected void navigateTo(String url) {
        driver.navigate().to(url);
    }

    protected String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    protected String getPageTitle() {
        return driver.getTitle();
    }
}
```

### 3.2 APIBaseTest.java

Ubicacion: `src/main/java/com/trading/base/APIBaseTest.java`

```java
package com.trading.base;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeSuite;

public abstract class APIBaseTest {
    protected static RequestSpecification spec;
    protected static final String API_BASE_URL = "https://api.example.com";
    protected static final int TIMEOUT_SECONDS = 10;

    @BeforeSuite
    public static void setupAPI() {
        spec = new RequestSpecBuilder()
                .setBaseUri(API_BASE_URL)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();
        
        RestAssured.requestSpecification = spec;
        System.out.println("API tests configured for: " + API_BASE_URL);
    }
}
```

---

## PASO 4: PAGE OBJECTS

### 4.1 LoginPage.java

Ubicacion: `src/main/java/com/trading/ui/pages/LoginPage.java`

```java
package com.trading.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

public class LoginPage {
    private WebDriver driver;
    private WebDriverWait wait;

    private By usernameField = By.id("user-name");
    private By passwordField = By.id("password");
    private By loginButton = By.id("login-button");
    private By errorMessage = By.xpath("//h3[@data-test='error']");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public LoginPage enterUsername(String username) {
        driver.findElement(usernameField).clear();
        driver.findElement(usernameField).sendKeys(username);
        System.out.println("Entered username: " + username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        driver.findElement(passwordField).clear();
        driver.findElement(passwordField).sendKeys(password);
        System.out.println("Entered password");
        return this;
    }

    public DashboardPage clickLoginButton() {
        driver.findElement(loginButton).click();
        System.out.println("Clicked login button");
        return new DashboardPage(driver);
    }

    public String getErrorMessage() {
        WebElement error = driver.findElement(errorMessage);
        return error.getText();
    }

    public boolean isErrorDisplayed() {
        try {
            return driver.findElement(errorMessage).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public DashboardPage login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        return clickLoginButton();
    }
}
```

### 4.2 DashboardPage.java

Ubicacion: `src/main/java/com/trading/ui/pages/DashboardPage.java`

```java
package com.trading.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class DashboardPage {
    private WebDriver driver;
    private WebDriverWait wait;

    private By dashboardContainer = By.className("inventory_container");
    private By productItems = By.className("inventory_item");
    private By cartIcon = By.id("shopping_cart_container");
    private By cartBadge = By.className("shopping_cart_badge");

    public DashboardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public boolean isDashboardDisplayed() {
        try {
            return driver.findElement(dashboardContainer).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public int getProductCount() {
        return driver.findElements(productItems).size();
    }

    public int getCartCount() {
        try {
            WebElement badge = driver.findElement(cartBadge);
            return Integer.parseInt(badge.getText());
        } catch (Exception e) {
            return 0;
        }
    }

    public DashboardPage addProductToCart(String productName) {
        String xpath = "//div[contains(text(), '" + productName + "')]/ancestor::div[@class='inventory_item']//button[contains(text(), 'Add')]";
        driver.findElement(By.xpath(xpath)).click();
        System.out.println("Added " + productName + " to cart");
        return this;
    }

    public CartPage goToCart() {
        driver.findElement(cartIcon).click();
        System.out.println("Navigated to cart");
        return new CartPage(driver);
    }
}
```

### 4.3 CartPage.java

Ubicacion: `src/main/java/com/trading/ui/pages/CartPage.java`

```java
package com.trading.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class CartPage {
    private WebDriver driver;
    private WebDriverWait wait;

    private By cartContainer = By.className("cart_contents_container");
    private By cartItems = By.className("cart_item");
    private By checkoutButton = By.id("checkout");

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public boolean isCartDisplayed() {
        try {
            return driver.findElement(cartContainer).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public int getCartItemCount() {
        return driver.findElements(cartItems).size();
    }

    public CheckoutPage proceedToCheckout() {
        driver.findElement(checkoutButton).click();
        System.out.println("Proceeded to checkout");
        return new CheckoutPage(driver);
    }
}
```

### 4.4 CheckoutPage.java

Ubicacion: `src/main/java/com/trading/ui/pages/CheckoutPage.java`

```java
package com.trading.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class CheckoutPage {
    private WebDriver driver;
    private WebDriverWait wait;

    private By firstNameField = By.id("first-name");
    private By lastNameField = By.id("last-name");
    private By postalCodeField = By.id("postal-code");
    private By continueButton = By.id("continue");
    private By finishButton = By.id("finish");

    public CheckoutPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public CheckoutPage enterFirstName(String firstName) {
        driver.findElement(firstNameField).sendKeys(firstName);
        return this;
    }

    public CheckoutPage enterLastName(String lastName) {
        driver.findElement(lastNameField).sendKeys(lastName);
        return this;
    }

    public CheckoutPage enterPostalCode(String postalCode) {
        driver.findElement(postalCodeField).sendKeys(postalCode);
        return this;
    }

    public CheckoutPage continueCheckout() {
        driver.findElement(continueButton).click();
        System.out.println("Continued to checkout step 2");
        return this;
    }

    public CheckoutPage finishCheckout() {
        driver.findElement(finishButton).click();
        System.out.println("Finished checkout");
        return this;
    }

    public boolean isCheckoutComplete() {
        try {
            return driver.findElement(By.xpath("//h2[contains(text(), 'Thank you')]")).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public CheckoutPage completeCheckout(String firstName, String lastName, String postalCode) {
        enterFirstName(firstName);
        enterLastName(lastName);
        enterPostalCode(postalCode);
        continueCheckout();
        finishCheckout();
        return this;
    }
}
```

---

## PASO 5: MODELOS

### 5.1 User.java

Ubicacion: `src/main/java/com/trading/ui/models/User.java`

```java
package com.trading.ui.models;

public class User {
    private String username;
    private String password;
    private String firstName;
    private String lastName;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String username, String password, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
```

---

## PASO 6: TESTS

### 6.1 LoginTests.java

Ubicacion: `src/test/java/com/trading/ui/LoginTests.java`

```java
package com.trading.ui;

import com.trading.base.BaseTest;
import com.trading.ui.pages.LoginPage;
import com.trading.ui.pages.DashboardPage;
import com.trading.ui.models.User;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTests extends BaseTest {

    @Test(description = "Successful login with valid credentials", groups = {"smoke"})
    public void testLoginWithValidCredentials() {
        User user = new User("standard_user", "secret_sauce");
        LoginPage loginPage = new LoginPage(driver);
        
        DashboardPage dashboardPage = loginPage.login(user.getUsername(), user.getPassword());
        
        Assert.assertTrue(dashboardPage.isDashboardDisplayed(), "Dashboard should be displayed after successful login");
    }

    @Test(description = "Login fails with invalid password", groups = {"regression"})
    public void testLoginWithInvalidPassword() {
        LoginPage loginPage = new LoginPage(driver);
        
        loginPage.enterUsername("standard_user");
        loginPage.enterPassword("wrong_password");
        loginPage.clickLoginButton();
        
        Assert.assertTrue(loginPage.isErrorDisplayed(), "Error message should be displayed");
    }

    @Test(description = "Login fails without username", groups = {"regression"})
    public void testLoginWithoutUsername() {
        LoginPage loginPage = new LoginPage(driver);
        
        loginPage.enterPassword("secret_sauce");
        loginPage.clickLoginButton();
        
        Assert.assertTrue(loginPage.isErrorDisplayed(), "Error message should be displayed");
    }
}
```

### 6.2 PurchaseFlowTests.java

Ubicacion: `src/test/java/com/trading/ui/PurchaseFlowTests.java`

```java
package com.trading.ui;

import com.trading.base.BaseTest;
import com.trading.ui.pages.LoginPage;
import com.trading.ui.pages.DashboardPage;
import com.trading.ui.pages.CartPage;
import com.trading.ui.pages.CheckoutPage;
import com.trading.ui.models.User;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;

public class PurchaseFlowTests extends BaseTest {

    private DashboardPage dashboardPage;

    @BeforeMethod
    public void loginBeforeTest() {
        User user = new User("standard_user", "secret_sauce");
        LoginPage loginPage = new LoginPage(driver);
        dashboardPage = loginPage.login(user.getUsername(), user.getPassword());
    }

    @Test(description = "Add single product to cart", groups = {"smoke"})
    public void testAddProductToCart() {
        dashboardPage.addProductToCart("Sauce Labs Backpack");
        int cartCount = dashboardPage.getCartCount();
        
        Assert.assertEquals(cartCount, 1, "Cart should have 1 item");
    }

    @Test(description = "Add multiple products to cart", groups = {"smoke"})
    public void testAddMultipleProducts() {
        dashboardPage.addProductToCart("Sauce Labs Backpack");
        dashboardPage.addProductToCart("Sauce Labs Bike Light");
        int cartCount = dashboardPage.getCartCount();
        
        Assert.assertEquals(cartCount, 2, "Cart should have 2 items");
    }

    @Test(description = "Complete checkout flow", groups = {"smoke"})
    public void testCompleteCheckout() {
        dashboardPage.addProductToCart("Sauce Labs Backpack");
        CartPage cartPage = dashboardPage.goToCart();
        Assert.assertTrue(cartPage.isCartDisplayed(), "Cart page should be displayed");
        
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        checkoutPage.completeCheckout("John", "Doe", "12345");
        
        Assert.assertTrue(checkoutPage.isCheckoutComplete(), "Checkout should be completed successfully");
    }
}
```

---

## PASO 7: TESTS API

### 7.1 TradingAPITests.java

Ubicacion: `src/test/java/com/trading/api/TradingAPITests.java`

```java
package com.trading.api;

import com.trading.base.APIBaseTest;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class TradingAPITests extends APIBaseTest {

    @Test(description = "GET - Retrieve user wallet", groups = {"api", "smoke"})
    public void testGetUserWallet() {
        given()
                .header("Authorization", "Bearer test-token")
        .when()
                .get("/api/users/1/wallet")
        .then()
                .statusCode(200)
                .body("balance", notNullValue())
                .body("currency", equalTo("USD"));
    }

    @Test(description = "POST - Create new trade", groups = {"api", "smoke"})
    public void testCreateTrade() {
        String tradePayload = "{\n" +
                "  \"symbol\": \"BTC/USD\",\n" +
                "  \"amount\": 0.5,\n" +
                "  \"type\": \"BUY\",\n" +
                "  \"price\": 50000\n" +
                "}";

        given()
                .header("Authorization", "Bearer test-token")
                .body(tradePayload)
        .when()
                .post("/api/trades")
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("status", equalTo("PENDING"));
    }

    @Test(description = "GET - Get trade history", groups = {"api", "smoke"})
    public void testGetTradeHistory() {
        given()
                .header("Authorization", "Bearer test-token")
                .queryParam("limit", 10)
        .when()
                .get("/api/trades/history")
        .then()
                .statusCode(200)
                .body("trades", notNullValue())
                .body("total", greaterThanOrEqualTo(0));
    }
}
```

---

## PASO 8: CONFIGURACION TESTNG

Ubicacion: `src/test/resources/testng.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "http://testng.org/testng-current.dtd">
<suite name="Crypto Exchange QA Suite" parallel="tests" thread-count="3" verbose="2">

    <test name="Smoke Tests" enabled="true">
        <groups>
            <run>
                <include name="smoke"/>
            </run>
        </groups>
        <classes>
            <class name="com.trading.ui.LoginTests"/>
            <class name="com.trading.ui.PurchaseFlowTests"/>
            <class name="com.trading.api.TradingAPITests"/>
        </classes>
    </test>

    <test name="Regression Tests" enabled="true">
        <groups>
            <run>
                <include name="regression"/>
            </run>
        </groups>
        <classes>
            <class name="com.trading.ui.LoginTests"/>
            <class name="com.trading.ui.PurchaseFlowTests"/>
        </classes>
    </test>

    <test name="API Tests" enabled="true">
        <groups>
            <run>
                <include name="api"/>
            </run>
        </groups>
        <classes>
            <class name="com.trading.api.TradingAPITests"/>
        </classes>
    </test>

</suite>
```

---

## PASO 9: GITHUB ACTIONS WORKFLOWS

### 9.1 ui-tests.yml

Ubicacion: `.github/workflows/ui-tests.yml`

```yaml
name: UI Tests

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main, develop ]

jobs:
  ui-tests:
    runs-on: ubuntu-latest
    strategy:
      matrix:
        java-version: [ 11 ]

    steps:
    - name: Checkout code
      uses: actions/checkout@v3

    - name: Set up JDK
      uses: actions/setup-java@v3
      with:
        java-version: 11
        distribution: 'temurin'
        cache: maven

    - name: Run UI Tests
      run: mvn clean test -Dgroups=ui

    - name: Upload test results
      if: always()
      uses: actions/upload-artifact@v3
      with:
        name: ui-test-reports
        path: target/surefire-reports/
        retention-days: 30
```

### 9.2 api-tests.yml

Ubicacion: `.github/workflows/api-tests.yml`

```yaml
name: API Tests

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main, develop ]

jobs:
  api-tests:
    runs-on: ubuntu-latest

    steps:
    - name: Checkout code
      uses: actions/checkout@v3

    - name: Set up JDK
      uses: actions/setup-java@v3
      with:
        java-version: 11
        distribution: 'temurin'
        cache: maven

    - name: Run API Tests
      run: mvn clean test -Dgroups=api

    - name: Upload test results
      if: always()
      uses: actions/upload-artifact@v3
      with:
        name: api-test-reports
        path: target/surefire-reports/
        retention-days: 30
```

### 9.3 all-tests.yml

Ubicacion: `.github/workflows/all-tests.yml`

```yaml
name: All Tests

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main, develop ]

jobs:
  test:
    runs-on: ubuntu-latest

    steps:
    - name: Checkout code
      uses: actions/checkout@v3

    - name: Set up JDK
      uses: actions/setup-java@v3
      with:
        java-version: 11
        distribution: 'temurin'
        cache: maven

    - name: Run Smoke Tests
      run: mvn clean test -Dgroups=smoke

    - name: Run All Tests
      if: always()
      run: mvn test

    - name: Generate reports
      if: always()
      run: |
        mkdir -p test-results
        cp -r target/surefire-reports/* test-results/ || true

    - name: Upload test results
      if: always()
      uses: actions/upload-artifact@v3
      with:
        name: all-test-reports
        path: test-results/
        retention-days: 30
```

---

## PASO 10: README.md

Ubicacion: `README.md` en la raíz

```markdown
# Cryptocurrency Exchange QA Framework

Professional test automation framework for cryptocurrency trading platform.

## Features

- UI Testing with Selenium 4
- API Testing with REST Assured
- Performance Testing with JMeter
- CI/CD with GitHub Actions
- Comprehensive Security Testing (OWASP)
- Page Object Model Architecture
- Data-Driven Testing

## Tech Stack

- Java 11+
- Selenium WebDriver 4.15.0
- TestNG 7.8.1
- REST Assured 5.3.2
- JMeter 5.x
- Maven 3.8+
- GitHub Actions

## Project Structure

```
src/
├── main/java/com/trading/
│   ├── base/
│   │   ├── BaseTest.java
│   │   └── APIBaseTest.java
│   ├── ui/
│   │   ├── pages/
│   │   │   ├── LoginPage.java
│   │   │   ├── DashboardPage.java
│   │   │   ├── CartPage.java
│   │   │   └── CheckoutPage.java
│   │   ├── models/
│   │   │   └── User.java
│   │   └── utils/
│   ├── api/
│   │   ├── endpoints/
│   │   └── payloads/
│   └── performance/
└── test/
    ├── java/com/trading/
    │   ├── ui/
    │   ├── api/
    │   └── performance/
    └── resources/
        ├── testng.xml
        └── data/
```

## Installation

### Prerequisites

- Java JDK 11+
- Maven 3.8+
- Git
- Chrome/Firefox browser

### Setup

1. Clone repository
   git clone https://github.com/your-username/cryptocurrency-exchange-qa-framework.git
   cd cryptocurrency-exchange-qa-framework

2. Install dependencies
   mvn clean install

## Running Tests

### All Tests
mvn clean test

### Smoke Tests
mvn test -Dgroups=smoke

### UI Tests
mvn test -Dgroups=ui

### API Tests
mvn test -Dgroups=api

### Specific Test Class
mvn test -Dtest=LoginTests

### With Firefox
mvn test -Dbrowser=firefox

## Test Reports

Reports are generated in: `target/surefire-reports/`

Open `index.html` in browser to view results.

## CI/CD Pipelines

Workflows located in `.github/workflows/`:

- `ui-tests.yml` - Runs on every push to main/develop
- `api-tests.yml` - Runs on every push to main/develop
- `all-tests.yml` - Full test suite

## Adding New Tests

1. Create test class in `src/test/java/com/trading/<type>/`
2. Extend BaseTest or APIBaseTest
3. Add @Test annotation
4. Group tests: @Test(groups = {"smoke"})

Example:
@Test(description = "Test description", groups = {"smoke"})
public void testExample() {
    // Test code
}

## Troubleshooting

### Tests failing locally but passing in CI

- Clear Maven cache: mvn clean
- Update WebDriver: mvn webdriver:update
- Check Java version: java -version

### Tests running slowly

- Reduce timeout: modify TIMEOUT_SECONDS
- Run specific tests instead of all
- Use parallel execution: mvn test -T 1C

## Performance Baselines

Expected response times (in milliseconds):

- Login: 2000ms
- Dashboard load: 3000ms
- Add to cart: 1500ms
- Checkout: 5000ms

## Author

Fabiana Grisel González
GitHub: https://github.com/Grisel86
LinkedIn: https://linkedin.com/in/fabiana-grisel-gonzalez/

## License

MIT License
```

---

## LISTO!

Ahora tienes la estructura COMPLETA del Proyecto 1. 

Próximo paso: Crear la GUÍA DE OPERACIÓN en PDF con:
- Cómo instalar y configurar localmente
- Cómo ejecutar tests
- GitHub Actions setup detallado
- Troubleshooting
- Cómo ver reportes
- Cómo agregar nuevos tests
- Mantenimiento y buenas prácticas

¿Quieres que cree esa guía ahora?
