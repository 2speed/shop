package griz.shop.server.service;

import griz.shop.server.domain.Cart;
import griz.shop.server.domain.CartItem;
import griz.shop.server.domain.Receipt;
import griz.shop.server.domain.ReceiptItem;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static java.util.stream.Collectors.toSet;

/**
 * Simple service for completing {@link Cart} checkout.
 *
 * <p>When calculating the total price for each type of item, the following bulk discounts apply:
 *
 * Quantity:
 * 1 – 10:    No discount
 * 11 – 100:  10% discount
 * 101 – 1000 15% discount
 * 1001+      25% discount
 *
 * @author nichollsmc
 */
@Singleton
public class CheckoutService {

    private static final BigDecimal TEN_PERCENT_DISCOUNT         = new BigDecimal("0.10");
    private static final BigDecimal FIFTEEN_PERCENT_DISCOUNT     = new BigDecimal("0.15");
    private static final BigDecimal TWENTY_FIVE_PERCENT_DISCOUNT = new BigDecimal("0.25");

    /**
     * {@link Function} that accepts a {@link Cart} and produces a {@link Receipt} with calculated totals.
     *
     * @return the {@code Receipt} for the {@code Cart}
     */
    public Function<Cart, Receipt> checkout() {
        return cart -> {
            final var receiptItems =
                cart.getItems().stream().parallel()
                    .map(toReceiptItem().andThen(applyDiscount()))
                    .collect(toSet());

            final var receiptTotal =
                receiptItems.stream()
                    .map(ReceiptItem::getTotalPrice)
                    .reduce(new BigDecimal(0), BigDecimal::add);

            return Receipt.builder()
                    .items(receiptItems)
                    .totalPrice(receiptTotal)
                    .build();
        };
    }

    private Function<CartItem, ReceiptItem> toReceiptItem() {
        return cartItem ->
            ReceiptItem.builder()
                .name(cartItem.getName())
                .quantity(cartItem.getQuantity())
                .totalPrice(cartItem.getPricePerItem().multiply(new BigDecimal(cartItem.getQuantity())))
                .build();
    }

    private UnaryOperator<ReceiptItem> applyDiscount() {
        return receiptItem -> {
            final var quantity = receiptItem.getQuantity().intValue();

            if (quantity <= 10) {
                return receiptItem;
            }

            var discount = new BigDecimal(0);

            if (quantity > 10 && quantity <= 100) {
                discount = TEN_PERCENT_DISCOUNT;
            } else if (quantity > 100 && quantity <= 1000) {
                discount = FIFTEEN_PERCENT_DISCOUNT;
            } else if (quantity > 1000) {
                discount = TWENTY_FIVE_PERCENT_DISCOUNT;
            }

            final var discountedTotal = receiptItem.getTotalPrice();

            return ReceiptItem.builder()
                    .name(receiptItem.getName())
                    .quantity(receiptItem.getQuantity())
                    .totalPrice(discountedTotal.subtract(discountedTotal.multiply(discount)))
                    .build();

        };
    }
}
