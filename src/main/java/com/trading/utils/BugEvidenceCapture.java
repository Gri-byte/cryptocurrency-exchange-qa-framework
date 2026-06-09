package com.trading.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BugEvidenceCapture {

    private WebDriver driver;
    private static final String EVIDENCE_FOLDER = "target/bug-evidence/";

    public BugEvidenceCapture(WebDriver driver) {
        this.driver = driver;
        createEvidenceFolder();
    }

    /**
     * Crea la carpeta para guardar evidencia si no existe
     */
    private void createEvidenceFolder() {
        File folder = new File(EVIDENCE_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
            System.out.println("Evidence folder created: " + EVIDENCE_FOLDER);
        }
    }

    /**
     * Captura screenshot con timestamp
     * @param testName - nombre del test que falló
     * @return ruta del archivo
     */
    public String captureScreenshot(String testName) {
        try {
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));

            String fileName = EVIDENCE_FOLDER + testName + "_" + timestamp + ".png";

            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destFile = new File(fileName);

            FileUtils.copyFile(srcFile, destFile);

            System.out.println("Screenshot captured: " + fileName);
            return fileName;

        } catch (Exception e) {
            System.out.println("Failed to capture screenshot: " + e.getMessage());
            return null;
        }
    }

    /**
     * Captura screenshot y logs del navegador
     * @param testName
     * @return objeto con datos de evidencia
     */
    public BugEvidence captureBugEvidence(String testName) {
        BugEvidence evidence = new BugEvidence();

        // Capturar screenshot
        String screenshotPath = captureScreenshot(testName);
        evidence.setScreenshotPath(screenshotPath);

        // Capturar URL actual
        evidence.setCurrentUrl(driver.getCurrentUrl());

        // Capturar título de la página
        evidence.setPageTitle(driver.getTitle());

        // Capturar HTML de la página
        evidence.setPageSource(driver.getPageSource());

        // Timestamp
        evidence.setTimestamp(LocalDateTime.now().toString());

        return evidence;
    }

    /**
     * Clase para almacenar evidencia de bug
     */
    public static class BugEvidence {
        private String screenshotPath;
        private String currentUrl;
        private String pageTitle;
        private String pageSource;
        private String timestamp;

        // Getters y Setters
        public String getScreenshotPath() { return screenshotPath; }
        public void setScreenshotPath(String screenshotPath) { this.screenshotPath = screenshotPath; }

        public String getCurrentUrl() { return currentUrl; }
        public void setCurrentUrl(String currentUrl) { this.currentUrl = currentUrl; }

        public String getPageTitle() { return pageTitle; }
        public void setPageTitle(String pageTitle) { this.pageTitle = pageTitle; }

        public String getPageSource() { return pageSource; }
        public void setPageSource(String pageSource) { this.pageSource = pageSource; }

        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

        @Override
        public String toString() {
            return "BugEvidence{" +
                    "screenshotPath='" + screenshotPath + '\'' +
                    ", currentUrl='" + currentUrl + '\'' +
                    ", pageTitle='" + pageTitle + '\'' +
                    ", timestamp='" + timestamp + '\'' +
                    '}';
        }
    }
}