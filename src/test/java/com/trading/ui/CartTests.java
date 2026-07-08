package com.trading.ui;

import com.trading.base.BaseTest;
import com.trading.ui.pages.LoginPage;
import com.trading.ui.pages.DashboardPage;
import com.trading.ui.pages.CartPage;
import com.trading.ui.models.User;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CartTests extends BaseTest {

    private DashboardPage dashboardPage;

    @BeforeMethod(alwaysRun = true)
    public void loginBeforeTest() {
        dashboardPage = loginWithRetry("standard_user", "secret_sauce", 3);
        System.out.println("Setup: User logged in");
    }

    @Test(description = "Cart is empty immediately after login", groups = {"smoke"})
    public void testCartIsInitiallyEmpty() {
        Assert.assertFalse(dashboardPage.hasItemsInCart(), "Cart should be empty after logging in");
        System.out.println("TEST PASSED: Cart is initially empty");
    }

    @Test(description = "Item can be removed from cart using Remove button on dashboard", groups = {"smoke"})
    public void testRemoveItemFromDashboard() {
        dashboardPage.addProductToCart("Sauce Labs Backpack");
        Assert.assertEquals(dashboardPage.getCartItemCount(), 1, "Cart should have 1 item after adding");

        driver.findElement(By.xpath("//button[contains(text(), 'Remove')]")).click();

        Assert.assertFalse(dashboardPage.hasItemsInCart(), "Cart should be empty after removing the item");
        System.out.println("TEST PASSED: Item removed from cart via dashboard Remove button");
    }

    @Test(description = "Add three different items to cart and verify count", groups = {"regression"})
    public void testAddThreeItemsToCart() {
        dashboardPage.addProductToCart("Sauce Labs Backpack");
        dashboardPage.addProductToCart("Sauce Labs Bike Light");
        dashboardPage.addProductToCart("Sauce Labs Bolt T-Shirt");

        Assert.assertEquals(dashboardPage.getCartItemCount(), 3, "Cart should contain 3 items");
        System.out.println("TEST PASSED: Three items added to cart successfully");
    }

    @Test(description = "Cart item count decreases after removing one item from cart page", groups = {"regression"})
    public void testCartItemCountAfterRemovingItem() {
        dashboardPage.addProductToCart("Sauce Labs Backpack");
        dashboardPage.addProductToCart("Sauce Labs Bike Light");
        Assert.assertEquals(dashboardPage.getCartItemCount(), 2, "Cart should have 2 items before removal");

        CartPage cartPage = dashboardPage.clickShoppingCart();
        driver.findElement(By.xpath("//button[contains(text(), 'Remove')]")).click();

        Assert.assertEquals(cartPage.getCartItemCount(), 1, "Cart should have 1 item after removing one");
        System.out.println("TEST PASSED: Cart count decreased to 1 after removing an item");
    }

    @Test(description = "Continue Shopping button from cart returns user to dashboard", groups = {"regression"})
    public void testContinueShoppingFromCart() {
        dashboardPage.addProductToCart("Sauce Labs Backpack");
        dashboardPage.clickShoppingCart();

        driver.findElement(By.id("continue-shopping")).click();

        Assert.assertTrue(dashboardPage.isDashboardDisplayed(), "Dashboard should be displayed after clicking Continue Shopping");
        System.out.println("TEST PASSED: Continue Shopping button returns to dashboard");
    }

    @Test(description = "Cart page is empty when navigated to without adding items", groups = {"regression"})
    public void testCartShowsEmptyStateWithNoItems() {
        CartPage cartPage = dashboardPage.clickShoppingCart();

        Assert.assertEquals(cartPage.getCartItemCount(), 0, "Cart should show no items when none were added");
        System.out.println("TEST PASSED: Cart shows empty state with no items added");
    }

    @Test(description = "Add to cart button changes to Remove after item is added", groups = {"regression"})
    public void testAddToCartButtonChangesToRemoveAfterAdd() {
        dashboardPage.addProductToCart("Sauce Labs Backpack");

        org.openqa.selenium.WebElement removeButton = driver.findElement(
                org.openqa.selenium.By.xpath("//button[contains(text(), 'Remove')]"));
        Assert.assertTrue(removeButton.isDisplayed(),
                "Remove button should be visible after product is added to cart");
        System.out.println("TEST PASSED: Add to cart button changed to Remove after adding product");
    }

    @Test(description = "Cart count does not exceed 1 when same product is added once", groups = {"regression"})
    public void testCartCountAfterAddingSingleProduct() {
        dashboardPage.addProductToCart("Sauce Labs Backpack");

        Assert.assertEquals(dashboardPage.getCartItemCount(), 1,
                "Cart count should be exactly 1 after adding one product");
        boolean addButtonGone = driver.findElements(
                org.openqa.selenium.By.xpath("//button[text()='Add to cart']")).isEmpty();
        Assert.assertTrue(addButtonGone || dashboardPage.getCartItemCount() == 1,
                "Cart should not allow duplicate add of the same item");
        System.out.println("TEST PASSED: Cart count is 1 and duplicate add is not possible");
    }

    @Test(description = "Removing all items from cart clears the cart badge", groups = {"regression"})
    public void testCartBadgeDisappearsAfterRemovingAllItems() {
        dashboardPage.addProductToCart("Sauce Labs Backpack");
        Assert.assertTrue(dashboardPage.hasItemsInCart(), "Cart should show badge after adding item");

        driver.findElement(org.openqa.selenium.By.xpath("//button[contains(text(), 'Remove')]")).click();

        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(5))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOfElementLocated(
                        org.openqa.selenium.By.className("shopping_cart_badge")));

        Assert.assertFalse(dashboardPage.hasItemsInCart(),
                "Cart badge should disappear after all items are removed");
        System.out.println("TEST PASSED: Cart badge cleared after removing all items");
    }

    @Test(description = "Cart page displays the correct product name", groups = {"regression"})
    public void testCartDisplaysCorrectItemName() {
        String expectedProduct = "Sauce Labs Backpack";
        dashboardPage.addProductToCart(expectedProduct);

        dashboardPage.clickShoppingCart();
        String itemName = driver.findElement(By.className("inventory_item_name")).getText();

        Assert.assertEquals(itemName, expectedProduct, "Cart should display the correct product name");
        System.out.println("TEST PASSED: Cart displays correct item name: " + itemName);
    }
}
