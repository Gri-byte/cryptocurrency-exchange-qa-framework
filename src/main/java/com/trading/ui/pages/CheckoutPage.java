package com.trading.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class CheckoutPage extends BasePage {
    private By firstNameField = By.id("first-name");
    private By lastNameField = By.id("last-name");
    private By postalCodeField = By.id("postal-code");
    private By continueButton = By.id("continue");
    private By finishButton = By.id("finish");
    private By errorMessage = By.cssSelector("[data-test='error']");
    private By completeHeader = By.className("complete-header");

    private WebDriverWait errorWait;

    public CheckoutPage(WebDriver driver) {
        super(driver);
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
        typeAndVerify(firstNameField, firstName);
        return this;
    }

    public CheckoutPage enterLastName(String lastName) {
        typeAndVerify(lastNameField, lastName);
        return this;
    }

    public CheckoutPage enterPostalCode(String postalCode) {
        typeAndVerify(postalCodeField, postalCode);
        return this;
    }

    public CheckoutPage continueCheckout() {
        retry("click continue button", 3, () -> {
            // Same React click-handler race as the checkout button: the click can register
            // without the SPA reacting at all. A successful click either advances to step two
            // (finish button appears) or surfaces a validation error - either counts as proof
            // the click actually landed, so only retry when neither happens.
            clickAndVerify(continueButton, ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(finishButton),
                    ExpectedConditions.visibilityOfElementLocated(errorMessage)));
            return null;
        });
        System.out.println("Continued to checkout step 2");
        return this;
    }

    public CheckoutPage finishCheckout() {
        retry("click finish button", 3, () -> {
            clickAndVerify(finishButton, ExpectedConditions.visibilityOfElementLocated(completeHeader));
            return null;
        });
        System.out.println("Finished checkout");
        return this;
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
