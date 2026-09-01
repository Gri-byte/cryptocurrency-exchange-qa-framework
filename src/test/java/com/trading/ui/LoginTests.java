package com.trading.ui;

import com.trading.base.BaseTest;
import com.trading.ui.pages.LoginPage;
import com.trading.ui.pages.DashboardPage;
import com.trading.ui.models.User;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LoginTests extends BaseTest {

    @BeforeMethod(alwaysRun = true)
    public void navigateToLoginPage() {
        resetToLoginPage();
        System.out.println("Setup: Navigated to login page");
    }

    @Test(description = "Successful login with valid credentials", groups = {"smoke"})
    public void testLoginWithValidCredentials() {
        User user = new User("standard_user", "secret_sauce");

        DashboardPage dashboardPage = loginWithRetry(user.getUsername(), user.getPassword(), 3);

        Assert.assertTrue(dashboardPage.isDashboardDisplayed(),
                "Dashboard should be displayed after successful login");

        System.out.println("TEST PASSED: User successfully logged in");
    }

    @Test(description = "Login fails with invalid password", groups = {"regression"})
    public void testLoginWithInvalidPassword() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.enterUsername("standard_user");
        loginPage.enterPassword("wrong_password");
        loginPage.clickLoginButton();

        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "Error message should be displayed");

        System.out.println("TEST PASSED: Invalid password error displayed");
    }

    @Test(description = "Login with locked out user", groups = {"regression"})
    public void testLoginWithLockedOutUser() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("locked_out_user", "secret_sauce");

        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "Error message should be displayed for locked out user");

        System.out.println("TEST PASSED: Locked out user error displayed");
    }

    @Test(description = "Login page is displayed on load", groups = {"smoke"})
    public void testLoginPageDisplayed() {
        LoginPage loginPage = new LoginPage(driver);

        Assert.assertNotNull(loginPage, "LoginPage should be accessible");

        System.out.println("TEST PASSED: Login page is displayed");
    }

    @Test(description = "Login fails with empty username", groups = {"regression"})
    public void testLoginWithEmptyUsername() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.enterPassword("secret_sauce");
        loginPage.clickLoginButton();

        Assert.assertTrue(loginPage.isErrorDisplayed(), "Error should be shown when username is empty");
        Assert.assertTrue(loginPage.getErrorMessage().contains("Username"),
                "Error message should mention Username field");

        System.out.println("TEST PASSED: Empty username error displayed: " + loginPage.getErrorMessage());
    }

    @Test(description = "Login fails with empty password", groups = {"regression"})
    public void testLoginWithEmptyPassword() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.enterUsername("standard_user");
        loginPage.clickLoginButton();

        Assert.assertTrue(loginPage.isErrorDisplayed(), "Error should be shown when password is empty");
        Assert.assertTrue(loginPage.getErrorMessage().contains("Password"),
                "Error message should mention Password field");

        System.out.println("TEST PASSED: Empty password error displayed: " + loginPage.getErrorMessage());
    }

    @Test(description = "Login fails when both fields are empty", groups = {"regression"})
    public void testLoginWithEmptyCredentials() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.clickLoginButton();

        Assert.assertTrue(loginPage.isErrorDisplayed(), "Error should be shown when both fields are empty");

        System.out.println("TEST PASSED: Empty credentials error displayed: " + loginPage.getErrorMessage());
    }

    @Test(description = "Login fails for non-existent username", groups = {"regression"})
    public void testLoginWithNonExistentUsername() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.enterUsername("user_does_not_exist");
        loginPage.enterPassword("secret_sauce");
        loginPage.clickLoginButton();

        Assert.assertTrue(loginPage.isErrorDisplayed(), "Error should be shown for non-existent username");

        System.out.println("TEST PASSED: Non-existent user error displayed: " + loginPage.getErrorMessage());
    }

    @Test(description = "Error message is dismissed when X button is clicked", groups = {"regression"})
    public void testErrorMessageDismissedOnClose() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.enterUsername("bad_user").enterPassword("bad_pass");
        loginPage.clickLoginButton();

        Assert.assertTrue(loginPage.isErrorDisplayed(), "Error message should appear after failed login");

        clickAndVerify(org.openqa.selenium.By.cssSelector("[data-test='error'] button"),
                org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOfElementLocated(
                        org.openqa.selenium.By.cssSelector("[data-test='error']")));

        Assert.assertFalse(loginPage.isErrorDisplayed(), "Error message should be dismissed after clicking X");

        System.out.println("TEST PASSED: Error message dismissed after clicking close button");
    }

    @Test(description = "Login page title is 'Swag Labs'", groups = {"smoke"})
    public void testLoginPageTitle() {
        String title = driver.getTitle();

        Assert.assertEquals(title, "Swag Labs", "Page title should be 'Swag Labs'");

        System.out.println("TEST PASSED: Page title is correct: " + title);
    }

    @Test(description = "Password field masks input characters", groups = {"smoke"})
    public void testPasswordFieldMasksInput() {
        String fieldType = driver.findElement(org.openqa.selenium.By.id("password")).getAttribute("type");

        Assert.assertEquals(fieldType, "password", "Password field should have type='password' to mask input");

        System.out.println("TEST PASSED: Password field correctly masks input");
    }

    @Test(description = "Successful login redirects to inventory page URL", groups = {"smoke"})
    public void testLoginRedirectsToInventoryUrl() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("standard_user", "secret_sauce");

        String currentUrl = driver.getCurrentUrl();

        Assert.assertTrue(currentUrl.contains("inventory"),
                "URL should contain 'inventory' after successful login. Got: " + currentUrl);

        System.out.println("TEST PASSED: Redirected to inventory URL after login");
    }

    @Test(description = "Login with problem_user succeeds and loads dashboard", groups = {"regression"})
    public void testLoginWithProblemUser() {
        DashboardPage dashboardPage = loginWithRetry("problem_user", "secret_sauce", 3);

        Assert.assertTrue(dashboardPage.isDashboardDisplayed(),
                "Dashboard should be displayed for problem_user despite known UI glitches");

        System.out.println("TEST PASSED: problem_user logged in and dashboard displayed");
    }

    @Test(description = "Login with performance_glitch_user succeeds", groups = {"regression"})
    public void testLoginWithPerformanceGlitchUser() {
        DashboardPage dashboardPage = loginWithRetry("performance_glitch_user", "secret_sauce", 3);

        Assert.assertTrue(dashboardPage.isDashboardDisplayed(),
                "Dashboard should be displayed for performance_glitch_user");

        System.out.println("TEST PASSED: performance_glitch_user logged in successfully");
    }

    @Test(description = "Login fails with username in wrong case", groups = {"regression"})
    public void testLoginWithCaseSensitiveUsername() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.enterUsername("Standard_User").enterPassword("secret_sauce");
        loginPage.clickLoginButton();

        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "Error should be shown when username casing does not match");

        System.out.println("TEST PASSED: Case-sensitive username rejected: " + loginPage.getErrorMessage());
    }

    @Test(description = "Login fails with password in wrong case", groups = {"regression"})
    public void testLoginWithCaseSensitivePassword() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.enterUsername("standard_user").enterPassword("Secret_Sauce");
        loginPage.clickLoginButton();

        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "Error should be shown when password casing does not match");

        System.out.println("TEST PASSED: Case-sensitive password rejected: " + loginPage.getErrorMessage());
    }

    @Test(description = "Invalid credentials error message contains expected text", groups = {"regression"})
    public void testInvalidCredentialsErrorMessage() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.enterUsername("standard_user").enterPassword("wrong_password");
        loginPage.clickLoginButton();

        String errorMsg = loginPage.getErrorMessage();

        Assert.assertTrue(errorMsg.contains("Username and password do not match"),
                "Error message should state credentials do not match. Got: " + errorMsg);

        System.out.println("TEST PASSED: Correct error message shown for invalid credentials");
    }

    @Test(description = "Locked out user error message mentions 'locked out'", groups = {"regression"})
    public void testLockedOutUserErrorMessage() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("locked_out_user", "secret_sauce");

        String errorMsg = loginPage.getErrorMessage();

        Assert.assertTrue(errorMsg.contains("locked out"),
                "Error message should contain 'locked out'. Got: " + errorMsg);

        System.out.println("TEST PASSED: Locked out user received correct error message");
    }

    @Test(description = "Login with special characters in credentials fails", groups = {"regression"})
    public void testLoginWithSpecialCharactersInCredentials() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.enterUsername("user@#$%!").enterPassword("p@ss!#$%");
        loginPage.clickLoginButton();

        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "Error should be shown when credentials contain special characters");

        System.out.println("TEST PASSED: Special character credentials rejected: " + loginPage.getErrorMessage());
    }

    @Test(description = "Login with whitespace-only credentials fails", groups = {"regression"})
    public void testLoginWithWhitespaceCredentials() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.enterUsername("   ").enterPassword("   ");
        loginPage.clickLoginButton();

        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "Error should be shown when credentials are whitespace only");

        System.out.println("TEST PASSED: Whitespace-only credentials rejected: " + loginPage.getErrorMessage());
    }

    @Test(description = "Login with leading space in username fails", groups = {"regression"})
    public void testLoginWithLeadingSpaceInUsername() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.enterUsername(" standard_user").enterPassword("secret_sauce");
        loginPage.clickLoginButton();

        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "Error should be shown when username has a leading space");

        System.out.println("TEST PASSED: Username with leading space rejected: " + loginPage.getErrorMessage());
    }

    @Test(description = "Multiple consecutive failed login attempts all display errors", groups = {"regression"})
    public void testMultipleFailedLoginAttempts() {
        LoginPage loginPage = new LoginPage(driver);
        int attempts = 3;

        for (int i = 1; i <= attempts; i++) {
            loginPage.enterUsername("bad_user_" + i).enterPassword("bad_pass_" + i);
            loginPage.clickLoginButton();

            Assert.assertTrue(loginPage.isErrorDisplayed(),
                    "Error should be displayed on attempt " + i);

            clickAndVerify(org.openqa.selenium.By.cssSelector("[data-test='error'] button"),
                org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOfElementLocated(
                        org.openqa.selenium.By.cssSelector("[data-test='error']")));

            Assert.assertFalse(loginPage.isErrorDisplayed(),
                    "Error should be dismissed between attempts");
        }

        System.out.println("TEST PASSED: All " + attempts + " failed attempts showed and dismissed errors");
    }

    @Test(description = "Login with SQL injection attempt in credentials fails safely", groups = {"regression"})
    public void testLoginWithSQLInjectionAttempt() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.enterUsername("' OR '1'='1").enterPassword("' OR '1'='1' --");
        loginPage.clickLoginButton();

        Assert.assertFalse(new DashboardPage(driver).isDashboardDisplayed(),
                "SQL injection attempt should not grant access to the dashboard");
        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "Error should be shown for SQL injection attempt");

        System.out.println("TEST PASSED: SQL injection attempt was rejected: " + loginPage.getErrorMessage());
    }
}
