package griz.shop.client;

import static java.lang.String.format;
import static java.net.http.HttpRequest.BodyPublishers;
import static java.net.http.HttpResponse.BodyHandlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.io.*;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.fasterxml.jackson.databind.DeserializationFeature.*;
import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

/**
 * Main
 *
 * @author nichollsmc
 */
public class Main {

    private static final String       REQUESTS_JSON = "requests.json";
    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper()
            .configure(ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
            .configure(ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true)
            .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(FAIL_ON_EMPTY_BEANS, false)
            .configure(WRITE_DATES_AS_TIMESTAMPS, false);
    }

    private void run() throws IOException {
        final var client = createHttpClient();

        readRequests()
            .map(toHttpRequest().andThen(sendRequestWith(client)))
            .forEach(r -> {});

        toHttpRequest()
            .andThen(sendRequestWith(client))
            .andThen(response -> {
                var receipt = Receipt.builder().build();
                try {
                    receipt = OBJECT_MAPPER.readValue(response, Receipt.class);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

                return receipt;
            })
            .andThen(printReceipt())
            .apply(Request.builder()
                    .endpoint("http://localhost:8080/cart/receipt")
                    .method("GET")
                    .build());
    }

    private Stream<Request> readRequests() throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(REQUESTS_JSON)) {
            return OBJECT_MAPPER.readValue(inputStream, new TypeReference<List<Request>>(){}).stream();
        }
    }

    private HttpClient createHttpClient() {
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(30))
                .cookieHandler(new CookieManager())
                .build();
    }

    private Function<Request, HttpRequest> toHttpRequest() {
        return request ->
            HttpRequest.newBuilder()
                .uri(URI.create(request.getEndpoint()))
                .header("Content-Type", "application/json")
                .method(request.getMethod().toUpperCase(),
                        Optional.ofNullable(request.getPayload())
                                .filter(p -> !p.isEmpty())
                                .map(toJson().andThen(BodyPublishers::ofString))
                                .orElse(BodyPublishers.noBody()))
                .build();
    }

    private Function<HttpRequest, String> sendRequestWith(final HttpClient client) {
        return httpRequest -> {
            try {
                return client.send(httpRequest, BodyHandlers.ofString()).body();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

            return "";
        };
    }

    private UnaryOperator<Receipt> printReceipt() {
        return receipt -> {
            final var timestamp = Timestamp.from(Instant.now());
            final var dateTimeFormat = DateFormat.getDateTimeInstance();
            final var dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            final var timeFormat = new SimpleDateFormat("HH:mm:ss");

            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            final var builder = new StringBuilder();

            builder
                .append("\n")
                .append("RECEIPT").append("\n")
                .append("Date: ").append(dateFormat.format(timestamp)).append("\n")
                .append("Time: ").append(timeFormat.format(timestamp)).append("\n")
                .append("---------------------------------------------------------------------").append("\n")
                .append(format("%-10s%-11s%-16s", "Name", "Quantity", "Total Price")).append("\n");

            var currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);

            receipt.getItems().stream()
                .forEach(item -> {
                    var items =
                        item.entrySet().stream()
                            .map(Map.Entry::getValue)
                            .collect(Collectors.toList());

                    builder
                        .append(format("%-10s%-11d%-16s", items.get(0), items.get(1), currencyFormatter.format(items.get(2))))
                        .append("\n");
                });

            builder.append("---------------------------------------------------------------------").append("\n");
            builder.append(format("%16s%14s", "Total:", currencyFormatter.format(receipt.getTotalPrice()))).append("\n");

            System.out.println(builder.toString());

            return receipt;
        };
    }

    private <I> Function<I, String> toJson() {
        return object -> {
            try {
                return OBJECT_MAPPER.writeValueAsString(object);
            }
            catch (IOException ioe) {
                throw new UncheckedIOException(ioe);
            }
        };
    }

    public static void main(String... args) throws Exception {
        new Main().run();
    }
}
