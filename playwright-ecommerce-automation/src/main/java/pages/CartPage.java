package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

/** Page object for the shopping cart page. */
public class CartPage {

    private final Page page;

    public CartPage(Page page) {
        this.page = page;
    }

    public Locator title() {
        return page.getByTestId("title");
    }

    public Locator items() {
        return page.getByTestId("inventory-item");
    }

    public Locator itemNames() {
        return page.getByTestId("inventory-item-name");
    }

    /** Price element of one specific product in the cart. */
    public Locator priceOf(String productName) {
        return item(productName).getByTestId("inventory-item-price");
    }

    public void remove(String productName) {
        item(productName)
                .getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("Remove"))
                .click();
    }

    public Locator cartBadge() {
        return page.getByTestId("shopping-cart-badge");
    }

    public void continueShopping() {
        page.getByTestId("continue-shopping").click();
    }

    public void checkout() {
        page.getByTestId("checkout").click();
    }

    private Locator item(String productName) {
        return items().filter(new Locator.FilterOptions().setHasText(productName));
    }
}
