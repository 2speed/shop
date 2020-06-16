package griz.shop.server.domain;

import io.micronaut.core.annotation.Introspected;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * CartItem
 *
 * @author nichollsmc
 */
@Data
@Builder
@Introspected
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    private static final String ITEM_MAX_QUANTITY_VALUE = "9999999999";

    /**
     * Defines the name for the quantity field.
     */
    public static final String FIELD_NAME_QUANTITY = "quantity";

    /**
     * Defines the maximum item quantity.
     */
    public static final Long ITEM_MAX_QUANTITY = Long.valueOf(ITEM_MAX_QUANTITY_VALUE);

    @NotBlank
    @Size(max=256)
    private String  name;

    @NotNull
    @Positive
    @DecimalMax(value = "9999999999.99")
    private BigDecimal pricePerItem;

    @NotNull
    @Positive
    @DecimalMax(value = ITEM_MAX_QUANTITY_VALUE)
    private BigInteger quantity;
}
