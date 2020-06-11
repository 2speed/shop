package griz.shop.server.domain;

import io.micronaut.core.annotation.Introspected;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
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
    private String  name;

    @NotNull
    @Positive
    private BigDecimal pricePerItem;

    @NotNull
    @Positive
    private BigInteger quantity;
}
