package com.sneakervault.controller;

import com.sneakervault.dto.PriceUpdateRequest;
import com.sneakervault.model.Shoe;
import com.sneakervault.repository.ShoeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shoes")
public class ShoeController {

    private final ShoeRepository shoeRepository;

    public ShoeController(ShoeRepository shoeRepository) {
        this.shoeRepository = shoeRepository;
    }

    @GetMapping
    public List<Shoe> getShoes(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) Double sizeEUR,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean includeSold) {

        List<Shoe> shoes = shoeRepository.findAll();

        if (!Boolean.TRUE.equals(includeSold)) {
            shoes = shoes.stream()
                    .filter(s -> !Boolean.TRUE.equals(s.getSold()) && !Boolean.TRUE.equals(s.getSuspended()))
                    .toList();
        }

        if (brand != null && !brand.isEmpty()) {
            shoes = shoes.stream().filter(s -> s.getBrand().equalsIgnoreCase(brand)).toList();
        }
        if (type != null && !type.isEmpty()) {
            shoes = shoes.stream().filter(s -> s.getType().equalsIgnoreCase(type)).toList();
        }
        if (gender != null && !gender.isEmpty()) {
            shoes = shoes.stream().filter(s -> s.getGender().equalsIgnoreCase(gender)).toList();
        }
        if (sizeEUR != null) {
            shoes = shoes.stream().filter(s -> s.getSizeEUR().equals(sizeEUR)).toList();
        }
        if (search != null && !search.isEmpty()) {
            String q = search.toLowerCase();
            shoes = shoes.stream().filter(s -> {
                String haystack = (s.getName() + " " + s.getColor() + " " + s.getStyleCode() + " "
                        + s.getSizeUS() + " " + s.getSizeEUR() + " " + s.getBrand() + " "
                        + s.getType() + " " + s.getGender()).toLowerCase();
                return haystack.contains(q);
            }).toList();
        }

        return shoes;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Shoe> updateShoe(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        return shoeRepository.findById(id).map(shoe -> {
            if (body.containsKey("name")) shoe.setName((String) body.get("name"));
            if (body.containsKey("variant")) shoe.setVariant((String) body.get("variant"));
            if (body.containsKey("color")) shoe.setColor((String) body.get("color"));
            if (body.containsKey("styleCode")) shoe.setStyleCode((String) body.get("styleCode"));
            if (body.containsKey("sizeUS")) shoe.setSizeUS((String) body.get("sizeUS"));
            if (body.containsKey("sizeEUR")) shoe.setSizeEUR(((Number) body.get("sizeEUR")).doubleValue());
            if (body.containsKey("brand")) shoe.setBrand((String) body.get("brand"));
            if (body.containsKey("type")) shoe.setType((String) body.get("type"));
            if (body.containsKey("gender")) shoe.setGender((String) body.get("gender"));
            if (body.containsKey("price")) shoe.setPrice(((Number) body.get("price")).intValue());
            if (body.containsKey("priceEUR")) shoe.setPriceEUR(((Number) body.get("priceEUR")).intValue());
            if (body.containsKey("suspended")) shoe.setSuspended((Boolean) body.get("suspended"));
            if (body.containsKey("onSale")) shoe.setOnSale((Boolean) body.get("onSale"));
            if (body.containsKey("saleType")) shoe.setSaleType((String) body.get("saleType"));
            if (body.containsKey("salePercent")) shoe.setSalePercent(body.get("salePercent") != null ? ((Number) body.get("salePercent")).intValue() : null);
            if (body.containsKey("salePrice")) shoe.setSalePrice(body.get("salePrice") != null ? ((Number) body.get("salePrice")).intValue() : null);
            if (body.containsKey("salePriceEUR")) shoe.setSalePriceEUR(body.get("salePriceEUR") != null ? ((Number) body.get("salePriceEUR")).intValue() : null);
            return ResponseEntity.ok(shoeRepository.save(shoe));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/price")
    public ResponseEntity<Shoe> updatePrice(@PathVariable Long id, @RequestBody PriceUpdateRequest req) {
        return shoeRepository.findById(id).map(shoe -> {
            shoe.setPrice(req.getPrice());
            shoe.setPriceEUR(req.getPriceEUR());
            return ResponseEntity.ok(shoeRepository.save(shoe));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/reset-price")
    public ResponseEntity<Shoe> resetPrice(@PathVariable Long id) {
        return shoeRepository.findById(id).map(shoe -> {
            shoe.setPrice(shoe.getDefaultPrice());
            shoe.setPriceEUR(shoe.getDefaultPriceEUR());
            return ResponseEntity.ok(shoeRepository.save(shoe));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/reset-prices")
    public List<Shoe> resetAllPrices() {
        List<Shoe> shoes = shoeRepository.findAll();
        shoes.forEach(shoe -> {
            shoe.setPrice(shoe.getDefaultPrice());
            shoe.setPriceEUR(shoe.getDefaultPriceEUR());
        });
        return shoeRepository.saveAll(shoes);
    }
}
