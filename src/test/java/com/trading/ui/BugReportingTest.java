package com.trading.ui;

import com.trading.base.BaseTest;
import com.trading.ui.pages.LoginPage;
import com.trading.ui.pages.DashboardPage;
import com.trading.utils.BugEvidenceCapture;
import com.trading.utils.BugEvidenceCapture.BugEvidence;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class BugReportingTest extends BaseTest {

    private BugEvidenceCapture evidenceCapture;

    @BeforeMethod
    public void navigateToLoginPage() {
        resetToLoginPage();
        System.out.println("Setup: Navigated to login page");
    }

    @Test(description = "Example: Capture evidence when test fails", groups = {"smoke"})
    public void testLoginWithBugEvidence() {
        evidenceCapture = new BugEvidenceCapture(driver);

        try {
            LoginPage loginPage = new LoginPage(driver);

            // Intentar login con credenciales válidas
            DashboardPage dashboardPage = loginPage.login("standard_user", "secret_sauce");

            // Si el login no se completa, capturar evidencia
            Assert.assertTrue(dashboardPage.isDashboardDisplayed(),
                    "Dashboard should be displayed after successful login");

            System.out.println("TEST PASSED: Login successful with bug evidence capture enabled");

        } catch (AssertionError e) {
            // Capturar evidencia cuando falla
            BugEvidence evidence = evidenceCapture.captureBugEvidence("LoginFailed");

            System.out.println("BUG DETECTED!");
            System.out.println("Evidence captured: " + evidence);
            System.out.println("Screenshot: " + evidence.getScreenshotPath());
            System.out.println("URL: " + evidence.getCurrentUrl());
            System.out.println("Page Title: " + evidence.getPageTitle());

            // Lanzar el error para que TestNG vea que falló
            throw e;
        } catch (Exception e) {
            System.out.println("Test execution error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Test(description = "Test with special characters - capture evidence if fails", groups = {"regression"})
    public void testLoginWithSpecialCharsAndEvidence() {
        evidenceCapture = new BugEvidenceCapture(driver);

        try {
            LoginPage loginPage = new LoginPage(driver);

            // Intentar login con caracteres especiales
            loginPage.enterUsername("standard_user");
            loginPage.enterPassword("secret_sauce!@#$");
            DashboardPage dashboardPage = loginPage.clickLoginButton();

            // Esperar un poco para que la página cargue
            Thread.sleep(3000);

            // Si no llega al dashboard, capturar bug
            Assert.assertTrue(dashboardPage.isDashboardDisplayed(),
                    "Dashboard should be displayed even with special chars in password");

            System.out.println("TEST PASSED: Login works with special characters");

        } catch (AssertionError e) {
            // Capturar evidencia de bug
            BugEvidence evidence = evidenceCapture.captureBugEvidence("LoginFailedSpecialChars");

            System.out.println("\n=== BUG EVIDENCE CAPTURED ===");
            System.out.println("Screenshot: " + evidence.getScreenshotPath());
            System.out.println("URL: " + evidence.getCurrentUrl());
            System.out.println("Page Title: " + evidence.getPageTitle());
            System.out.println("Timestamp: " + evidence.getTimestamp());
            System.out.println("=============================\n");

            throw e;
        } catch (Exception e) {
            System.out.println("Test execution error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}