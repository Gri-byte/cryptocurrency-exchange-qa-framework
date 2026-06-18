package com.trading.ui;

import com.trading.base.BaseTest;
import com.trading.ui.pages.LoginPage;
import com.trading.ui.pages.DashboardPage;
import com.trading.ui.models.User;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DashboardTests extends BaseTest {

    private DashboardPage dashboardPage;

    @BeforeMethod
    public void loginBeforeTest() {
        dashboardPage = loginWithRetry("standard_user", "secret_sauce", 3);
        System.out.println("Setup: User logged in");
    }

    @Test(description = "App logo is visible on dashboard", groups = {"smoke"})
    public void testDashboardLogoDisplayed() {
        Assert.assertTrue(dashboardPage.isLogoDisplayed(), "App logo should be visible on dashboard");
        System.out.println("TEST PASSED: Logo is displayed on dashboard");
    }

    @Test(description = "App logo displays correct brand text", groups = {"regression"})
    public void testDashboardLogoText() {
        String logoText = dashboardPage.getLogoText();
        Assert.assertEquals(logoText, "Swag Labs", "Logo text should be 'Swag Labs'");
        System.out.println("TEST PASSED: Logo text is correct: " + logoText);
    }

    @Test(description = "Dashboard shows exactly 6 products", groups = {"smoke"})
    public void testDashboardShowsExactProductCount() {
        int productCount = dashboardPage.getProductCount();
        Assert.assertEquals(productCount, 6, "Dashboard should display exactly 6 products");
        System.out.println("TEST PASSED: Dashboard shows " + productCount + " products");
    }

    @Test(description = "All product names on dashboard are unique", groups = {"regression"})
    public void testAllProductNamesAreUnique() {
        List<String> names = dashboardPage.getAllProductNames();
        long uniqueCount = names.stream().distinct().count();
        Assert.assertEquals(uniqueCount, names.size(), "All product names should be unique");
        System.out.println("TEST PASSED: All " + names.size() + " product names are unique");
    }

    @Test(description = "Products sort correctly from A to Z", groups = {"regression"})
    public void testSortProductsByNameAtoZ() {
        WebElement sortDropdown = driver.findElement(By.className("product_sort_container"));
        new Select(sortDropdown).selectByValue("az");

        List<String> productNames = dashboardPage.getAllProductNames();
        List<String> sortedNames = new ArrayList<>(productNames);
        Collections.sort(sortedNames);

        Assert.assertEquals(productNames, sortedNames, "Products should be sorted A to Z");
        System.out.println("TEST PASSED: Products sorted A to Z correctly");
    }

    @Test(description = "Products sort correctly from Z to A", groups = {"regression"})
    public void testSortProductsByNameZtoA() {
        WebElement sortDropdown = driver.findElement(By.className("product_sort_container"));
        new Select(sortDropdown).selectByValue("za");

        List<String> productNames = dashboardPage.getAllProductNames();
        List<String> sortedNames = new ArrayList<>(productNames);
        sortedNames.sort(Collections.reverseOrder());

        Assert.assertEquals(productNames, sortedNames, "Products should be sorted Z to A");
        System.out.println("TEST PASSED: Products sorted Z to A correctly");
    }

    @Test(description = "Cart badge is not shown when no items have been added", groups = {"smoke"})
    public void testCartBadgeNotShownWithNoItems() {
        Assert.assertFalse(dashboardPage.hasItemsInCart(), "Cart badge should not be visible with an empty cart");
        System.out.println("TEST PASSED: Cart badge not shown with empty cart");
    }

    @Test(description = "Cart badge count updates after adding an item", groups = {"smoke"})
    public void testCartBadgeUpdatesWhenItemAdded() {
        dashboardPage.addProductToCart("Sauce Labs Backpack");

        Assert.assertTrue(dashboardPage.hasItemsInCart(), "Cart badge should appear after adding an item");
        Assert.assertEquals(dashboardPage.getCartItemCount(), 1, "Cart badge should show count of 1");
        System.out.println("TEST PASSED: Cart badge updated correctly after adding item");
    }
}