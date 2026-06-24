package com.trading.base;

import com.trading.ui.pages.DashboardPage;
import com.trading.ui.pages.LoginPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseTest {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected static final String BASE_URL = "https://www.saucedemo.com";
    protected static final int TIMEOUT_SECONDS = 30;
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
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));
        switch (BROWSER) {
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver();
                break;
            default:
                ChromeOptions options = new ChromeOptions();
                // Allow CI to supply the Chrome binary path via -Dchrome.binary
                String chromeBinary = System.getProperty("chrome.binary");
                if (chromeBinary != null && !chromeBinary.isEmpty()) {
                    options.setBinary(chromeBinary);
                }
                Map<String, Object> prefs = new HashMap<>();
                prefs.put("credentials_enable_service", false);
                prefs.put("profile.password_manager_enabled", false);
                options.setExperimentalOption("prefs", prefs);
                options.addArguments("--disable-features=PasswordManagerEnabled");
                if (headless) {
                    options.addArguments("--headless=new");
                    options.addArguments("--no-sandbox");
                    options.addArguments("--disable-dev-shm-usage");
                    options.addArguments("--disable-gpu");
                    options.addArguments("--window-size=1920,1080");
                }
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

    /**
     * Resets browser to a clean login page state by clearing saucedemo localStorage,
     * navigating to about:blank to interrupt any in-flight React redirects, then loading BASE_URL fresh.
     */
    protected void resetToLoginPage() {
        if (driver.getCurrentUrl().contains("saucedemo.com")) {
            ((JavascriptExecutor) driver).executeScript("window.localStorage.clear();");
        }
        driver.get("about:blank");
        driver.manage().deleteAllCookies();
        driver.get(BASE_URL);
    }

    /**
     * Resets to the login page and logs in as the given user. Retries up to maxAttempts times
     * to handle the intermittent first-run React SPA initialization race where the login button
     * click triggers a native form submit instead of React's handler.
     */
    protected DashboardPage loginWithRetry(String username, String password, int maxAttempts) {
        DashboardPage dashboardPage = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            resetToLoginPage();
            dashboardPage = new LoginPage(driver).login(username, password);
            if (dashboardPage.isDashboardDisplayed()) {
                return dashboardPage;
            }
            System.out.println("Login attempt " + attempt + " failed (URL: " + driver.getCurrentUrl() + "), retrying...");
        }
        throw new RuntimeException("Dashboard not displayed after " + maxAttempts + " login attempts. URL: " + driver.getCurrentUrl());
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
