package com.sneakervault.init;

import com.sneakervault.model.Shoe;
import com.sneakervault.model.ShoeImage;
import com.sneakervault.repository.ShoeImageRepository;
import com.sneakervault.repository.ShoeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ShoeRepository shoeRepository;
    private final ShoeImageRepository shoeImageRepository;

    public DataInitializer(ShoeRepository shoeRepository, ShoeImageRepository shoeImageRepository) {
        this.shoeRepository = shoeRepository;
        this.shoeImageRepository = shoeImageRepository;
    }

    @Override
    public void run(String... args) {
        if (shoeRepository.count() == 0) {
            seedShoes();
        }

        // Migration: create ShoeImage records for existing shoes that have none
        if (shoeImageRepository.count() == 0) {
            for (Shoe shoe : shoeRepository.findAll()) {
                if (shoe.getImageUrl() != null && !shoe.getImageUrl().isEmpty()) {
                    ShoeImage image = new ShoeImage(shoe, shoe.getImageUrl(), 0, true);
                    shoeImageRepository.save(image);
                }
            }
        }
    }

    private void seedShoes() {
        Map<String, String> imageMap = Map.ofEntries(
            Map.entry("554725-201", "/images/554725-201.jpeg"),
            Map.entry("BQ6472-121", "/images/BQ6472-121.jpeg"),
            Map.entry("555112-401", "/images/555112-401.jpeg"),
            Map.entry("DD1869-103", "/images/DD1869-103.jpg"),
            Map.entry("CW1590-100", "/images/CW1590-100.jpg"),
            Map.entry("DH9765-003", "/images/DH9765-003.jpeg"),
            Map.entry("GW1814", "/images/GW1814.jpg"),
            Map.entry("DD1503-120", "/images/DD1503-120.jpeg"),
            Map.entry("DD1391-100", "/images/DD1391-100.jpeg"),
            Map.entry("DN3706-401", "/images/DN3706-401.jpeg"),
            Map.entry("DD1399-105", "/images/DD1399-105.jpg"),
            Map.entry("DD1399-107", "/images/DD1399-107.jpeg"),
            Map.entry("DC0774-602", "/images/DC0774-602.jpg"),
            Map.entry("DM1200-016", "/images/DM1200-016.jpeg"),
            Map.entry("VN000D5INVY", "/images/VN000D5INVY.webp"),
            Map.entry("555088-603", "/images/555088-603.jpg"),
            Map.entry("555088-118", "/images/555088-118.jpg"),
            Map.entry("DC7294-103", "/images/DC7294-103.jpg"),
            Map.entry("553558-612", "/images/553558-612.jpg"),
            Map.entry("554724-096", "/images/554724-096.jpg"),
            Map.entry("DC0774-114", "/images/DC0774-114.jpeg"),
            Map.entry("DJ6188-003", "/images/DJ6188-003.jpeg"),
            Map.entry("398614-126", "/images/398614-126.webp"),
            Map.entry("BQ6472-115", "/images/BQ6472-115.jpeg"),
            Map.entry("DB2179-105", "/images/DB2179-105.jpg"),
            Map.entry("553560-701", "/images/553560-701.jpeg"),
            Map.entry("DD1503-116", "/images/DD1503-116.jpeg"),
            Map.entry("554725-077", "/images/554725-077.jpg")
        );

        shoeRepository.save(new Shoe(1L, "Air Jordan 1 Mid", "GS", "White Onyx/Light Curry-White", "554725-201", "4Y", 36.0, "Jordan", "Mid", "GS", 63550, 155, imageMap.get("554725-201")));
        shoeRepository.save(new Shoe(2L, "WMNS Air Jordan 1 Mid", "", "Coconut Milk/Black", "BQ6472-121", "6W", 36.5, "Jordan", "Mid", "N\u0151i", 61090, 149, imageMap.get("BQ6472-121")));
        shoeRepository.save(new Shoe(3L, "Air Jordan 1 Mid", "GS", "Ice Blue/Black-Sail-White", "555112-401", "5.5Y", 38.0, "Jordan", "Mid", "GS", 61090, 149, imageMap.get("555112-401")));
        shoeRepository.save(new Shoe(4L, "W Nike Dunk High", "", "White/Black-University Red", "DD1869-103", "7.5W", 38.5, "Nike", "High", "N\u0151i", 47150, 115, imageMap.get("DD1869-103")));
        shoeRepository.save(new Shoe(5L, "Nike Dunk Low", "GS", "White/Black-White", "CW1590-100", "4.5Y", 36.5, "Nike", "Low", "GS", 44690, 109, imageMap.get("CW1590-100")));
        shoeRepository.save(new Shoe(6L, "Nike Dunk Low", "GS", "Phantom/Black-Safety Orange", "DH9765-003", "4.5Y", 36.5, "Nike", "Low", "GS", 59450, 145, imageMap.get("DH9765-003")));
        shoeRepository.save(new Shoe(7L, "W Nike Dunk High", "", "White/Black-University Red", "DD1869-103", "8.5W", 40.0, "Nike", "High", "N\u0151i", 47150, 115, imageMap.get("DD1869-103")));
        shoeRepository.save(new Shoe(8L, "Adidas YZY 700 V3", "", "Fadcar", "GW1814", "4.5", 36.7, "Adidas", "Low", "F\u00e9rfi", 41000, 100, imageMap.get("GW1814")));
        shoeRepository.save(new Shoe(9L, "W Nike Dunk Low", "", "White/Medium Olive-White", "DD1503-120", "9W", 40.5, "Nike", "Low", "N\u0151i", 63550, 155, imageMap.get("DD1503-120")));
        shoeRepository.save(new Shoe(10L, "Nike Dunk Low Retro", "", "White/Black-White", "DD1391-100", "10.5", 44.5, "Nike", "Low", "F\u00e9rfi", 52890, 129, imageMap.get("DD1391-100")));
        shoeRepository.save(new Shoe(11L, "Air Jordan 1 Mid SE", "", "French Blue/Fire Red-White", "DN3706-401", "11", 45.0, "Jordan", "Mid", "F\u00e9rfi", 38950, 95, imageMap.get("DN3706-401")));
        shoeRepository.save(new Shoe(12L, "Nike Dunk Hi Retro", "", "White/Black-Total Orange", "DD1399-105", "11.5M", 45.5, "Nike", "High", "F\u00e9rfi", 73800, 180, imageMap.get("DD1399-105")));
        shoeRepository.save(new Shoe(13L, "Nike Dunk Hi Retro", "", "White/Cargo Khaki", "DD1399-107", "10.5M", 44.5, "Nike", "High", "F\u00e9rfi", 73390, 179, imageMap.get("DD1399-107")));
        shoeRepository.save(new Shoe(14L, "WMNS Air Jordan 1 Low", "", "Canyon Rust/Black-Purple Smoke", "DC0774-602", "8W", 39.0, "Jordan", "Low", "N\u0151i", 82000, 200, imageMap.get("DC0774-602")));
        shoeRepository.save(new Shoe(15L, "Air Jordan 1 Mid SE", "", "Black/University Red-White", "DM1200-016", "10.5", 44.5, "Jordan", "Mid", "F\u00e9rfi", 34850, 85, imageMap.get("DM1200-016")));
        shoeRepository.save(new Shoe(16L, "WMNS Air Jordan 1 Mid", "", "Coconut Milk/Black", "BQ6472-121", "6W", 36.5, "Jordan", "Mid", "N\u0151i", 61090, 149, imageMap.get("BQ6472-121")));
        shoeRepository.save(new Shoe(17L, "Vans SK8-Hi", "", "Navy", "VN000D5INVY", "10", 43.0, "Vans", "High", "F\u00e9rfi", 32800, 80, imageMap.get("VN000D5INVY")));
        shoeRepository.save(new Shoe(18L, "Air Jordan 1 Retro High OG", "", "Lt Fusion Red/Black-White", "555088-603", "8.5", 42.0, "Jordan", "High", "F\u00e9rfi", 30750, 75, imageMap.get("555088-603")));
        shoeRepository.save(new Shoe(19L, "Air Jordan 1 Retro High OG", "", "White/Black-Volt", "555088-118", "8.5", 42.0, "Jordan", "High", "F\u00e9rfi", 63550, 155, imageMap.get("555088-118")));
        shoeRepository.save(new Shoe(20L, "Air Jordan 1 Mid SE", "", "White/Pine Green-Lt Smoke Grey", "DC7294-103", "10.5", 44.5, "Jordan", "Mid", "F\u00e9rfi", 34850, 85, imageMap.get("DC7294-103")));
        shoeRepository.save(new Shoe(21L, "Air Jordan 1 Low", "", "Gym Red/White-Black", "553558-612", "7.5", 40.5, "Jordan", "Low", "F\u00e9rfi", 32800, 80, imageMap.get("553558-612")));
        shoeRepository.save(new Shoe(22L, "Air Jordan 1 Mid", "", "Black/Gym Red-Particle Grey", "554724-096", "11", 45.0, "Jordan", "Mid", "F\u00e9rfi", 41000, 100, imageMap.get("554724-096")));
        shoeRepository.save(new Shoe(23L, "W Nike Dunk High", "", "White/Black-University Red", "DD1869-103", "7.5W", 38.5, "Nike", "High", "N\u0151i", 47150, 115, imageMap.get("DD1869-103")));
        shoeRepository.save(new Shoe(24L, "WMNS Air Jordan 1 Low", "", "White/DK Marina Blue-White", "DC0774-114", "8W", 39.0, "Jordan", "Low", "N\u0151i", 82000, 200, imageMap.get("DC0774-114")));
        shoeRepository.save(new Shoe(25L, "Nike Dunk Low Retro", "", "Wolf Grey/White-Wolf Grey", "DJ6188-003", "9", 42.5, "Nike", "Low", "F\u00e9rfi", 89790, 219, imageMap.get("DJ6188-003")));
        shoeRepository.save(new Shoe(26L, "WMNS Air Jordan 1 Mid", "", "Coconut Milk/Black", "BQ6472-121", "6W", 36.5, "Jordan", "Mid", "N\u0151i", 61090, 149, imageMap.get("BQ6472-121")));
        shoeRepository.save(new Shoe(27L, "Air Jordan 3 Retro", "GS", "White/Light Curry-Cardinal Red", "398614-126", "7Y", 40.0, "Jordan", "Mid", "GS", 63550, 155, imageMap.get("398614-126")));
        shoeRepository.save(new Shoe(28L, "WMNS Air Jordan 1 Mid", "", "Sail/Stealth-White", "BQ6472-115", "6.5W", 37.5, "Jordan", "Mid", "N\u0151i", 67650, 165, imageMap.get("BQ6472-115")));
        shoeRepository.save(new Shoe(29L, "Nike Dunk High", "GS", "White/Cargo Khaki", "DB2179-105", "5Y", 37.5, "Nike", "High", "GS", 44690, 109, imageMap.get("DB2179-105")));
        shoeRepository.save(new Shoe(30L, "Air Jordan 1 Low", "GS", "Taxi/Black-White", "553560-701", "5Y", 37.5, "Jordan", "Low", "GS", 69290, 169, imageMap.get("553560-701")));
        shoeRepository.save(new Shoe(31L, "W Nike Dunk Low", "", "White/Venice", "DD1503-116", "7W", 38.0, "Nike", "Low", "N\u0151i", 84050, 205, imageMap.get("DD1503-116")));
        shoeRepository.save(new Shoe(32L, "Air Jordan 1 Mid", "GS", "Black/Hyper Royal-White", "554725-077", "6.5Y", 39.0, "Jordan", "Mid", "GS", 63550, 155, imageMap.get("554725-077")));
        shoeRepository.save(new Shoe(33L, "Air Jordan 1 Mid SE", "", "French Blue/Fire Red-White", "DN3706-401", "10", 44.0, "Jordan", "Mid", "F\u00e9rfi", 38950, 95, imageMap.get("DN3706-401")));
        shoeRepository.save(new Shoe(34L, "Air Jordan 1 Mid", "", "Black/Gym Red-Particle Grey", "554724-096", "10.5", 44.5, "Jordan", "Mid", "F\u00e9rfi", 41000, 100, imageMap.get("554724-096")));
        shoeRepository.save(new Shoe(35L, "W Nike Dunk Low", "", "White/Venice", "DD1503-116", "7W", 38.0, "Nike", "Low", "N\u0151i", 84050, 205, imageMap.get("DD1503-116")));
    }
}
