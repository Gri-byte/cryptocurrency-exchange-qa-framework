package com.trading.ui;

import com.trading.base.BaseTest;
import com.trading.ui.pages.LoginPage;
import com.trading.ui.pages.DashboardPage;
import com.trading.ui.models.User;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LoginTests extends BaseTest {

    private static final int STEP_PAUSE_MS    = 1000;  // pausa entre pasos
    private static final int ACTION_PAUSE_MS  = 1500;  // pausa tras una acción
    private static final int RESULT_PAUSE_MS  = 2500;  // pausa para ver el resultado

    @BeforeMethod
    public void navigateToLoginPage() {
        resetToLoginPage();
        System.out.println("Setup: Navigated to login page");
        sleep(STEP_PAUSE_MS);
    }

    @Test(description = "Successful login with valid credentials", groups = {"smoke"})
    public void testLoginWithValidCredentials() {
        System.out.println(">>> INICIO: testLoginWithValidCredentials");
        sleep(STEP_PAUSE_MS);

        User user = new User("standard_user", "secret_sauce");
        LoginPage loginPage = new LoginPage(driver);
        System.out.println("  -> Ingresando credenciales válidas...");
        sleep(ACTION_PAUSE_MS);

        DashboardPage dashboardPage = loginPage.login(user.getUsername(), user.getPassword());
        System.out.println("  -> Login ejecutado. Verificando dashboard...");
        sleep(RESULT_PAUSE_MS);

        Assert.assertTrue(dashboardPage.isDashboardDisplayed(),
                "Dashboard should be displayed after successful login");
        sleep(RESULT_PAUSE_MS);

        System.out.println("TEST PASSED: User successfully logged in");
        sleep(STEP_PAUSE_MS);
    }

    @Test(description = "Login fails with invalid password", groups = {"regression"})
    public void testLoginWithInvalidPassword() {
        System.out.println(">>> INICIO: testLoginWithInvalidPassword");
        sleep(STEP_PAUSE_MS);

        LoginPage loginPage = new LoginPage(driver);

        System.out.println("  -> Ingresando username...");
        loginPage.enterUsername("standard_user");
        sleep(ACTION_PAUSE_MS);

        System.out.println("  -> Ingresando password incorrecto...");
        loginPage.enterPassword("wrong_password");
        sleep(ACTION_PAUSE_MS);

        System.out.println("  -> Haciendo click en Login...");
        loginPage.clickLoginButton();
        sleep(RESULT_PAUSE_MS);

        System.out.println("  -> Verificando mensaje de error...");
        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "Error message should be displayed");
        sleep(RESULT_PAUSE_MS);

        System.out.println("TEST PASSED: Invalid password error displayed");
        sleep(STEP_PAUSE_MS);
    }

    @Test(description = "Login with locked out user", groups = {"regression"})
    public void testLoginWithLockedOutUser() {
        System.out.println(">>> INICIO: testLoginWithLockedOutUser");
        sleep(STEP_PAUSE_MS);

        LoginPage loginPage = new LoginPage(driver);

        System.out.println("  -> Intentando login con usuario bloqueado...");
        sleep(ACTION_PAUSE_MS);

        loginPage.login("locked_out_user", "secret_sauce");
        sleep(RESULT_PAUSE_MS);

        System.out.println("  -> Verificando mensaje de error por usuario bloqueado...");
        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "Error message should be displayed for locked out user");
        sleep(RESULT_PAUSE_MS);

        System.out.println("TEST PASSED: Locked out user error displayed");
        sleep(STEP_PAUSE_MS);
    }

    @Test(description = "Login page is displayed on load", groups = {"smoke"})
    public void testLoginPageDisplayed() {
        System.out.println(">>> INICIO: testLoginPageDisplayed");
        sleep(STEP_PAUSE_MS);

        System.out.println("  -> Instanciando LoginPage...");
        LoginPage loginPage = new LoginPage(driver);
        sleep(ACTION_PAUSE_MS);

        System.out.println("  -> Verificando que la página de login sea accesible...");
        Assert.assertNotNull(loginPage, "LoginPage should be accessible");
        sleep(RESULT_PAUSE_MS);

        System.out.println("TEST PASSED: Login page is displayed");
        sleep(STEP_PAUSE_MS);
    }

    @Test(description = "Login fails with empty username", groups = {"regression"})
    public void testLoginWithEmptyUsername() {
        System.out.println(">>> INICIO: testLoginWithEmptyUsername");
        sleep(STEP_PAUSE_MS);

        LoginPage loginPage = new LoginPage(driver);
        System.out.println("  -> Ingresando solo password, dejando username vacío...");
        loginPage.enterPassword("secret_sauce");
        sleep(ACTION_PAUSE_MS);

        loginPage.clickLoginButton();
        sleep(RESULT_PAUSE_MS);

        Assert.assertTrue(loginPage.isErrorDisplayed(), "Error should be shown when username is empty");
        Assert.assertTrue(loginPage.getErrorMessage().contains("Username"),
                "Error message should mention Username field");
        sleep(RESULT_PAUSE_MS);

        System.out.println("TEST PASSED: Empty username error displayed: " + loginPage.getErrorMessage());
        sleep(STEP_PAUSE_MS);
    }

    @Test(description = "Login fails with empty password", groups = {"regression"})
    public void testLoginWithEmptyPassword() {
        System.out.println(">>> INICIO: testLoginWithEmptyPassword");
        sleep(STEP_PAUSE_MS);

        LoginPage loginPage = new LoginPage(driver);
        System.out.println("  -> Ingresando solo username, dejando password vacío...");
        loginPage.enterUsername("standard_user");
        sleep(ACTION_PAUSE_MS);

        loginPage.clickLoginButton();
        sleep(RESULT_PAUSE_MS);

        Assert.assertTrue(loginPage.isErrorDisplayed(), "Error should be shown when password is empty");
        Assert.assertTrue(loginPage.getErrorMessage().contains("Password"),
                "Error message should mention Password field");
        sleep(RESULT_PAUSE_MS);

        System.out.println("TEST PASSED: Empty password error displayed: " + loginPage.getErrorMessage());
        sleep(STEP_PAUSE_MS);
    }

    @Test(description = "Login fails when both fields are empty", groups = {"regression"})
    public void testLoginWithEmptyCredentials() {
        System.out.println(">>> INICIO: testLoginWithEmptyCredentials");
        sleep(STEP_PAUSE_MS);

        LoginPage loginPage = new LoginPage(driver);
        System.out.println("  -> Haciendo click en Login sin ingresar ningún dato...");
        sleep(ACTION_PAUSE_MS);

        loginPage.clickLoginButton();
        sleep(RESULT_PAUSE_MS);

        Assert.assertTrue(loginPage.isErrorDisplayed(), "Error should be shown when both fields are empty");
        sleep(RESULT_PAUSE_MS);

        System.out.println("TEST PASSED: Empty credentials error displayed: " + loginPage.getErrorMessage());
        sleep(STEP_PAUSE_MS);
    }

    @Test(description = "Login fails for non-existent username", groups = {"regression"})
    public void testLoginWithNonExistentUsername() {
        System.out.println(">>> INICIO: testLoginWithNonExistentUsername");
        sleep(STEP_PAUSE_MS);

        LoginPage loginPage = new LoginPage(driver);
        System.out.println("  -> Intentando login con usuario inexistente...");
        loginPage.enterUsername("user_does_not_exist");
        sleep(ACTION_PAUSE_MS);
        loginPage.enterPassword("secret_sauce");
        sleep(ACTION_PAUSE_MS);

        loginPage.clickLoginButton();
        sleep(RESULT_PAUSE_MS);

        Assert.assertTrue(loginPage.isErrorDisplayed(), "Error should be shown for non-existent username");
        sleep(RESULT_PAUSE_MS);

        System.out.println("TEST PASSED: Non-existent user error displayed: " + loginPage.getErrorMessage());
        sleep(STEP_PAUSE_MS);
    }

    @Test(description = "Error message is dismissed when X button is clicked", groups = {"regression"})
    public void testErrorMessageDismissedOnClose() {
        System.out.println(">>> INICIO: testErrorMessageDismissedOnClose");
        sleep(STEP_PAUSE_MS);

        LoginPage loginPage = new LoginPage(driver);
        loginPage.enterUsername("bad_user").enterPassword("bad_pass");
        sleep(ACTION_PAUSE_MS);

        loginPage.clickLoginButton();
        sleep(RESULT_PAUSE_MS);

        Assert.assertTrue(loginPage.isErrorDisplayed(), "Error message should appear after failed login");

        System.out.println("  -> Cerrando el mensaje de error...");
        driver.findElement(org.openqa.selenium.By.cssSelector("[data-test='error'] button")).click();
        sleep(ACTION_PAUSE_MS);

        Assert.assertFalse(loginPage.isErrorDisplayed(), "Error message should be dismissed after clicking X");
        sleep(RESULT_PAUSE_MS);

        System.out.println("TEST PASSED: Error message dismissed after clicking close button");
        sleep(STEP_PAUSE_MS);
    }

    @Test(description = "Login page title is 'Swag Labs'", groups = {"smoke"})
    public void testLoginPageTitle() {
        System.out.println(">>> INICIO: testLoginPageTitle");
        sleep(STEP_PAUSE_MS);

        String title = driver.getTitle();
        System.out.println("  -> Page title: " + title);
        sleep(ACTION_PAUSE_MS);

        Assert.assertEquals(title, "Swag Labs", "Page title should be 'Swag Labs'");
        sleep(RESULT_PAUSE_MS);

        System.out.println("TEST PASSED: Page title is correct: " + title);
        sleep(STEP_PAUSE_MS);
    }

    @Test(description = "Password field masks input characters", groups = {"smoke"})
    public void testPasswordFieldMasksInput() {
        System.out.println(">>> INICIO: testPasswordFieldMasksInput");
        sleep(STEP_PAUSE_MS);

        String fieldType = driver.findElement(org.openqa.selenium.By.id("password")).getAttribute("type");
        System.out.println("  -> Password field type attribute: " + fieldType);
        sleep(ACTION_PAUSE_MS);

        Assert.assertEquals(fieldType, "password", "Password field should have type='password' to mask input");
        sleep(RESULT_PAUSE_MS);

        System.out.println("TEST PASSED: Password field correctly masks input");
        sleep(STEP_PAUSE_MS);
    }

    @Test(description = "Successful login redirects to inventory page URL", groups = {"smoke"})
    public void testLoginRedirectsToInventoryUrl() {
        System.out.println(">>> INICIO: testLoginRedirectsToInventoryUrl");
        sleep(STEP_PAUSE_MS);

        LoginPage loginPage = new LoginPage(driver);
        System.out.println("  -> Logging in as standard_user...");
        loginPage.login("standard_user", "secret_sauce");
        sleep(RESULT_PAUSE_MS);

        String currentUrl = driver.getCurrentUrl();
        System.out.println("  -> Current URL after login: " + currentUrl);

        Assert.assertTrue(currentUrl.contains("inventory"),
                "URL should contain 'inventory' after successful login. Got: " + currentUrl);
        sleep(RESULT_PAUSE_MS);

        System.out.println("TEST PASSED: Redirected to inventory URL after login");
        sleep(STEP_PAUSE_MS);
    }

    @Test(description = "Login with problem_user succeeds and loads dashboard", groups = {"regression"})
    public void testLoginWithProblemUser() {
        System.out.println(">>> INICIO: testLoginWithProblemUser");
        sleep(STEP_PAUSE_MS);

        LoginPage loginPage = new LoginPage(driver);
        System.out.println("  -> Logging in as problem_user...");
        sleep(ACTION_PAUSE_MS);

        DashboardPage dashboardPage = loginPage.login("problem_user", "secret_sauce");
        sleep(RESULT_PAUSE_MS);

        Assert.assertTrue(dashboardPage.isDashboardDisplayed(),
                "Dashboard should be displayed for problem_user despite known UI glitches");
        sleep(RESULT_PAUSE_MS);

        System.out.println("TEST PASSED: problem_user logged in and dashboard displayed");
        sleep(STEP_PAUSE_MS);
    }

    @Test(description = "Login with performance_glitch_user succeeds", groups = {"regression"})
    public void testLoginWithPerformanceGlitchUser() {
        System.out.println(">>> INICIO: testLoginWithPerformanceGlitchUser");
        sleep(STEP_PAUSE_MS);

        LoginPage loginPage = new LoginPage(driver);
        System.out.println("  -> Logging in as performance_glitch_user (may be slow)...");
        sleep(ACTION_PAUSE_MS);

        DashboardPage dashboardPage = loginPage.login("performance_glitch_user", "secret_sauce");
        sleep(RESULT_PAUSE_MS);

        Assert.assertTrue(dashboardPage.isDashboardDisplayed(),
                "Dashboard should be displayed for performance_glitch_user");
        sleep(RESULT_PAUSE_MS);

        System.out.println("TEST PASSED: performance_glitch_user logged in successfully");
        sleep(STEP_PAUSE_MS);
    }

    @Test(description = "Login fails with username in wrong case", groups = {"regression"})
    public void testLoginWithCaseSensitiveUsername() {
        System.out.println(">>> INICIO: testLoginWithCaseSensitiveUsername");
        sleep(STEP_PAUSE_MS);

        LoginPage loginPage = new LoginPage(driver);
        System.out.println("  -> Attempting login with 'Standard_User' (wrong case)...");
        loginPage.enterUsername("Standard_User").enterPassword("secret_sauce");
        sleep(ACTION_PAUSE_MS);

        loginPage.clickLoginButton();
        sleep(RESULT_PAUSE_MS);

        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "Error should be shown when username casing does not match");
        sleep(RESULT_PAUSE_MS);

        System.out.println("TEST PASSED: Case-sensitive username rejected: " + loginPage.getErrorMessage());
        sleep(STEP_PAUSE_MS);
    }

    @Test(description = "Login fails with password in wrong case", groups = {"regression"})
    public void testLoginWithCaseSensitivePassword() {
        System.out.println(">>> INICIO: testLoginWithCaseSensitivePassword");
        sleep(STEP_PAUSE_MS);

        LoginPage loginPage = new LoginPage(driver);
        System.out.println("  -> Attempting login with 'Secret_Sauce' (wrong case)...");
        loginPage.enterUsername("standard_user").enterPassword("Secret_Sauce");
        sleep(ACTION_PAUSE_MS);

        loginPage.clickLoginButton();
        sleep(RESULT_PAUSE_MS);

        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "Error should be shown when password casing does not match");
        sleep(RESULT_PAUSE_MS);

        System.out.println("TEST PASSED: Case-sensitive password rejected: " + loginPage.getErrorMessage());
        sleep(STEP_PAUSE_MS);
    }

    @Test(description = "Invalid credentials error message contains expected text", groups = {"regression"})
    public void testInvalidCredentialsErrorMessage() {
        System.out.println(">>> INICIO: testInvalidCredentialsErrorMessage");
        sleep(STEP_PAUSE_MS);

        LoginPage loginPage = new LoginPage(driver);
        System.out.println("  -> Entering mismatched credentials...");
        loginPage.enterUsername("standard_user").enterPassword("wrong_password");
        sleep(ACTION_PAUSE_MS);

        loginPage.clickLoginButton();
        sleep(RESULT_PAUSE_MS);

        String errorMsg = loginPage.getErrorMessage();
        System.out.println("  -> Error message: " + errorMsg);

        Assert.assertTrue(errorMsg.contains("Username and password do not match"),
                "Error message should state credentials do not match. Got: " + errorMsg);
        sleep(RESULT_PAUSE_MS);

        System.out.println("TEST PASSED: Correct error message shown for invalid credentials");
        sleep(STEP_PAUSE_MS);
    }

    @Test(description = "Locked out user error message mentions 'locked out'", groups = {"regression"})
    public void testLockedOutUserErrorMessage() {
        System.out.println(">>> INICIO: testLockedOutUserErrorMessage");
        sleep(STEP_PAUSE_MS);

        LoginPage loginPage = new LoginPage(driver);
        System.out.println("  -> Logging in as locked_out_user...");
        sleep(ACTION_PAUSE_MS);

        loginPage.login("locked_out_user", "secret_sauce");
        sleep(RESULT_PAUSE_MS);

        String errorMsg = loginPage.getErrorMessage();
        System.out.println("  -> Error message: " + errorMsg);

        Assert.assertTrue(errorMsg.contains("locked out"),
                "Error message should contain 'locked out'. Got: " + errorMsg);
        sleep(RESULT_PAUSE_MS);

        System.out.println("TEST PASSED: Locked out user received correct error message");
        sleep(STEP_PAUSE_MS);
    }

    @Test(description = "Login with special characters in credentials fails", groups = {"regression"})
    public void testLoginWithSpecialCharactersInCredentials() {
        System.out.println(">>> INICIO: testLoginWithSpecialCharactersInCredentials");
        sleep(STEP_PAUSE_MS);

        LoginPage loginPage = new LoginPage(driver);
        System.out.println("  -> Entering credentials with special characters...");
        loginPage.enterUsername("user@#$%!").enterPassword("p@ss!#$%");
        sleep(ACTION_PAUSE_MS);

        loginPage.clickLoginButton();
        sleep(RESULT_PAUSE_MS);

        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "Error should be shown when credentials contain special characters");
        sleep(RESULT_PAUSE_MS);

        System.out.println("TEST PASSED: Special character credentials rejected: " + loginPage.getErrorMessage());
        sleep(STEP_PAUSE_MS);
    }

    @Test(description = "Login with whitespace-only credentials fails", groups = {"regression"})
    public void testLoginWithWhitespaceCredentials() {
        System.out.println(">>> INICIO: testLoginWithWhitespaceCredentials");
        sleep(STEP_PAUSE_MS);

        LoginPage loginPage = new LoginPage(driver);
        System.out.println("  -> Entering whitespace-only credentials...");
        loginPage.enterUsername("   ").enterPassword("   ");
        sleep(ACTION_PAUSE_MS);

        loginPage.clickLoginButton();
        sleep(RESULT_PAUSE_MS);

        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "Error should be shown when credentials are whitespace only");
        sleep(RESULT_PAUSE_MS);

        System.out.println("TEST PASSED: Whitespace-only credentials rejected: " + loginPage.getErrorMessage());
        sleep(STEP_PAUSE_MS);
    }

    @Test(description = "Login with leading space in username fails", groups = {"regression"})
    public void testLoginWithLeadingSpaceInUsername() {
        System.out.println(">>> INICIO: testLoginWithLeadingSpaceInUsername");
        sleep(STEP_PAUSE_MS);

        LoginPage loginPage = new LoginPage(driver);
        System.out.println("  -> Entering username with leading space: ' standard_user'...");
        loginPage.enterUsername(" standard_user").enterPassword("secret_sauce");
        sleep(ACTION_PAUSE_MS);

        loginPage.clickLoginButton();
        sleep(RESULT_PAUSE_MS);

        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "Error should be shown when username has a leading space");
        sleep(RESULT_PAUSE_MS);

        System.out.println("TEST PASSED: Username with leading space rejected: " + loginPage.getErrorMessage());
        sleep(STEP_PAUSE_MS);
    }

    @Test(description = "Multiple consecutive failed login attempts all display errors", groups = {"regression"})
    public void testMultipleFailedLoginAttempts() {
        System.out.println(">>> INICIO: testMultipleFailedLoginAttempts");
        sleep(STEP_PAUSE_MS);

        LoginPage loginPage = new LoginPage(driver);
        int attempts = 3;

        for (int i = 1; i <= attempts; i++) {
            System.out.println("  -> Failed login attempt " + i + " of " + attempts + "...");
            loginPage.enterUsername("bad_user_" + i).enterPassword("bad_pass_" + i);
            sleep(ACTION_PAUSE_MS);

            loginPage.clickLoginButton();
            sleep(RESULT_PAUSE_MS);

            Assert.assertTrue(loginPage.isErrorDisplayed(),
                    "Error should be displayed on attempt " + i);

            driver.findElement(org.openqa.selenium.By.cssSelector("[data-test='error'] button")).click();
            sleep(ACTION_PAUSE_MS);

            Assert.assertFalse(loginPage.isErrorDisplayed(),
                    "Error should be dismissed between attempts");
        }

        System.out.println("TEST PASSED: All " + attempts + " failed attempts showed and dismissed errors");
        sleep(STEP_PAUSE_MS);
    }

    @Test(description = "Login with SQL injection attempt in credentials fails safely", groups = {"regression"})
    public void testLoginWithSQLInjectionAttempt() {
        System.out.println(">>> INICIO: testLoginWithSQLInjectionAttempt");
        sleep(STEP_PAUSE_MS);

        LoginPage loginPage = new LoginPage(driver);
        System.out.println("  -> Entering SQL injection attempt in credentials...");
        loginPage.enterUsername("' OR '1'='1").enterPassword("' OR '1'='1' --");
        sleep(ACTION_PAUSE_MS);

        loginPage.clickLoginButton();
        sleep(RESULT_PAUSE_MS);

        Assert.assertFalse(new DashboardPage(driver).isDashboardDisplayed(),
                "SQL injection attempt should not grant access to the dashboard");
        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "Error should be shown for SQL injection attempt");
        sleep(RESULT_PAUSE_MS);

        System.out.println("TEST PASSED: SQL injection attempt was rejected: " + loginPage.getErrorMessage());
        sleep(STEP_PAUSE_MS);
    }

    // ── helper ──────────────────────────────────────────────────────────────
    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}