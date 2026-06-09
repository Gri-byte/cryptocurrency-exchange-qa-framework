package com.trading.ui;

import com.trading.base.BaseTest;
import com.trading.ui.pages.LoginPage;
import com.trading.ui.pages.DashboardPage;
import com.trading.ui.pages.CartPage;
import com.trading.ui.pages.CheckoutPage;
import com.trading.ui.models.User;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CheckoutTests extends BaseTest {

    private DashboardPage dashboardPage;

    @BeforeMethod
    public void loginAndAddProductToCart() {
        ((JavascriptExecutor) driver).executeScript("window.localStorage.clear()");
        driver.get(BASE_URL);
        User user = new User("standard_user", "secret_sauce");
        LoginPage loginPage = new LoginPage(driver);
        dashboardPage = loginPage.login(user.getUsername(), user.getPassword());
        dashboardPage.addProductToCart("Sauce Labs Backpack");
        System.out.println("Setup: User logged in and product added to cart");
    }

    @Test(description = "Checkout form shows error when first name is missing", groups = {"regression"})
    public void testCheckoutRequiresFirstName() {
        CartPage cartPage = dashboardPage.clickShoppingCart();
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();

        checkoutPage.enterLastName("Doe").enterPostalCode("12345").continueCheckout();

        String errorMessage = driver.findElement(By.cssSelector("[data-test='error']")).getText();
        Assert.assertTrue(errorMessage.contains("First Name"), "Error should indicate that First Name is required");
        System.out.println("TEST PASSED: Error shown for missing first name: " + errorMessage);
    }

    @Test(description = "Checkout form shows error when last name is missing", groups = {"regression"})
    public void testCheckoutRequiresLastName() {
        CartPage cartPage = dashboardPage.clickShoppingCart();
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();

        checkoutPage.enterFirstName("John").enterPostalCode("12345").continueCheckout();

        String errorMessage = driver.findElement(By.cssSelector("[data-test='error']")).getText();
        Assert.assertTrue(errorMessage.contains("Last Name"), "Error should indicate that Last Name is required");
        System.out.println("TEST PASSED: Error shown for missing last name: " + errorMessage);
    }

    @Test(description = "Checkout form shows error when postal code is missing", groups = {"regression"})
    public void testCheckoutRequiresPostalCode() {
        CartPage cartPage = dashboardPage.clickShoppingCart();
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();

        checkoutPage.enterFirstName("John").enterLastName("Doe").continueCheckout();

        String errorMessage = driver.findElement(By.cssSelector("[data-test='error']")).getText();
        Assert.assertTrue(errorMessage.contains("Postal Code"), "Error should indicate that Postal Code is required");
        System.out.println("TEST PASSED: Error shown for missing postal code: " + errorMessage);
    }

    @Test(description = "Checkout overview shows the item added to cart", groups = {"smoke"})
    public void testCheckoutSummaryShowsItem() {
        CartPage cartPage = dashboardPage.clickShoppingCart();
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        checkoutPage.enterFirstName("John").enterLastName("Doe").enterPostalCode("12345").continueCheckout();

        String itemName = driver.findElement(By.className("inventory_item_name")).getText();
        Assert.assertEquals(itemName, "Sauce Labs Backpack", "Checkout overview should display the added product");
        System.out.println("TEST PASSED: Checkout summary shows item: " + itemName);
    }

    @Test(description = "Cancelling checkout step 1 returns user to cart", groups = {"regression"})
    public void testCancelCheckoutReturnsToCart() {
        CartPage cartPage = dashboardPage.clickShoppingCart();
        cartPage.proceedToCheckout();

        driver.findElement(By.id("cancel")).click();

        Assert.assertTrue(new CartPage(driver).isCartDisplayed(), "Cart page should be displayed after cancelling checkout");
        System.out.println("TEST PASSED: Cancel checkout returns to cart page");
    }

    @Test(description = "Completed checkout displays order confirmation message", groups = {"smoke"})
    public void testCheckoutCompletionMessage() {
        CartPage cartPage = dashboardPage.clickShoppingCart();
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        checkoutPage.completeCheckout("Jane", "Smith", "67890");

        String confirmationHeader = driver.findElement(By.className("complete-header")).getText();
        Assert.assertEquals(confirmationHeader, "Thank you for your order!", "Order confirmation header should be displayed");
        System.out.println("TEST PASSED: Order confirmation message displayed: " + confirmationHeader);
    }
}
