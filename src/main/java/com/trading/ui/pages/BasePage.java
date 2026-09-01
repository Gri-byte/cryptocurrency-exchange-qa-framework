package com.trading.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
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
    private final WebDriverWait nativeClickWait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        this.verifyWait = new WebDriverWait(driver, Duration.ofSeconds(15));
        // Screenshot evidence on CI showed the native click reporting success (no exception) while
        // producing zero DOM effect even after 90s of retrying - not a slow render, a click that
        // never dispatched at all. So don't wait long for it: if it hasn't landed quickly, fall back
        // to a JS-executed click (calls the DOM's click() directly, bypassing whatever native input
        // dispatch is failing) rather than burning the retry budget waiting on the same broken click.
        this.nativeClickWait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    /**
     * Clicks the element at clickLocator, then confirms the click actually took effect via
     * verification, falling back to a JavaScript-executed click if the native click produced no
     * effect.
     */
    protected void clickAndVerify(By clickLocator, ExpectedCondition<?> verification) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(clickLocator));
        clickAndVerify(element, verification);
    }

    protected void clickAndVerify(WebElement element, ExpectedCondition<?> verification) {
        settleAfterRender();
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.click();

        try {
            nativeClickWait.until(verification);
            return;
        } catch (TimeoutException e) {
            System.out.println("Native click produced no DOM effect; retrying via JavaScript click");
        }

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        verifyWait.until(verification);
    }

    /**
     * Short pause before firing a click. On the slower headless-CI runner, an element can satisfy
     * elementToBeClickable (visible + enabled) before React has finished re-attaching its click
     * handler following the previous DOM update, so the click lands on an unwired element and does
     * nothing. This consistently reproduces on the second consecutive UI action in a test (never
     * the first), so give React's re-render/hydration cycle a moment to catch up before clicking.
     */
    private void settleAfterRender() {
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
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
