package com.trading.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
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
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public boolean isCartDisplayed() {
        try {
            return driver.findElement(cartContainer).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public int getCartItemCount() {
        return driver.findElements(cartItems).size();
    }

    public CheckoutPage proceedToCheckout() {
        driver.findElement(checkoutButton).click();
        System.out.println("Proceeded to checkout");
        return new CheckoutPage(driver);
    }
}
