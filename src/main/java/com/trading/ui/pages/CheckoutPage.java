package com.trading.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
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
    private By errorMessage = By.cssSelector("[data-test='error']");
    private By completeHeader = By.className("complete-header");

    private WebDriverWait errorWait;

    public CheckoutPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        this.errorWait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    public String getErrorMessage() {
        try {
            return errorWait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage)).getText();
        } catch (Exception e) {
            return "";
        }
    }

    public CheckoutPage enterFirstName(String firstName) {
        wait.until(ExpectedConditions.elementToBeClickable(firstNameField)).sendKeys(firstName);
        return this;
    }

    public CheckoutPage enterLastName(String lastName) {
        wait.until(ExpectedConditions.elementToBeClickable(lastNameField)).sendKeys(lastName);
        return this;
    }

    public CheckoutPage enterPostalCode(String postalCode) {
        wait.until(ExpectedConditions.elementToBeClickable(postalCodeField)).sendKeys(postalCode);
        return this;
    }

    public CheckoutPage continueCheckout() {
        final int maxAttempts = 3;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                wait.until(ExpectedConditions.elementToBeClickable(continueButton)).click();
                System.out.println("Continued to checkout step 2");
                return this;
            } catch (TimeoutException e) {
                System.out.println("Attempt " + attempt + "/" + maxAttempts
                        + " timed out clicking continue button: " + e.getMessage());
                if (attempt == maxAttempts) {
                    throw new RuntimeException("Failed to click continue button after " + maxAttempts + " attempts", e);
                }
            }
        }

        throw new RuntimeException("Failed to click continue button after " + maxAttempts + " attempts");
    }

    public CheckoutPage finishCheckout() {
        final int maxAttempts = 3;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                wait.until(ExpectedConditions.elementToBeClickable(finishButton)).click();
                System.out.println("Finished checkout");
                return this;
            } catch (TimeoutException e) {
                System.out.println("Attempt " + attempt + "/" + maxAttempts
                        + " timed out clicking finish button: " + e.getMessage());
                if (attempt == maxAttempts) {
                    throw new RuntimeException("Failed to click finish button after " + maxAttempts + " attempts", e);
                }
            }
        }

        throw new RuntimeException("Failed to click finish button after " + maxAttempts + " attempts");
    }

    public boolean isCheckoutComplete() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(completeHeader)).isDisplayed();
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
