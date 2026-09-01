package com.trading.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.concurrent.Callable;

public abstract class BasePage {
    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final WebDriverWait verifyWait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        this.verifyWait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    /**
     * Clicks the element at clickLocator, then confirms the click actually took effect via
     * verification. In headless CI this app's React click handlers occasionally drop a click's
     * effect (the click registers with no exception, but the resulting DOM update - a button label
     * flip, an SPA route change - never happens), so a bare click() is never trusted on its own.
     */
    protected void clickAndVerify(By clickLocator, ExpectedCondition<?> verification) {
        wait.until(ExpectedConditions.elementToBeClickable(clickLocator)).click();
        verifyWait.until(verification);
    }

    protected void clickAndVerify(WebElement element, ExpectedCondition<?> verification) {
        wait.until(ExpectedConditions.elementToBeClickable(element)).click();
        verifyWait.until(verification);
    }

    /**
     * Retries action up to maxAttempts times, treating Timeout/StaleElement/ClickIntercepted/
     * NoSuchElement failures as retryable. Any other exception fails fast without retrying.
     */
    protected <T> T retry(String actionName, int maxAttempts, Callable<T> action) {
        RuntimeException lastFailure = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return action.call();
            } catch (TimeoutException | StaleElementReferenceException
                     | ElementClickInterceptedException | NoSuchElementException e) {
                System.out.println("Attempt " + attempt + "/" + maxAttempts + " failed: " + actionName
                        + ": " + e.getClass().getSimpleName() + ": " + e.getMessage());
                lastFailure = new RuntimeException(
                        "Failed: " + actionName + " after " + maxAttempts + " attempts", e);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        throw lastFailure;
    }
}
