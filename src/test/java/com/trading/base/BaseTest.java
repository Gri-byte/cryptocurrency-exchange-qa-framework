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

public class BaseTest {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected static final String BASE_URL = "https://www.saucedemo.com";
    protected static final int TIMEOUT_SECONDS = 30;
    protected static final String BROWSER = System.getProperty("browser", "brave").toLowerCase();

    private static final String[] BRAVE_DEFAULT_PATHS = {
            "C:\\Program Files\\BraveSoftware\\Brave-Browser\\Application\\brave.exe",
            "C:\\Program Files (x86)\\BraveSoftware\\Brave-Browser\\Application\\brave.exe",
            System.getProperty("user.home") + "\\AppData\\Local\\BraveSoftware\\Brave-Browser\\Application\\brave.exe"
    };

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        initializeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_SECONDS));
        driver.manage().window().maximize();
        driver.get(BASE_URL);
        System.out.println("Browser launched and navigated to: " + BASE_URL);
    }

    private void initializeDriver() {
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));
        String chromeBinary = System.getProperty("chrome.binary");
        String chromeDriver = System.getProperty("webdriver.chrome.driver");
        System.out.println("[BaseTest] headless=" + headless);
        System.out.println("[BaseTest] chrome.binary=" + chromeBinary);
        System.out.println("[BaseTest] webdriver.chrome.driver=" + chromeDriver);

        switch (BROWSER) {
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver();
                break;
            case "brave":
            default:
                String resolvedBinary = (chromeBinary != null && !chromeBinary.isEmpty()) ? chromeBinary : null;
                if (resolvedBinary == null && BROWSER.equals("brave")) {
                    resolvedBinary = findBraveBinary();
                    if (resolvedBinary == null) {
                        System.out.println("[BaseTest] Brave binary not found in default locations; "
                                + "falling back to system Chrome. Pass -Dchrome.binary=<path> to point at Brave explicitly.");
                    }
                }

                // Resolve a chromedriver build whose major version matches the browser binary we are
                // actually about to launch, rather than whatever "stable Chrome" WebDriverManager
                // guesses at by default. ChromeDriver enforces major-version compatibility with the
                // browser it drives, so a stale cached driver (e.g. major 151) paired with a browser
                // that has since updated (e.g. major 152) fails session creation outright.
                if (chromeDriver != null && !chromeDriver.isEmpty()) {
                    System.out.println("[BaseTest] Using explicit chromedriver at " + chromeDriver
                            + " (skipping WebDriverManager auto-resolution)");
                } else if (resolvedBinary != null) {
                    String majorVersion = detectBrowserMajorVersion(resolvedBinary);
                    if (majorVersion != null) {
                        System.out.println("[BaseTest] Detected browser major version " + majorVersion
                                + " from " + resolvedBinary + "; resolving matching chromedriver");
                        WebDriverManager.chromedriver().browserVersion(majorVersion).setup();
                    } else {
                        System.out.println("[BaseTest] Could not detect version of " + resolvedBinary
                                + "; falling back to default chromedriver resolution");
                        WebDriverManager.chromedriver().setup();
                    }
                } else {
                    WebDriverManager.chromedriver().setup();
                }

                ChromeOptions options = new ChromeOptions();
                if (resolvedBinary != null) {
                    System.out.println("[BaseTest] Setting browser binary: " + resolvedBinary);
                    options.setBinary(resolvedBinary);
                }
                Map<String, Object> prefs = new HashMap<>();
                prefs.put("credentials_enable_service", false);
                prefs.put("profile.password_manager_enabled", false);
                options.setExperimentalOption("prefs", prefs);
                options.addArguments("--disable-features=PasswordManagerEnabled");
                if (headless) {
                    // Legacy headless, not --headless=new: CI's clicks on saucedemo.com (pure
                    // client-side React state, no network call) were consistently landing on an
                    // unresponsive element - reproduced neither locally nor via manual browser
                    // automation against the same site, only on CI's headless Chromium. --headless=new
                    // has had event-dispatch quirks in some Chromium releases that legacy headless
                    // doesn't share, so try it as the isolated variable.
                    options.addArguments("--headless");
                    options.addArguments("--no-sandbox");
                    options.addArguments("--disable-dev-shm-usage");
                    options.addArguments("--disable-gpu");
                    options.addArguments("--window-size=1920,1080");
                }
                System.out.println("[BaseTest] ChromeOptions args: " + options.asMap());
                try {
                    driver = new ChromeDriver(options);
                    System.out.println("[BaseTest] ChromeDriver created successfully");
                } catch (Exception e) {
                    System.err.println("[BaseTest] FAILED to create ChromeDriver: "
                            + e.getClass().getName() + ": " + e.getMessage());
                    e.printStackTrace();
                    throw e;
                }
        }
    }

    private String findBraveBinary() {
        for (String path : BRAVE_DEFAULT_PATHS) {
            if (new java.io.File(path).exists()) {
                return path;
            }
        }
        return null;
    }

    /**
     * Reads the browser binary's major version directly from its file metadata (via PowerShell),
     * without launching it. Deliberately does NOT shell out to "<binary> --version": Chromium-based
     * browsers are single-instance by default, so invoking the GUI executable while one is already
     * running just forwards the call to the existing process (printing "Opening in existing browser
     * session." instead of a version string), and forcing a fresh instance via a throwaway
     * --user-data-dir risks spawning an orphaned, hard-to-clean-up browser process.
     *
     * Only the major version is used: a rebranded Chromium build's own product version (e.g. Brave
     * reporting "152.1.94.117") does not correspond to any real Chrome-for-Testing release, but
     * major-version compatibility is what ChromeDriver actually enforces at session creation.
     */
    private String detectBrowserMajorVersion(String binaryPath) {
        try {
            String escapedPath = binaryPath.replace("'", "''");
            Process process = new ProcessBuilder(
                    "powershell", "-NoProfile", "-NonInteractive", "-Command",
                    "(Get-Item -LiteralPath '" + escapedPath + "').VersionInfo.ProductVersion")
                    .redirectErrorStream(true)
                    .start();
            String output;
            try (java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream()))) {
                output = reader.lines().collect(java.util.stream.Collectors.joining(" "));
            }
            process.waitFor(10, java.util.concurrent.TimeUnit.SECONDS);

            java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("(\\d+)\\.\\d+\\.\\d+\\.\\d+").matcher(output);
            if (matcher.find()) {
                return matcher.group(1);
            }
            System.out.println("[BaseTest] Unrecognized version metadata for " + binaryPath + ": " + output);
        } catch (Exception e) {
            System.out.println("[BaseTest] Version metadata lookup failed for " + binaryPath + ": " + e.getMessage());
        }
        return null;
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
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                resetToLoginPage();
                DashboardPage dashboardPage = new LoginPage(driver).login(username, password);
                if (dashboardPage.isDashboardDisplayed()) {
                    return dashboardPage;
                }
                System.out.println("Login attempt " + attempt + "/" + maxAttempts
                        + " failed (URL: " + driver.getCurrentUrl() + "), retrying...");
            } catch (Exception e) {
                System.out.println("Login attempt " + attempt + "/" + maxAttempts + " threw "
                        + e.getClass().getSimpleName() + ": " + e.getMessage() + ", retrying...");
            }
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