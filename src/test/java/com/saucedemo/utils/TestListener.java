package com.saucedemo.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.saucedemo.base.BaseTest;
import com.saucedemo.config.ConfigReader;

/**
 * TestNG listener that produces a rich ExtentReports HTML report and
 * attaches a screenshot to every failed test.
 *
 * <p>Registered automatically through
 * {@code META-INF/services/org.testng.ITestNGListener}, so it works from
 * Maven, IntelliJ, or any TestNG runner without extra configuration.</p>
 */
public class TestListener implements ITestListener {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(TestListener.class);

    private static final DateTimeFormatter TIMESTAMP =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private static final ThreadLocal<ExtentTest> CURRENT_TEST =
            new ThreadLocal<>();

    private static ExtentReports extent;
    private static String reportPath;

    @Override
    public synchronized void onStart(ITestContext context) {

        if (extent != null) {
            return;
        }

        try {
            Path directory = Paths.get("target", "extent-reports");
            Files.createDirectories(directory);

            reportPath = directory.resolve(
                    "SauceDemo-Report-"
                            + LocalDateTime.now().format(TIMESTAMP)
                            + ".html").toString();

            ExtentSparkReporter spark =
                    new ExtentSparkReporter(reportPath);

            spark.config().setTheme(Theme.DARK);
            spark.config().setDocumentTitle(
                    "SauceDemo Automation Report");
            spark.config().setReportName(
                    "SauceDemo E2E Test Execution");
            spark.config().setTimelineEnabled(true);

            extent = new ExtentReports();
            extent.attachReporter(spark);

            extent.setSystemInfo("Project", "SauceDemo Automation");
            extent.setSystemInfo("Type", "UI / End-to-End");
            extent.setSystemInfo("Base URL", ConfigReader.get("base.url"));
            extent.setSystemInfo("Browser", ConfigReader.get("browser", "chrome"));
            extent.setSystemInfo("Headless", String.valueOf(
                    ConfigReader.getBoolean("headless", false)));
            extent.setSystemInfo("Java", System.getProperty("java.version"));
            extent.setSystemInfo("OS", System.getProperty("os.name")
                    + " " + System.getProperty("os.version"));
            extent.setSystemInfo("Executed by", System.getProperty("user.name"));

            LOGGER.info("Extent report will be written to {}", reportPath);

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Unable to initialise ExtentReports", e);
        }
    }

    @Override
    public void onTestStart(ITestResult result) {

        String testName = result.getMethod().getMethodName();

        Object[] parameters = result.getParameters();
        if (parameters != null && parameters.length > 0) {
            testName += " ["
                    + Arrays.stream(parameters)
                            .map(p -> p == null ? "null" : p.toString())
                            .collect(Collectors.joining(", "))
                    + "]";
        }

        String description = result.getMethod().getDescription();
        ExtentTest test = getExtent().createTest(
                testName, description == null ? "" : description);

        for (String group : result.getMethod().getGroups()) {
            test.assignCategory(group);
        }

        CURRENT_TEST.set(test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTest test = CURRENT_TEST.get();
        if (test != null) {
            test.pass("Test passed");
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {

        ExtentTest test = CURRENT_TEST.get();
        Throwable error = result.getThrowable();

        if (test == null) {
            return;
        }

        test.fail(error);

        WebDriver driver = getDriver(result);

        if (driver == null) {
            test.warning("Driver was not available for a screenshot");
            return;
        }

        try {
            String base64 = ScreenshotUtils.captureBase64(driver);

            test.addScreenCaptureFromBase64String(
                    base64, "Failure screenshot");

            Path saved = ScreenshotUtils.save(
                    base64, result.getMethod().getMethodName());

            if (saved != null) {
                test.info("Screenshot saved to: " + saved);
            }

        } catch (RuntimeException e) {
            test.warning("Unable to capture screenshot: "
                    + e.getMessage());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest test = CURRENT_TEST.get();
        if (test != null) {
            test.skip(result.getThrowable());
        }
    }

    @Override
    public synchronized void onFinish(ITestContext context) {
        getExtent().flush();
        LOGGER.info("Extent report generated: {}", reportPath);
    }

    private synchronized ExtentReports getExtent() {
        if (extent == null) {
            throw new IllegalStateException(
                    "ExtentReports has not been initialised");
        }
        return extent;
    }

    private WebDriver getDriver(ITestResult result) {

        Object instance = result.getInstance();

        if (instance instanceof BaseTest baseTest) {
            return baseTest.getDriver();
        }

        return null;
    }
}
