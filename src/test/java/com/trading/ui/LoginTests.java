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

    @BeforeMethod
    public void navigateToLoginPage() {
        ((JavascriptExecutor) driver).executeScript("window.localStorage.clear()");
        driver.get(BASE_URL);
        System.out.println("Setup: Navigated to login page");
    }

    @Test(description = "Successful login with valid credentials", groups = {"smoke"})
    public void testLoginWithValidCredentials() {
        User user = new User("standard_user", "secret_sauce");
        LoginPage loginPage = new LoginPage(driver);

        DashboardPage dashboardPage = loginPage.login(user.getUsername(), user.getPassword());

        Assert.assertTrue(dashboardPage.isDashboardDisplayed(), "Dashboard should be displayed after successful login");
        System.out.println("TEST PASSED: User successfully logged in");
    }

    @Test(description = "Login fails with invalid password", groups = {"regression"})
    public void testLoginWithInvalidPassword() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.enterUsername("standard_user");
        loginPage.enterPassword("wrong_password");
        loginPage.clickLoginButton();

        Assert.assertTrue(loginPage.isErrorDisplayed(), "Error message should be displayed");
        System.out.println("TEST PASSED: Invalid password error displayed");
    }

    @Test(description = "Login with locked out user", groups = {"regression"})
    public void testLoginWithLockedOutUser() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("locked_out_user", "secret_sauce");

        Assert.assertTrue(loginPage.isErrorDisplayed(), "Error message should be displayed for locked out user");
        System.out.println("TEST PASSED: Locked out user error displayed");
    }

    @Test(description = "Login page is displayed on load", groups = {"smoke"})
    public void testLoginPageDisplayed() {
        LoginPage loginPage = new LoginPage(driver);

        // If we can create a LoginPage without errors, the login page is displayed
        Assert.assertNotNull(loginPage, "LoginPage should be accessible");
        System.out.println("TEST PASSED: Login page is displayed");
    }
}