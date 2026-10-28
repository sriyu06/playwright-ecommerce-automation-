package tests;

import base.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import pages.InventoryPage;
import pages.LoginPage;
import utils.TestData;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class LoginTests extends BaseTest {

    private LoginPage loginPage;

    @BeforeEach
    void openLoginPage() {
        loginPage = new LoginPage(page);
        loginPage.navigate();
    }

    @Test
    void validUserCanLogin() {
        loginPage.login(TestData.STANDARD_USER, TestData.PASSWORD);

        assertThat(page).hasURL(Pattern.compile(".*/inventory\\.html"));
        assertThat(new InventoryPage(page).title()).hasText("Products");
    }

    @ParameterizedTest
    @CsvSource({
            "invalid_user, secret_sauce",
            "standard_user, wrong_password"
    })
    void invalidCredentialsShowError(String username, String password) {
        loginPage.login(username, password);

        assertThat(loginPage.errorMessage())
                .containsText("Username and password do not match any user in this service");
        assertThat(loginPage.loginButton()).isVisible();
    }

    @Test
    void emptyUsernameAndPasswordShowUsernameRequired() {
        loginPage.login("", "");

        assertThat(loginPage.errorMessage()).containsText("Username is required");
    }

    @Test
    void emptyPasswordShowsPasswordRequired() {
        loginPage.login(TestData.STANDARD_USER, "");

        assertThat(loginPage.errorMessage()).containsText("Password is required");
    }

    @Test
    void lockedOutUserCannotLogin() {
        loginPage.login(TestData.LOCKED_OUT_USER, TestData.PASSWORD);

        assertThat(loginPage.errorMessage()).containsText("Sorry, this user has been locked out");
        assertThat(page).not().hasURL(Pattern.compile(".*/inventory\\.html"));
    }
}
