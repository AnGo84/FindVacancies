package ua.findvacancies.common.scraper;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

public final class WebDriverService {

    // --- Static Initializer for One-Time Setup ---
    static {
        // Guarantee WebDriverManager setup runs exactly once per JVM lifecycle
        try {
            WebDriverManager.chromedriver().setup();
        } catch (Exception e) {
            throw new WebDriverException("Failed to setup WebDriver dependencies.", e);
        }
    }

    private WebDriverService() {
    }

    /**
     * Initializes a new WebDriver instance (if one doesn't exist for the current thread)
     * and stores it in ThreadLocal storage.
     *
     * @return The WebDriver instance for the current thread.
     */
    public static WebDriver getDriver() {
        WebDriver driver = new ChromeDriver(getChromeOptions());
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();

        return driver;
    }

    private static ChromeOptions getChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        //options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage");
        //options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        return options;
    }

}
