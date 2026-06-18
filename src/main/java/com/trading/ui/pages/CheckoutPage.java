package com.trading.ui.pages;

import org.openqa.selenium.By;
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

    public CheckoutPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
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
        wait.until(ExpectedConditions.elementToBeClickable(continueButton)).click();
        System.out.println("Continued to checkout step 2");
        return this;
    }

    public CheckoutPage finishCheckout() {
        wait.until(ExpectedConditions.elementToBeClickable(finishButton)).click();
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
