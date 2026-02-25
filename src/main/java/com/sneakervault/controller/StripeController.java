package com.sneakervault.controller;

import com.sneakervault.dto.CheckoutItemRequest;
import com.sneakervault.dto.CheckoutRequest;
import com.sneakervault.model.OrderItem;
import com.sneakervault.model.OrderStatus;
import com.sneakervault.model.Shoe;
import com.sneakervault.model.ShoeOrder;
import com.sneakervault.repository.ShoeOrderRepository;
import com.sneakervault.repository.ShoeRepository;
import com.sneakervault.service.EmailService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/stripe")
public class StripeController {

    private static final Logger log = LoggerFactory.getLogger(StripeController.class);

    private final ShoeRepository shoeRepository;
    private final ShoeOrderRepository orderRepository;
    private final EmailService emailService;

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    @Value("${stripe.publishable-key}")
    private String stripePublishableKey;

    @Value("${stripe.webhook-secret:}")
    private String webhookSecret;

    public StripeController(ShoeRepository shoeRepository, ShoeOrderRepository orderRepository, EmailService emailService) {
        this.shoeRepository = shoeRepository;
        this.orderRepository = orderRepository;
        this.emailService = emailService;
    }

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    @PostMapping("/create-checkout-session")
    public ResponseEntity<Map<String, String>> createCheckoutSession(@RequestBody CheckoutRequest req) {
        if (req.getItems() == null || req.getItems().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            String currency = req.getCurrency() != null ? req.getCurrency().toLowerCase() : "huf";
            boolean isEur = "eur".equals(currency);

            List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();
            List<String> shoeIds = new ArrayList<>();
            long totalAmount = 0;

            List<String> soldNames = new ArrayList<>();

            for (CheckoutItemRequest itemReq : req.getItems()) {
                Optional<Shoe> optShoe = shoeRepository.findById(itemReq.getShoeId());
                if (optShoe.isEmpty()) continue;

                Shoe shoe = optShoe.get();
                if (Boolean.TRUE.equals(shoe.getSold())) {
                    String name = shoe.getName();
                    if (shoe.getVariant() != null && !shoe.getVariant().isEmpty()) {
                        name += " (" + shoe.getVariant() + ")";
                    }
                    soldNames.add(name);
                    continue;
                }

                int price = isEur ? shoe.getEffectivePriceEUR() : shoe.getEffectivePrice();
                // Stripe expects amounts in the smallest currency unit (fillér for HUF, cent for EUR)
                long unitAmount = price * 100L;

                String productName = shoe.getName();
                if (shoe.getVariant() != null && !shoe.getVariant().isEmpty()) {
                    productName += " (" + shoe.getVariant() + ")";
                }

                String description = "EU " + shoe.getSizeEUR() + " | US " + shoe.getSizeUS();

                SessionCreateParams.LineItem.PriceData.Builder priceDataBuilder =
                        SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency(currency)
                                .setUnitAmount(unitAmount)
                                .setProductData(
                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                .setName(productName)
                                                .setDescription(description)
                                                .build()
                                );

                lineItems.add(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(priceDataBuilder.build())
                                .build()
                );

                shoeIds.add(String.valueOf(shoe.getId()));
                totalAmount += unitAmount;
            }

            if (!soldNames.isEmpty()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "sold");
                errorResponse.put("soldItems", String.join(", ", soldNames));
                return ResponseEntity.status(409).body(errorResponse);
            }

            if (lineItems.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            // Build success and cancel URLs
            String baseUrl = req.getBaseUrl() != null ? req.getBaseUrl() : "";
            String successUrl = baseUrl + "/checkout-success.html?session_id={CHECKOUT_SESSION_ID}";
            String cancelUrl = baseUrl + "/checkout.html";

            // Store order data in metadata
            Map<String, String> metadata = new HashMap<>();
            metadata.put("shoeIds", String.join(",", shoeIds));
            metadata.put("customerName", safe(req.getCustomerName()));
            metadata.put("customerEmail", safe(req.getCustomerEmail()));
            metadata.put("customerPhone", safe(req.getCustomerPhone()));
            metadata.put("customerZip", safe(req.getCustomerZip()));
            metadata.put("customerCity", safe(req.getCustomerCity()));
            metadata.put("customerStreet", safe(req.getCustomerStreet()));
            metadata.put("customerNotes", safe(req.getCustomerNotes()));
            metadata.put("currency", req.getCurrency() != null ? req.getCurrency() : "HUF");
            metadata.put("language", req.getLanguage() != null ? req.getLanguage() : "hu");

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(cancelUrl)
                    .setCustomerEmail(req.getCustomerEmail())
                    .putAllMetadata(metadata)
                    .addAllLineItem(lineItems)
                    .build();

            Session session = Session.create(params);

            Map<String, String> response = new HashMap<>();
            response.put("url", session.getUrl());
            response.put("sessionId", session.getId());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to create Stripe checkout session: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload,
                                                 @RequestHeader("Stripe-Signature") String sigHeader) {
        // Verify signature if webhook secret is configured
        if (webhookSecret != null && !webhookSecret.isBlank()) {
            try {
                Webhook.constructEvent(payload, sigHeader, webhookSecret);
            } catch (SignatureVerificationException e) {
                log.error("Stripe webhook signature verification failed: {}", e.getMessage());
                return ResponseEntity.badRequest().body("Invalid signature");
            } catch (Exception e) {
                log.error("Stripe webhook signature check failed: {}", e.getMessage());
                return ResponseEntity.badRequest().body("Invalid payload");
            }
        }

        // Parse event type and session ID directly from JSON for robustness
        try {
            JsonObject jsonObject = JsonParser.parseString(payload).getAsJsonObject();
            String eventType = jsonObject.get("type").getAsString();
            log.info("Stripe webhook received: {}", eventType);

            if ("checkout.session.completed".equals(eventType)) {
                String sessionId = jsonObject.getAsJsonObject("data")
                        .getAsJsonObject("object")
                        .get("id").getAsString();
                log.info("Processing checkout session: {}", sessionId);

                // Retrieve full session with metadata from Stripe API
                Session fullSession = Session.retrieve(sessionId);
                createOrderFromSession(fullSession);
            }
        } catch (Exception e) {
            log.error("Failed to process Stripe webhook: {}", e.getMessage(), e);
        }

        return ResponseEntity.ok("ok");
    }

    private void createOrderFromSession(Session session) {
        Map<String, String> metadata = session.getMetadata();
        if (metadata == null || !metadata.containsKey("shoeIds")) {
            log.error("Stripe session {} missing metadata", session.getId());
            return;
        }

        String[] shoeIdStrings = metadata.get("shoeIds").split(",");
        String currency = metadata.getOrDefault("currency", "HUF");
        boolean isEur = "EUR".equalsIgnoreCase(currency);

        ShoeOrder order = new ShoeOrder();
        order.setOrderDate(LocalDateTime.now());
        order.setCurrency(currency);
        order.setStatus(OrderStatus.PENDING);
        order.setCustomerName(metadata.getOrDefault("customerName", ""));
        order.setCustomerEmail(metadata.getOrDefault("customerEmail", ""));
        order.setCustomerPhone(metadata.getOrDefault("customerPhone", ""));
        order.setCustomerZip(metadata.getOrDefault("customerZip", ""));
        order.setCustomerCity(metadata.getOrDefault("customerCity", ""));
        order.setCustomerStreet(metadata.getOrDefault("customerStreet", ""));
        order.setCustomerNotes(metadata.getOrDefault("customerNotes", ""));
        order.setLanguage(metadata.getOrDefault("language", "hu"));

        int totalHUF = 0;
        int totalEUR = 0;
        List<Shoe> soldShoes = new ArrayList<>();

        for (String shoeIdStr : shoeIdStrings) {
            Long shoeId = Long.parseLong(shoeIdStr.trim());
            Optional<Shoe> optShoe = shoeRepository.findById(shoeId);
            if (optShoe.isEmpty()) continue;

            Shoe shoe = optShoe.get();
            if (Boolean.TRUE.equals(shoe.getSold())) continue;

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setShoeId(shoe.getId());
            item.setName(shoe.getName() + (shoe.getVariant() != null && !shoe.getVariant().isEmpty() ? " (" + shoe.getVariant() + ")" : ""));
            item.setColor(shoe.getColor());
            item.setStyleCode(shoe.getStyleCode());
            item.setSizeEUR(shoe.getSizeEUR());
            item.setSizeUS(shoe.getSizeUS());
            item.setPrice(shoe.getEffectivePrice());
            item.setPriceEUR(shoe.getEffectivePriceEUR());
            item.setImageUrl(shoe.getImageUrl());

            order.getItems().add(item);
            totalHUF += shoe.getEffectivePrice();
            totalEUR += shoe.getEffectivePriceEUR();
            soldShoes.add(shoe);
        }

        if (order.getItems().isEmpty()) {
            log.warn("No valid items for Stripe session {}", session.getId());
            return;
        }

        order.setTotalHUF(totalHUF);
        order.setTotalEUR(totalEUR);

        soldShoes.forEach(s -> s.setSold(true));
        shoeRepository.saveAll(soldShoes);

        ShoeOrder savedOrder = orderRepository.save(order);
        log.info("Order #{} created from Stripe session {}", savedOrder.getId(), session.getId());

        emailService.sendOrderConfirmation(savedOrder);
    }

    private String safe(String s) {
        if (s == null) return "";
        // Stripe metadata values max 500 chars
        return s.length() > 500 ? s.substring(0, 500) : s;
    }
}
