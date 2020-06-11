package griz.shop.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

/**
 * Receipt
 *
 * @author nichollsmc
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Receipt {
    private Set<Map<String, Object>> items;
    private BigDecimal               totalPrice;
}
