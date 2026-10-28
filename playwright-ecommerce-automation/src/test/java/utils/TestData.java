package utils;

import java.math.BigDecimal;
import java.util.List;

/**
 * Test data. The credentials below are the public demo credentials that
 * SauceDemo prints on its own login page - they are not secrets.
 */
public final class TestData {

    public static final String STANDARD_USER = "standard_user";
    public static final String LOCKED_OUT_USER = "locked_out_user";
    public static final String PASSWORD = "secret_sauce";

    public static final String FIRST_NAME = "Test";
    public static final String LAST_NAME = "User";
    public static final String POSTAL_CODE = "500001";

    /** A product as displayed by the application. */
    public record Product(String name, BigDecimal price) {
        public String priceText() {
            return PriceUtils.format(price);
        }
    }

    public static final Product BACKPACK = new Product("Sauce Labs Backpack", new BigDecimal("29.99"));
    public static final Product BIKE_LIGHT = new Product("Sauce Labs Bike Light", new BigDecimal("9.99"));
    public static final Product BOLT_TSHIRT = new Product("Sauce Labs Bolt T-Shirt", new BigDecimal("15.99"));
    public static final Product FLEECE_JACKET = new Product("Sauce Labs Fleece Jacket", new BigDecimal("49.99"));
    public static final Product ONESIE = new Product("Sauce Labs Onesie", new BigDecimal("7.99"));
    public static final Product RED_TSHIRT = new Product("Test.allTheThings() T-Shirt (Red)", new BigDecimal("15.99"));

    /** All products in the default (A to Z) order. */
    public static final List<Product> ALL_PRODUCTS =
            List.of(BACKPACK, BIKE_LIGHT, BOLT_TSHIRT, FLEECE_JACKET, ONESIE, RED_TSHIRT);

    private TestData() {
    }
}
