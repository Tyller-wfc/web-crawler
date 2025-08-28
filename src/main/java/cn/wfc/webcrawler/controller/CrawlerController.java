package cn.wfc.webcrawler.controller;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class CrawlerController {
    @GetMapping("/crawler")
    public Set<String> crawler(String url) {
        String BASE_URL = url;
        // 自动管理 chromedriver
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // 先不要 headless，调试时可见
        options.addArguments("--remote-allow-origins=*");

        WebDriver driver = new ChromeDriver(options);
        Set<String> secondLevel = new HashSet<>();

        try {
            driver.get(BASE_URL);
            Thread.sleep(5000);

            List<WebElement> links = driver.findElements(By.tagName("a"));

            for (WebElement link : links) {
                String href = link.getAttribute("href");
//                System.out.println("发现链接：" + href);

                if (href != null) {
                    if (href.startsWith("#/")) {
                        try {
                            URL base = new URL(BASE_URL);
                            URL fullUrl = new URL(base, href);

                            String[] parts = href.split("/");
                            int count = 0;
                            for (String p : parts) {
                                if (!p.equals("#") && !p.isEmpty()) {
                                    count++;
                                }
                            }

                            if (count >= 2) {
                                secondLevel.add(fullUrl.toString());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        secondLevel.add(href);
                    }
                }
            }
            //过滤到不是http开头的数据
            secondLevel = secondLevel.stream()
                    .filter(u -> u.startsWith("http"))
                    .collect(Collectors.toSet());
            System.out.println("二级链接：");
            if (secondLevel.isEmpty()) {
                System.out.println("⚠ 没有获取到任何二级链接，请确认页面是否在 JS 渲染后才生成。");
            } else {
                secondLevel.stream().sorted().forEach(System.out::println);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
        return secondLevel;
    }
}
