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
    @NotBlank
    @Size(max=256)
    private String  name;

    @NotNull
    @Positive
    @DecimalMax(value = "9999999999.999")
    private BigDecimal pricePerItem;

    @NotNull
    @Positive
    @DecimalMax(value = "9999999999")
    private BigInteger quantity;
}
