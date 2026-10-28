package base;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import pages.InventoryPage;
import pages.LoginPage;
import utils.Config;
import utils.TestData;

import java.nio.file.Files;

/**
 * Base class for all tests.
 *
 * Lifecycle:
 *  - once per test class: start Playwright and launch one Browser
 *  - once per test: create a fresh BrowserContext + Page (isolated cookies/storage)
 *  - after each test: save a screenshot and trace if the test failed, then close the context
 */
public abstract class BaseTest {

    private static Playwright playwright;
    private static Browser browser;

    protected BrowserContext context;
    protected Page page;

    private boolean testFailed;

    /** Called by JUnit when a test method throws, i.e. before @AfterEach runs. */
    @RegisterExtension
    final TestExecutionExceptionHandler failureRecorder = (extensionContext, throwable) -> {
        testFailed = true;
        throw throwable;
    };

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        // SauceDemo uses "data-test" instead of Playwright's default "data-testid".
        playwright.selectors().setTestIdAttribute("data-test");

        BrowserType browserType = switch (Config.browser()) {
            case "chromium" -> playwright.chromium();
            case "firefox" -> playwright.firefox();
            case "webkit" -> playwright.webkit();
            default -> throw new IllegalArgumentException(
                    "Unsupported browser '" + Config.browser() + "'. Use chromium, firefox or webkit.");
        };

        browser = browserType.launch(new BrowserType.LaunchOptions()
                .setHeadless(Config.headless())
                .setSlowMo(Config.slowMo()));
    }

    @AfterAll
    static void closeBrowser() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    @BeforeEach
    void createContext() {
        testFailed = false;
        context = browser.newContext(new Browser.NewContextOptions()
                .setBaseURL(Config.baseUrl())
                .setViewportSize(1280, 720));
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true));
        page = context.newPage();
    }

    @AfterEach
    void closeContext(TestInfo testInfo) throws Exception {
        try {
            if (testFailed) {
                Files.createDirectories(Config.artifactsDir());
                String name = artifactName(testInfo);
                page.screenshot(new Page.ScreenshotOptions()
                        .setPath(Config.artifactsDir().resolve(name + ".png"))
                        .setFullPage(true));
                context.tracing().stop(new Tracing.StopOptions()
                        .setPath(Config.artifactsDir().resolve(name + "-trace.zip")));
            } else {
                context.tracing().stop(); // discard the trace of passing tests
            }
        } finally {
            context.close();
        }
    }

    /** Logs in as the standard demo user and returns the inventory page. */
    protected InventoryPage loginAsStandardUser() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.navigate();
        loginPage.login(TestData.STANDARD_USER, TestData.PASSWORD);
        return new InventoryPage(page);
    }

    private static String artifactName(TestInfo testInfo) {
        String className = testInfo.getTestClass().map(Class::getSimpleName).orElse("UnknownClass");
        String raw = className + "_" + testInfo.getDisplayName() + "_" + Config.browser();
        return raw.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
