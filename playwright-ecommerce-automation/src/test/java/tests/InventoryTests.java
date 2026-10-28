package tests;

import base.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pages.InventoryPage;
import pages.InventoryPage.SortOption;
import pages.ProductDetailsPage;
import utils.PriceUtils;
import utils.TestData;
import utils.TestData.Product;

import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class InventoryTests extends BaseTest {

    private InventoryPage inventory;

    @BeforeEach
    void login() {
        inventory = loginAsStandardUser();
    }

    @Test
    void allProductsAreListed() {
        assertThat(inventory.items()).hasCount(TestData.ALL_PRODUCTS.size());
    }

    @Test
    void productNamesAndPricesAreCorrect() {
        String[] expectedNames = TestData.ALL_PRODUCTS.stream().map(Product::name).toArray(String[]::new);
        String[] expectedPrices = TestData.ALL_PRODUCTS.stream().map(Product::priceText).toArray(String[]::new);

        assertThat(inventory.itemNames()).hasText(expectedNames);
        assertThat(inventory.itemPrices()).hasText(expectedPrices);
    }

    @Test
    void productsCanBeSortedByNameZToA() {
        List<String> expected = TestData.ALL_PRODUCTS.stream()
                .map(Product::name)
                .sorted(Comparator.reverseOrder())
                .toList();

        inventory.sortBy(SortOption.NAME_Z_TO_A);

        assertThat(inventory.itemNames()).hasText(expected.toArray(new String[0]));
    }

    @Test
    void productsCanBeSortedByNameAToZ() {
        List<String> expected = TestData.ALL_PRODUCTS.stream()
                .map(Product::name)
                .sorted()
                .toList();

        inventory.sortBy(SortOption.NAME_Z_TO_A);   // change the order first so the test is meaningful
        inventory.sortBy(SortOption.NAME_A_TO_Z);

        assertThat(inventory.itemNames()).hasText(expected.toArray(new String[0]));
    }

    @Test
    void productsCanBeSortedByPriceLowToHigh() {
        String[] expected = TestData.ALL_PRODUCTS.stream()
                .map(Product::price)
                .sorted()
                .map(PriceUtils::format)
                .toArray(String[]::new);

        inventory.sortBy(SortOption.PRICE_LOW_TO_HIGH);

        assertThat(inventory.itemPrices()).hasText(expected);
    }

    @Test
    void productsCanBeSortedByPriceHighToLow() {
        String[] expected = TestData.ALL_PRODUCTS.stream()
                .map(Product::price)
                .sorted(Comparator.reverseOrder())
                .map(PriceUtils::format)
                .toArray(String[]::new);

        inventory.sortBy(SortOption.PRICE_HIGH_TO_LOW);

        assertThat(inventory.itemPrices()).hasText(expected);
    }

    @Test
    void productDetailsPageShowsSelectedProduct() {
        inventory.openProductDetails(TestData.BACKPACK.name());

        ProductDetailsPage details = new ProductDetailsPage(page);
        assertThat(page).hasURL(Pattern.compile(".*/inventory-item\\.html\\?id=\\d+"));
        assertThat(details.name()).hasText(TestData.BACKPACK.name());
        assertThat(details.price()).hasText(TestData.BACKPACK.priceText());
        assertThat(details.description()).isVisible();

        details.backToProducts();
        assertThat(page).hasURL(Pattern.compile(".*/inventory\\.html"));
    }
}
