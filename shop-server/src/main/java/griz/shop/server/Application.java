package griz.shop.server;

import io.micronaut.runtime.Micronaut;

/**
 * Entrypoint into the application.
 *
 * @author nichollsmc
 */
public class Application {
    public static void main(String[] args) {
        Micronaut.run(Application.class);
    }
}