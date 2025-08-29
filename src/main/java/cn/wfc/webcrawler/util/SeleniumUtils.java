package cn.wfc.webcrawler.util;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

public class SeleniumUtils {

    /**
     * 等待页面完全加载 (document.readyState == "complete")
     */
    public static void waitForPageLoad(WebDriver driver, int timeoutInSeconds) {
        new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds))
                .until(webDriver -> ((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState").equals("complete"));
    }

    /**
     * 等待某个元素加载完成（出现并可见）
     */
    public static WebElement waitForElementVisible(WebDriver driver, By locator, int timeoutInSeconds) {
        WebElement element;
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            element = wait.until(ExpectedConditions.presenceOfElementLocated(locator)); // 先确保存在
            element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator)); // 再确保可见
        } catch (TimeoutException e) {
            System.err.println("当前页面标题: " + driver.getTitle());
            System.err.println("当前URL: " + driver.getCurrentUrl());
            // 可选：输出部分源码，方便排查
            String pageSource = driver.getPageSource();
            System.err.println("页面源码前500字符: " + pageSource.substring(0, Math.min(500, pageSource.length())));
            throw e;
        }
        System.out.println("元素出现并可见: " + locator.toString());
        return element;
    }


    /**
     * 组合等待：先等页面加载完成，再等关键元素出现
     */
    public static WebElement waitForPageAndElement(WebDriver driver, By locator, int timeoutInSeconds) {
        waitForPageLoad(driver, timeoutInSeconds);
        return waitForElementVisible(driver, locator, timeoutInSeconds);
    }
}

