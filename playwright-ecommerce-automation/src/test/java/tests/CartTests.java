package tests;

import base.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pages.CartPage;
import pages.InventoryPage;
import utils.TestData;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CartTests extends BaseTest {

    private InventoryPage inventory;

    @BeforeEach
    void login() {
        inventory = loginAsStandardUser();
    }

    @Test
    void addingOneProductUpdatesBadgeAndCart() {
        inventory.addToCart(TestData.BACKPACK.name());

        assertThat(inventory.cartBadge()).hasText("1");

        inventory.openCart();
        CartPage cart = new CartPage(page);
        assertThat(page).hasURL(Pattern.compile(".*/cart\\.html"));
        assertThat(cart.items()).hasCount(1);
        assertThat(cart.itemNames()).hasText(TestData.BACKPACK.name());
        assertThat(cart.priceOf(TestData.BACKPACK.name())).hasText(TestData.BACKPACK.priceText());
    }

    @Test
    void addingMultipleProductsShowsAllOfThemInCart() {
        List<TestData.Product> selected =
                List.of(TestData.BACKPACK, TestData.BIKE_LIGHT, TestData.ONESIE);
        selected.forEach(product -> inventory.addToCart(product.name()));

        assertThat(inventory.cartBadge()).hasText("3");

        inventory.openCart();
        CartPage cart = new CartPage(page);
        assertThat(cart.items()).hasCount(3);

        Set<String> expectedNames = new HashSet<>();
        selected.forEach(product -> expectedNames.add(product.name()));
        assertEquals(expectedNames, new HashSet<>(cart.itemNames().allInnerTexts()));

        for (TestData.Product product : selected) {
            assertThat(cart.priceOf(product.name())).hasText(product.priceText());
        }
    }

    @Test
    void productCanBeRemovedFromInventoryPage() {
        inventory.addToCart(TestData.BACKPACK.name());
        assertThat(inventory.cartBadge()).hasText("1");

        inventory.removeFromCart(TestData.BACKPACK.name());

        assertThat(inventory.cartBadge()).hasCount(0);
    }

    @Test
    void productCanBeRemovedFromCartPage() {
        inventory.addToCart(TestData.BACKPACK.name());
        inventory.addToCart(TestData.BIKE_LIGHT.name());
        inventory.openCart();

        CartPage cart = new CartPage(page);
        assertThat(cart.items()).hasCount(2);

        cart.remove(TestData.BACKPACK.name());

        assertThat(cart.items()).hasCount(1);
        assertThat(cart.itemNames()).hasText(TestData.BIKE_LIGHT.name());
        assertThat(cart.cartBadge()).hasText("1");
    }

    @Test
    void cartIsEmptyForNewSession() {
        inventory.openCart();

        CartPage cart = new CartPage(page);
        assertThat(cart.title()).hasText("Your Cart");
        assertThat(cart.items()).hasCount(0);
        assertThat(cart.cartBadge()).hasCount(0);
    }
}
