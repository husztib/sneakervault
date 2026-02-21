package com.sneakervault.controller;

import com.sneakervault.dto.*;
import com.sneakervault.model.OrderItem;
import com.sneakervault.model.Shoe;
import com.sneakervault.model.ShoeOrder;
import com.sneakervault.repository.ShoeOrderRepository;
import com.sneakervault.repository.ShoeRepository;
import com.sneakervault.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final ShoeOrderRepository orderRepository;
    private final ShoeRepository shoeRepository;
    private final EmailService emailService;

    public OrderController(ShoeOrderRepository orderRepository, ShoeRepository shoeRepository, EmailService emailService) {
        this.orderRepository = orderRepository;
        this.shoeRepository = shoeRepository;
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity<ShoeOrder> checkout(@RequestBody CheckoutRequest req) {
        if (req.getItems() == null || req.getItems().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        ShoeOrder order = new ShoeOrder();
        order.setOrderDate(LocalDateTime.now());
        order.setCurrency(req.getCurrency());

        int totalHUF = 0;
        int totalEUR = 0;

        List<Shoe> soldShoes = new ArrayList<>();

        for (CheckoutItemRequest itemReq : req.getItems()) {
            Optional<Shoe> optShoe = shoeRepository.findById(itemReq.getShoeId());
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
            item.setPrice(shoe.getPrice());
            item.setPriceEUR(shoe.getPriceEUR());
            item.setImageUrl(shoe.getImageUrl());

            order.getItems().add(item);
            totalHUF += shoe.getPrice();
            totalEUR += shoe.getPriceEUR();
            soldShoes.add(shoe);
        }

        if (order.getItems().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        order.setTotalHUF(totalHUF);
        order.setTotalEUR(totalEUR);

        order.setCustomerName(req.getCustomerName());
        order.setCustomerEmail(req.getCustomerEmail());
        order.setCustomerPhone(req.getCustomerPhone());
        order.setCustomerZip(req.getCustomerZip());
        order.setCustomerCity(req.getCustomerCity());
        order.setCustomerStreet(req.getCustomerStreet());
        order.setCustomerNotes(req.getCustomerNotes());

        soldShoes.forEach(s -> s.setSold(true));
        shoeRepository.saveAll(soldShoes);

        ShoeOrder savedOrder = orderRepository.save(order);

        if (savedOrder.getCustomerEmail() != null && !savedOrder.getCustomerEmail().isBlank()) {
            emailService.sendOrderConfirmation(savedOrder);
        }

        return ResponseEntity.ok(savedOrder);
    }

    @GetMapping
    public List<ShoeOrder> getOrders() {
        List<ShoeOrder> orders = orderRepository.findAll();
        orders.sort((a, b) -> b.getOrderDate().compareTo(a.getOrderDate()));
        return orders;
    }

    @GetMapping("/stats")
    public OrderStatsResponse getStats() {
        List<ShoeOrder> orders = orderRepository.findAll();
        long totalItems = orders.stream().mapToLong(o -> o.getItems().size()).sum();
        long totalHUF = orders.stream().mapToLong(ShoeOrder::getTotalHUF).sum();
        long totalEUR = orders.stream().mapToLong(ShoeOrder::getTotalEUR).sum();
        return new OrderStatsResponse(orders.size(), totalItems, totalHUF, totalEUR);
    }

    @GetMapping("/per-shoe")
    public List<PerShoeSalesResponse> getPerShoeSales() {
        List<ShoeOrder> orders = orderRepository.findAll();
        Map<Long, PerShoeSalesResponse> salesMap = new LinkedHashMap<>();

        for (ShoeOrder order : orders) {
            for (OrderItem item : order.getItems()) {
                Long key = item.getShoeId();
                PerShoeSalesResponse ps = salesMap.get(key);
                if (ps == null) {
                    ps = new PerShoeSalesResponse();
                    ps.setShoeId(key);
                    ps.setName(item.getName());
                    ps.setColor(item.getColor());
                    ps.setStyleCode(item.getStyleCode());
                    ps.setTimesSold(0);
                    ps.setTotalHUF(0);
                    ps.setTotalEUR(0);
                    ps.setLastDate(order.getOrderDate().toString());
                    salesMap.put(key, ps);
                }
                ps.setTimesSold(ps.getTimesSold() + 1);
                ps.setTotalHUF(ps.getTotalHUF() + item.getPrice());
                ps.setTotalEUR(ps.getTotalEUR() + item.getPriceEUR());
                if (order.getOrderDate().toString().compareTo(ps.getLastDate()) > 0) {
                    ps.setLastDate(order.getOrderDate().toString());
                }
            }
        }

        List<PerShoeSalesResponse> result = new ArrayList<>(salesMap.values());
        result.sort((a, b) -> b.getTimesSold() - a.getTimesSold());
        return result;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        return orderRepository.findById(id).map(order -> {
            for (OrderItem item : order.getItems()) {
                shoeRepository.findById(item.getShoeId()).ifPresent(shoe -> {
                    shoe.setSold(false);
                    shoeRepository.save(shoe);
                });
            }
            orderRepository.delete(order);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllOrders() {
        List<ShoeOrder> orders = orderRepository.findAll();
        for (ShoeOrder order : orders) {
            for (OrderItem item : order.getItems()) {
                shoeRepository.findById(item.getShoeId()).ifPresent(shoe -> {
                    shoe.setSold(false);
                    shoeRepository.save(shoe);
                });
            }
        }
        orderRepository.deleteAll();
        return ResponseEntity.ok().build();
    }
}
