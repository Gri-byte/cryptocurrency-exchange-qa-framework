package com.trading.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

public class LoginPage extends BasePage {
    private WebDriverWait errorWait;

    private By usernameField = By.id("user-name");
    private By passwordField = By.id("password");
    private By loginButton = By.id("login-button");
    private By errorMessage = By.cssSelector("[data-test='error']");

    public LoginPage(WebDriver driver) {
        super(driver);
        this.errorWait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    public LoginPage enterUsername(String username) {
        typeAndVerify(usernameField, username);
        System.out.println("Entered username: " + username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        typeAndVerify(passwordField, password);
        System.out.println("Entered password");
        return this;
    }

    public DashboardPage clickLoginButton() {
        wait.until(ExpectedConditions.elementToBeClickable(loginButton)).click();
        System.out.println("Clicked login button");
        return new DashboardPage(driver);
    }

    public String getErrorMessage() {
        try {
            WebElement error = errorWait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage));
            return error.getText();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isErrorDisplayed() {
        try {
            return errorWait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage)).isDisplayed();
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
