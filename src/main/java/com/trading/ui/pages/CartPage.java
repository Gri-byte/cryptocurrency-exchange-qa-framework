package com.trading.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class CartPage extends BasePage {
    private By cartContainer = By.className("cart_contents_container");
    private By cartItems = By.className("cart_item");
    private By checkoutButton = By.id("checkout");
    private By firstNameField = By.id("first-name");

    public CartPage(WebDriver driver) {
        super(driver);
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
        retry("click checkout button", 3, () -> {
            // The click can register without the SPA actually routing to checkout-step-one
            // (React click-handler race), so confirm navigation happened before trusting it.
            clickAndVerify(checkoutButton, ExpectedConditions.visibilityOfElementLocated(firstNameField));
            return null;
        });
        System.out.println("Proceeded to checkout");
        return new CheckoutPage(driver);
    }
}
