package com.trading.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import java.util.List;

public class DashboardPage extends BasePage {
    private WebDriverWait shortWait;

    // Selectores del Dashboard
    private By appLogo = By.className("app_logo");
    private By inventoryContainer = By.id("inventory_container");
    private By inventoryItems = By.className("inventory_item");
    private By shoppingCart = By.className("shopping_cart_link");
    private By productNames = By.className("inventory_item_name");
    private By addToCartButtons = By.xpath("//button[contains(text(), 'Add to cart')]");
    private By cartBadge = By.className("shopping_cart_badge");

    public DashboardPage(WebDriver driver) {
        super(driver);
        this.shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    /**
     * Verifica si el Dashboard se mostró correctamente
     */
    public boolean isDashboardDisplayed() {
        try {
            // Esperar a que el contenedor de inventario sea visible
            WebElement inventory = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(inventoryContainer)
            );
            return inventory.isDisplayed();
        } catch (Exception e) {
            System.out.println("Dashboard not displayed. URL: " + driver.getCurrentUrl() + " | " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene la cantidad de productos en el dashboard
     */
    public int getProductCount() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(inventoryContainer));
            return driver.findElements(inventoryItems).size();
        } catch (Exception e) {
            System.out.println("Error getting product count: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Verifica si el logo es visible
     */
    public boolean isLogoDisplayed() {
        try {
            WebElement logo = wait.until(ExpectedConditions.visibilityOfElementLocated(appLogo));
            return logo.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Obtiene el texto del logo
     */
    public String getLogoText() {
        try {
            WebElement logo = wait.until(ExpectedConditions.visibilityOfElementLocated(appLogo));
            return logo.getText();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Agrega un producto al carrito por nombre
     */
    public DashboardPage addProductToCart(String productName) {
        return retry("add '" + productName + "' to cart", 3, () -> {
            wait.until(ExpectedConditions.visibilityOfElementLocated(inventoryContainer));
            List<WebElement> products = driver.findElements(inventoryItems);

            for (WebElement product : products) {
                WebElement name = product.findElement(productNames);

                if (name.getText().equals(productName)) {
                    WebElement addButton = product.findElement(
                            By.xpath(".//button[contains(text(), 'Add to cart')]")
                    );
                    // SauceDemo mutates the button text in-place (Add to cart → Remove) without
                    // replacing the element, so stalenessOf never fires. Poll for the Remove button.
                    By removeLocator = By.xpath(".//button[contains(text(),'Remove')]");
                    clickAndVerify(addButton, d -> !product.findElements(removeLocator).isEmpty());
                    System.out.println("Added to cart: " + productName);
                    return this;
                }
            }

            throw new IllegalStateException("Product not found: " + productName);
        });
    }

    /**
     * Agrega un producto al carrito por índice (posición)
     */
    public DashboardPage addProductToCartByIndex(int index) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(inventoryContainer));
            List<WebElement> buttons = driver.findElements(addToCartButtons);

            if (index < 0 || index >= buttons.size()) {
                throw new RuntimeException("Invalid product index: " + index);
            }

            wait.until(ExpectedConditions.elementToBeClickable(buttons.get(index))).click();
            System.out.println("Added product at index " + index + " to cart");
            return this;

        } catch (Exception e) {
            System.out.println("Error adding product by index: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Obtiene el número de items en el carrito
     */
    public int getCartItemCount() {
        try {
            WebElement badge = shortWait.until(ExpectedConditions.visibilityOfElementLocated(cartBadge));
            String countText = badge.getText();
            return Integer.parseInt(countText);
        } catch (Exception e) {
            System.out.println("No items in cart");
            return 0;
        }
    }

    /**
     * Verifica si hay items en el carrito
     */
    public boolean hasItemsInCart() {
        try {
            shortWait.until(ExpectedConditions.visibilityOfElementLocated(cartBadge));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Click en el carrito de compras
     */
    public CartPage clickShoppingCart() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(shoppingCart)).click();
            System.out.println("Clicked shopping cart");
            return new CartPage(driver);
        } catch (Exception e) {
            System.out.println("Error clicking shopping cart: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Obtiene el primer producto visible
     */
    public WebElement getFirstProduct() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(inventoryContainer));
            return driver.findElements(inventoryItems).get(0);
        } catch (Exception e) {
            System.out.println("Error getting first product: " + e.getMessage());
            return null;
        }
    }

    /**
     * Obtiene una lista de todos los nombres de productos
     */
    public List<String> getAllProductNames() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(inventoryContainer));
            List<WebElement> products = driver.findElements(productNames);
            List<String> names = new java.util.ArrayList<>();

            for (WebElement product : products) {
                names.add(product.getText());
            }

            return names;
        } catch (Exception e) {
            System.out.println("Error getting product names: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    /**
     * Agrega múltiples productos al carrito
     */
    public DashboardPage addMultipleProductsToCart(String... productNames) {
        for (String productName : productNames) {
            addProductToCart(productName);
        }
        return this;
    }
}