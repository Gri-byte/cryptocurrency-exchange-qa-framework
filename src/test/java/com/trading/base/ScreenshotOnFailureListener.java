package com.trading.base;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Captures a screenshot, page source, and current URL for every failed test. CI-only failures
 * (that don't reproduce locally) are otherwise impossible to diagnose from a timeout message
 * alone - this turns the next one into actual evidence of what the browser was seeing.
 */
public class ScreenshotOnFailureListener implements ITestListener {
    private static final Path EVIDENCE_DIR = Paths.get("target", "failure-evidence");

    @Override
    public void onTestFailure(ITestResult result) {
        Object instance = result.getInstance();
        if (!(instance instanceof BaseTest)) {
            return;
        }

        WebDriver driver = ((BaseTest) instance).driver;
        if (driver == null) {
            return;
        }

        String name = result.getTestClass().getRealClass().getSimpleName() + "."
                + result.getMethod().getMethodName() + "_"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));

        try {
            Files.createDirectories(EVIDENCE_DIR);

            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(screenshot.toPath(), EVIDENCE_DIR.resolve(name + ".png"),
                    StandardCopyOption.REPLACE_EXISTING);

            Files.writeString(EVIDENCE_DIR.resolve(name + ".html"), driver.getPageSource());
            Files.writeString(EVIDENCE_DIR.resolve(name + ".url.txt"), driver.getCurrentUrl());

            System.out.println("Failure evidence captured: " + EVIDENCE_DIR.resolve(name));
        } catch (IOException | RuntimeException e) {
            System.out.println("Failed to capture failure evidence: " + e.getMessage());
        }
    }
}
