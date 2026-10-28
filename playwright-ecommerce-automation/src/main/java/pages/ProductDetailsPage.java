package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/** Page object for a single product's detail page. */
public class ProductDetailsPage {

    private final Page page;

    public ProductDetailsPage(Page page) {
        this.page = page;
    }

    public Locator name() {
        return page.getByTestId("inventory-item-name");
    }

    public Locator price() {
        return page.getByTestId("inventory-item-price");
    }

    public Locator description() {
        return page.getByTestId("inventory-item-desc");
    }

    public void backToProducts() {
        page.getByTestId("back-to-products").click();
    }
}
