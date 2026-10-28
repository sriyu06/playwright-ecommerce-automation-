package tests;

import base.BaseTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import pages.CartPage;
import pages.CheckoutPage;
import pages.InventoryPage;
import utils.PriceUtils;
import utils.TestData;
import utils.TestData.Product;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CheckoutTests extends BaseTest {

    /** Logs in, adds the given products and opens checkout step one. */
    private CheckoutPage startCheckoutWith(Product... products) {
        InventoryPage inventory = loginAsStandardUser();
        for (Product product : products) {
            inventory.addToCart(product.name());
        }
        inventory.openCart();
        new CartPage(page).checkout();
        return new CheckoutPage(page);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "'', '', '', Error: First Name is required",
            "'', User, 500001, Error: First Name is required",
            "Test, '', 500001, Error: Last Name is required",
            "Test, User, '', Error: Postal Code is required"
    })
    void requiredCustomerFieldsAreValidated(String firstName, String lastName, String postalCode,
                                            String expectedError) {
        CheckoutPage checkout = startCheckoutWith(TestData.BACKPACK);

        checkout.fillCustomerInformation(firstName, lastName, postalCode);
        checkout.clickContinue();

        assertThat(checkout.errorMessage()).containsText(expectedError);
        assertThat(page).hasURL(Pattern.compile(".*/checkout-step-one\\.html"));
    }

    @Test
    void overviewShowsSelectedItemsAndConsistentTotals() {
        CheckoutPage checkout = startCheckoutWith(TestData.BACKPACK, TestData.BIKE_LIGHT);

        checkout.fillCustomerInformation(TestData.FIRST_NAME, TestData.LAST_NAME, TestData.POSTAL_CODE);
        checkout.clickContinue();

        assertThat(page).hasURL(Pattern.compile(".*/checkout-step-two\\.html"));
        assertThat(checkout.title()).hasText("Checkout: Overview");
        assertThat(checkout.itemNames()).hasCount(2);
        assertThat(checkout.subtotalLabel()).containsText("Item total:");

        Set<String> expectedNames = Set.of(TestData.BACKPACK.name(), TestData.BIKE_LIGHT.name());
        assertEquals(expectedNames, new HashSet<>(checkout.itemNames().allInnerTexts()));

        BigDecimal itemsSum = PriceUtils.sum(checkout.itemPrices().allInnerTexts());
        BigDecimal subtotal = PriceUtils.parse(checkout.subtotalLabel().innerText());
        BigDecimal tax = PriceUtils.parse(checkout.taxLabel().innerText());
        BigDecimal total = PriceUtils.parse(checkout.totalLabel().innerText());

        assertEquals(TestData.BACKPACK.price().add(TestData.BIKE_LIGHT.price()), itemsSum,
                "Sum of displayed item prices");
        assertEquals(itemsSum, subtotal, "Item total should equal the sum of item prices");
        assertEquals(subtotal.add(tax), total, "Total should equal item total plus tax");
    }

    @Test
    void userCanCompleteAPurchase() {
        CheckoutPage checkout = startCheckoutWith(TestData.BACKPACK);

        checkout.fillCustomerInformation(TestData.FIRST_NAME, TestData.LAST_NAME, TestData.POSTAL_CODE);
        checkout.clickContinue();

        assertThat(checkout.subtotalLabel()).hasText("Item total: $29.99");
        assertThat(checkout.taxLabel()).hasText("Tax: $2.40");
        assertThat(checkout.totalLabel()).hasText("Total: $32.39");

        checkout.finish();

        assertThat(page).hasURL(Pattern.compile(".*/checkout-complete\\.html"));
        assertThat(checkout.completeHeader()).hasText("Thank you for your order!");
        assertThat(new InventoryPage(page).cartBadge()).hasCount(0);

        checkout.backToProducts();
        assertThat(page).hasURL(Pattern.compile(".*/inventory\\.html"));
    }
}
