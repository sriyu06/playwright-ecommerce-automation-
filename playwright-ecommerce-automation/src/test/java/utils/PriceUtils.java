package utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Helpers for converting price text such as "Item total: $29.99" to numbers and back. */
public final class PriceUtils {

    private static final Pattern MONEY = Pattern.compile("\\$(\\d+(?:\\.\\d{1,2})?)");

    private PriceUtils() {
    }

    public static BigDecimal parse(String text) {
        Matcher matcher = MONEY.matcher(text);
        if (!matcher.find()) {
            throw new IllegalArgumentException("No price found in text: " + text);
        }
        return new BigDecimal(matcher.group(1));
    }

    public static BigDecimal sum(List<String> priceTexts) {
        return priceTexts.stream().map(PriceUtils::parse).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static String format(BigDecimal price) {
        return "$" + price.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
