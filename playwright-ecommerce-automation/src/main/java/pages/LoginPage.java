package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/** Page object for the SauceDemo login screen. */
public class LoginPage {

    private final Page page;
    private final Locator usernameInput;
    private final Locator passwordInput;
    private final Locator loginButton;
    private final Locator errorMessage;

    public LoginPage(Page page) {
        this.page = page;
        // getByTestId uses the "data-test" attribute (configured in BaseTest).
        this.usernameInput = page.getByTestId("username");
        this.passwordInput = page.getByTestId("password");
        this.loginButton = page.getByTestId("login-button");
        this.errorMessage = page.getByTestId("error");
    }

    /** Opens the application's home page (relative to the baseURL set on the context). */
    public void navigate() {
        page.navigate("/");
    }

    public void login(String username, String password) {
        usernameInput.fill(username);
        passwordInput.fill(password);
        loginButton.click();
    }

    public Locator loginButton() {
        return loginButton;
    }

    public Locator errorMessage() {
        return errorMessage;
    }
}
