package com.trading.ui;

import com.trading.base.BaseTest;
import com.trading.ui.pages.LoginPage;
import com.trading.ui.pages.DashboardPage;
import com.trading.ui.models.User;
import org.openqa.selenium.JavascriptExecutor;
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

    // ── helper ──────────────────────────────────────────────────────────────
    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}