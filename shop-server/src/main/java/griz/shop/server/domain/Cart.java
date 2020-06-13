package griz.shop.server.domain;

import io.micronaut.core.annotation.Introspected;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * Cart
 *
 * @author nichollsmc
 */
@Data
@Builder
@Introspected
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    private Set<CartItem> items;

    /**
     * Returns an {@link Optional} for a {@link CartItem} for the provided name.
     *
     * @return the {@code Optional} for the {@code CartItem}
     */
    public Optional<CartItem> findItemByName(final String name) {
        return Optional.ofNullable(name)
                .filter(n -> !n.isBlank())
                .flatMap(n -> Optional.ofNullable(getItems()).flatMap(items ->
                                items.stream()
                                    .filter(item -> item.getName().equalsIgnoreCase(n))
                                    .findFirst()));
    }

    /**
     * Removes a {@link CartItem} from the {@link Cart} and returns an {@link Optional} for item that was removed.
     *
     * @param name the name of the {@code CartItem} to remove
     * @return an {@code Optional} for the item that was removed
     */
    public Optional<CartItem> removeItemByName(final String name) {
        return findItemByName(name)
                .map(item -> {
                    setItems(getItems().stream()
                                .filter(i -> !item.getName().equalsIgnoreCase(i.getName()))
                                .collect(toSet()));

                    return item;
                });
    }
}
