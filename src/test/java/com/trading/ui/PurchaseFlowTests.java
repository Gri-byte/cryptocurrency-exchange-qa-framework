package com.trading.ui;

import com.trading.base.BaseTest;
import com.trading.ui.pages.LoginPage;
import com.trading.ui.pages.DashboardPage;
import com.trading.ui.pages.CartPage;
import com.trading.ui.pages.CheckoutPage;
import com.trading.ui.models.User;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;

public class PurchaseFlowTests extends BaseTest {

    private DashboardPage dashboardPage;

    @BeforeMethod
    public void loginBeforeTest() {
        User user = new User("standard_user", "secret_sauce");
        LoginPage loginPage = new LoginPage(driver);
        dashboardPage = loginPage.login(user.getUsername(), user.getPassword());
        System.out.println("Setup: User logged in");
    }

    @Test(description = "Add single product to cart", groups = {"smoke"})
    public void testAddProductToCart() {
        dashboardPage.addProductToCart("Sauce Labs Backpack");
        int cartCount = dashboardPage.getCartItemCount();

        Assert.assertEquals(cartCount, 1, "Cart should have 1 item");
        System.out.println("TEST PASSED: Single product added to cart");
    }

    @Test(description = "Add multiple products to cart", groups = {"smoke"})
    public void testAddMultipleProducts() {
        dashboardPage.addProductToCart("Sauce Labs Backpack");
        dashboardPage.addProductToCart("Sauce Labs Bike Light");
        int cartCount = dashboardPage.getCartItemCount();

        Assert.assertEquals(cartCount, 2, "Cart should have 2 items");
        System.out.println("TEST PASSED: Multiple products added to cart");
    }

    @Test(description = "View cart with products", groups = {"smoke"})
    public void testViewCart() {
        dashboardPage.addProductToCart("Sauce Labs Backpack");
        CartPage cartPage = dashboardPage.clickShoppingCart();

        Assert.assertTrue(cartPage.isCartDisplayed(), "Cart page should be displayed");
        Assert.assertEquals(cartPage.getCartItemCount(), 1, "Cart should have 1 item");
        System.out.println("TEST PASSED: Cart displayed correctly");
    }

    @Test(description = "Complete full checkout flow", groups = {"smoke"})
    public void testCompleteCheckout() {
        dashboardPage.addProductToCart("Sauce Labs Backpack");
        CartPage cartPage = dashboardPage.clickShoppingCart();
        Assert.assertTrue(cartPage.isCartDisplayed(), "Cart page should be displayed");

        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        checkoutPage.completeCheckout("John", "Doe", "12345");

        Assert.assertTrue(checkoutPage.isCheckoutComplete(), "Checkout should be completed successfully");
        System.out.println("TEST PASSED: Complete checkout flow successful");
    }

    @Test(description = "View product count on dashboard", groups = {"regression"})
    public void testProductCountOnDashboard() {
        int productCount = dashboardPage.getProductCount();

        Assert.assertTrue(productCount > 0, "Dashboard should have products");
        System.out.println("TEST PASSED: Dashboard has " + productCount + " products");
    }
}
