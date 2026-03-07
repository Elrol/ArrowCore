package dev.elrol.arrow.libs;

import com.google.common.collect.EvictingQueue;
import dev.elrol.arrow.ArrowCore;
import dev.elrol.arrow.api.events.ItemSoldCallback;
import dev.elrol.arrow.api.registries.IEconomyRegistry;
import dev.elrol.arrow.config.ArrowConfig;
import dev.elrol.arrow.data.Currency;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.HTTPServer;
import net.minecraft.registry.Registries;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MetricsExporter {
    private static HTTPServer server;
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static final Map<String, EvictingQueue<Double>> PRICE_BUFFERS = new HashMap<>();

    public static final Gauge TOTAL_SUPPLY = Gauge.build()
            .name("mc_economy")
            .help("Total supply of the economy")
            .labelNames("currency", "default_rank", "donor_rank", "staff_rank")
            .register();

    public static final Gauge SALES_VOLUME = Gauge.build()
            .name("mc_economy_sales")
            .help("Total currency moved through item sales")
            .labelNames("currency")
            .register();

    public static final Gauge ITEM_PRICE = Gauge.build()
            .name("mc_economy_price")
            .help("The last sold price of an item per unit")
            .labelNames("item_id", "currency")
            .register();


    public static void updateRankedMetrics() {
        scheduler.execute(() -> {
            IEconomyRegistry registry = ArrowCore.INSTANCE.getEconomyRegistry();
            ArrowConfig.MetricsSettings metricsSettings = ArrowCore.CONFIG.metricsSettings;

            Map<EconomyMetricLabel, BigDecimal> map = new HashMap<>();

            ArrowCore.CONFIG.metricsSettings.getCurrencies().forEach(curID -> {
                Currency currency = registry.getCurrency(curID);
                if(currency != null) {
                    registry.getAllBalances(currency).forEach((uuid, bal) -> {
                        String rankID = PermUtils.getHighestGroupInTrack(uuid, metricsSettings.getDefaultTrack()).orElse("none");
                        String donorID = PermUtils.getHighestGroupInTrack(uuid, metricsSettings.getDonorTrack()).orElse("none");
                        String staffID = PermUtils.getHighestGroupInTrack(uuid, metricsSettings.getStaffTrack()).orElse("none");

                        EconomyMetricLabel label = new EconomyMetricLabel(curID, rankID, donorID, staffID);
                        map.merge(label, bal, BigDecimal::add);
                    });
                }
            });

            TOTAL_SUPPLY.clear();

            map.forEach((label, total) -> {
                TOTAL_SUPPLY.labels(
                        label.currency(),
                        label.defaultRank(),
                        label.donorRank(),
                        label.staffRank()
                ).set(total.doubleValue());
            });
        });
    }

    public static void start() {
        try {
            int port = ArrowCore.CONFIG.metricsSettings.getPort();
            server = new HTTPServer(port);
            updateRankedMetrics();
            ArrowCore.LOGGER.info("Metrics Exporter started on port {}.", port);
        } catch(IOException e) {
            ArrowCore.LOGGER.error("Error starting HTTP server", e);
        }

        ItemSoldCallback.EVENT.register(((uuid, stack, amount, cost, curID) -> {
            String itemID = Registries.ITEM.getId(stack.getItem()).toString();
            double pricePerUnit = cost.divide(BigDecimal.valueOf(amount), RoundingMode.HALF_UP).doubleValue();

            EvictingQueue<Double> buffer = PRICE_BUFFERS.computeIfAbsent(itemID, k -> EvictingQueue.create(10));
            buffer.add(pricePerUnit);

            double average = buffer.stream().mapToDouble(Double::doubleValue).average().orElse(pricePerUnit);

            ITEM_PRICE.labels(itemID, curID).set(average);
            SALES_VOLUME.labels(curID).inc(cost.doubleValue());
        }));
    }

    public static void stop() {
        try {
            scheduler.shutdown();
            if(!scheduler.awaitTermination(2, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }

        if(server != null) {
            server.close();
        }
    }

    public record EconomyMetricLabel(String currency, String defaultRank, String donorRank, String staffRank) {}

}
