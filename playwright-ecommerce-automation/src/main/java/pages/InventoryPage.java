package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

/** Page object for the product listing ("Products") page. */
public class InventoryPage {

    /** Values of the sort dropdown on the page. */
    public enum SortOption {
        NAME_A_TO_Z("az"),
        NAME_Z_TO_A("za"),
        PRICE_LOW_TO_HIGH("lohi"),
        PRICE_HIGH_TO_LOW("hilo");

        private final String value;

        SortOption(String value) {
            this.value = value;
        }
    }

    private final Page page;

    public InventoryPage(Page page) {
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

    public Locator itemPrices() {
        return page.getByTestId("inventory-item-price");
    }

    public Locator cartBadge() {
        return page.getByTestId("shopping-cart-badge");
    }

    public void sortBy(SortOption option) {
        page.getByTestId("product-sort-container").selectOption(option.value);
    }

    public void addToCart(String productName) {
        item(productName)
                .getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("Add to cart"))
                .click();
    }

    public void removeFromCart(String productName) {
        item(productName)
                .getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("Remove"))
                .click();
    }

    public void openProductDetails(String productName) {
        itemNames().filter(new Locator.FilterOptions().setHasText(productName)).click();
    }

    public void openCart() {
        page.getByTestId("shopping-cart-link").click();
    }

    /** A single product card located by its name. */
    private Locator item(String productName) {
        return items().filter(new Locator.FilterOptions().setHasText(productName));
    }
}
