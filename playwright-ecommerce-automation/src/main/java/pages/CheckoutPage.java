package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Page object covering the three checkout screens:
 * step one (customer information), step two (overview) and the confirmation page.
 */
public class CheckoutPage {

    private final Page page;

    public CheckoutPage(Page page) {
        this.page = page;
    }

    public Locator title() {
        return page.getByTestId("title");
    }

    // ----- Step one: customer information -----

    public void fillCustomerInformation(String firstName, String lastName, String postalCode) {
        page.getByTestId("firstName").fill(firstName);
        page.getByTestId("lastName").fill(lastName);
        page.getByTestId("postalCode").fill(postalCode);
    }

    public void clickContinue() {
        page.getByTestId("continue").click();
    }

    public Locator errorMessage() {
        return page.getByTestId("error");
    }

    // ----- Step two: overview -----

    public Locator itemNames() {
        return page.getByTestId("inventory-item-name");
    }

    public Locator itemPrices() {
        return page.getByTestId("inventory-item-price");
    }

    public Locator subtotalLabel() {
        return page.getByTestId("subtotal-label");
    }

    public Locator taxLabel() {
        return page.getByTestId("tax-label");
    }

    public Locator totalLabel() {
        return page.getByTestId("total-label");
    }

    public void finish() {
        page.getByTestId("finish").click();
    }

    // ----- Confirmation page -----

    public Locator completeHeader() {
        return page.getByTestId("complete-header");
    }

    public void backToProducts() {
        page.getByTestId("back-to-products").click();
    }
}
