package com.trading.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class CartPage {
    private WebDriver driver;
    private WebDriverWait wait;

    private By cartContainer = By.className("cart_contents_container");
    private By cartItems = By.className("cart_item");
    private By checkoutButton = By.id("checkout");

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    public boolean isCartDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(cartContainer));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public int getCartItemCount() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(cartContainer));
        return driver.findElements(cartItems).size();
    }

    public CheckoutPage proceedToCheckout() {
        final int maxAttempts = 3;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                wait.until(ExpectedConditions.elementToBeClickable(checkoutButton)).click();
                System.out.println("Proceeded to checkout");
                return new CheckoutPage(driver);
            } catch (TimeoutException | StaleElementReferenceException | ElementClickInterceptedException e) {
                System.out.println("Attempt " + attempt + "/" + maxAttempts
                        + " failed clicking checkout button: "
                        + e.getClass().getSimpleName() + ": " + e.getMessage());
                if (attempt == maxAttempts) {
                    throw new RuntimeException("Failed to click checkout button after " + maxAttempts + " attempts", e);
                }
            }
        }

        throw new RuntimeException("Failed to click checkout button after " + maxAttempts + " attempts");
    }
}
