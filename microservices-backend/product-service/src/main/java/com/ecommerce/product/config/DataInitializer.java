package com.ecommerce.product.config;

import com.ecommerce.product.entity.Category;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.CategoryRepository;
import com.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (categoryRepository.count() > 0) {
            log.info("Database already seeded, skipping...");
            return;
        }

        log.info("Seeding database with categories and products...");

        Category electronics = save("Electronics",    "Gadgets, devices and electronic accessories");
        Category clothing    = save("Clothing",       "Men and women fashion and apparel");
        Category home        = save("Home & Kitchen", "Furniture, appliances and kitchen essentials");
        Category sports      = save("Sports",         "Sports equipment and outdoor gear");
        Category books       = save("Books",          "Bestsellers, textbooks and novels");

        // Electronics
        productRepository.saveAll(List.of(
            product("iPhone 15 Pro",        "Apple iPhone 15 Pro 256GB, Titanium finish, A17 Pro chip",                        999.99, "Apple",        "https://picsum.photos/seed/iphone15/600/400",    electronics),
            product("Samsung Galaxy S24",   "Samsung Galaxy S24 Ultra 512GB, AI-powered camera system",                        849.99, "Samsung",      "https://picsum.photos/seed/galaxys24/600/400",  electronics),
            product("Sony WH-1000XM5",      "Industry-leading noise cancelling wireless headphones",                            349.99, "Sony",         "https://picsum.photos/seed/sonyxm5/600/400",    electronics),
            product("MacBook Air M3",       "Apple MacBook Air 13-inch with M3 chip, 8GB RAM, 256GB SSD",                     1099.99, "Apple",        "https://picsum.photos/seed/macbookm3/600/400",  electronics),
            product("iPad Pro 12.9",        "Apple iPad Pro with M2 chip and Liquid Retina XDR display",                      1199.99, "Apple",        "https://picsum.photos/seed/ipadpro/600/400",    electronics),
            product("Dell XPS 15",          "Intel Core i7, 16GB RAM, 512GB SSD, OLED display",                               1299.99, "Dell",         "https://picsum.photos/seed/dellxps/600/400",    electronics),
            product("Canon EOS R50",        "Mirrorless camera, 24.2MP, 4K video recording",                                   679.99, "Canon",        "https://picsum.photos/seed/canonr50/600/400",   electronics),
            product("Apple Watch Series 9", "GPS 45mm, Always-On Retina display, health tracking",                             399.99, "Apple",        "https://picsum.photos/seed/applewatch/600/400", electronics),
            product("LG 4K OLED TV 55\"",   "55-inch 4K OLED Smart TV with webOS and Dolby Vision",                          1499.99, "LG",           "https://picsum.photos/seed/lgtv55/600/400",     electronics),
            product("Logitech MX Master 3", "Advanced wireless mouse with ultra-fast scrolling",                                 99.99, "Logitech",     "https://picsum.photos/seed/mxmaster/600/400",   electronics)
        ));

        // Clothing
        productRepository.saveAll(List.of(
            product("Classic White T-Shirt", "Premium 100% cotton unisex white t-shirt",                                        19.99, "H&M",          "https://picsum.photos/seed/whitetshirt/600/400",  clothing),
            product("Slim Fit Jeans",        "Men slim fit dark blue denim jeans, stretch fabric",                              49.99, "Levi's",       "https://picsum.photos/seed/slimjeans/600/400",    clothing),
            product("Floral Summer Dress",   "Women lightweight floral print midi dress",                                       39.99, "Zara",         "https://picsum.photos/seed/floraldress/600/400",  clothing),
            product("Hooded Sweatshirt",     "Unisex pullover hoodie, fleece lined, kangaroo pocket",                           44.99, "Nike",         "https://picsum.photos/seed/hoodie/600/400",       clothing),
            product("Leather Jacket",        "Men genuine leather biker jacket with zip pockets",                              129.99, "Zara",         "https://picsum.photos/seed/leatherjacket/600/400",clothing),
            product("Running Sneakers",      "Lightweight breathable running shoes with cushioned sole",                        79.99, "Adidas",       "https://picsum.photos/seed/sneakers/600/400",     clothing),
            product("Formal Blazer",         "Men slim fit formal blazer, perfect for office and events",                       89.99, "M&S",          "https://picsum.photos/seed/blazer/600/400",       clothing),
            product("Yoga Pants",            "Women high-waist leggings with moisture-wicking fabric",                          34.99, "Lululemon",    "https://picsum.photos/seed/yogapants/600/400",    clothing),
            product("Wool Overcoat",         "Women long wool blend overcoat, double-breasted",                                159.99, "Mango",        "https://picsum.photos/seed/overcoat/600/400",     clothing),
            product("Polo Shirt",            "Men classic fit cotton polo shirt, multiple colors",                              29.99, "Ralph Lauren", "https://picsum.photos/seed/poloshirt/600/400",    clothing)
        ));

        // Home & Kitchen
        productRepository.saveAll(List.of(
            product("Nespresso Coffee Machine", "Vertuo Next coffee and espresso machine with milk frother",                   149.99, "Nespresso",    "https://picsum.photos/seed/nespresso/600/400",    home),
            product("KitchenAid Stand Mixer",   "Artisan 5-quart stand mixer with 10 speeds",                                 379.99, "KitchenAid",   "https://picsum.photos/seed/kitchenaid/600/400",   home),
            product("Instant Pot Duo 7-in-1",   "Electric pressure cooker, slow cooker, rice cooker and more",                 89.99, "Instant Pot",  "https://picsum.photos/seed/instantpot/600/400",   home),
            product("Dyson V15 Vacuum",         "Cordless vacuum with laser dust detection technology",                        649.99, "Dyson",        "https://picsum.photos/seed/dysonv15/600/400",     home),
            product("Memory Foam Pillow",       "Ergonomic memory foam pillow with cooling gel layer",                          49.99, "Tempur",       "https://picsum.photos/seed/pillow/600/400",       home),
            product("Air Purifier HEPA",        "True HEPA air purifier covers up to 500 sq ft",                              199.99, "Levoit",       "https://picsum.photos/seed/airpurifier/600/400",  home),
            product("Non-Stick Cookware Set",   "10-piece non-stick cookware set with glass lids",                            119.99, "Tefal",        "https://picsum.photos/seed/cookware/600/400",     home),
            product("Smart LED Desk Lamp",      "Touch control LED lamp with USB charging port",                                39.99, "Philips",      "https://picsum.photos/seed/desklamp/600/400",     home),
            product("Wooden Cutting Board",     "Large acacia wood cutting board with juice groove",                            34.99, "OXO",          "https://picsum.photos/seed/cuttingboard/600/400", home),
            product("French Press Coffee Maker","Stainless steel French press 34oz, double wall insulated",                     29.99, "Bodum",        "https://picsum.photos/seed/frenchpress/600/400",  home)
        ));

        // Sports
        productRepository.saveAll(List.of(
            product("Yoga Mat",            "Non-slip 6mm thick yoga mat with carrying strap",                                   29.99, "Manduka",      "https://picsum.photos/seed/yogamat/600/400",      sports),
            product("Adjustable Dumbbells","Adjustable dumbbell set 5-52.5 lbs, replaces 15 sets",                            299.99, "Bowflex",      "https://picsum.photos/seed/dumbbells/600/400",    sports),
            product("Bike Helmet",         "Adult mountain bike helmet with MIPS protection, 20 vents",                         89.99, "Giro",         "https://picsum.photos/seed/bikehelmet/600/400",   sports),
            product("Water Bottle 32oz",   "Insulated stainless steel bottle, keeps cold 24 hours",                             24.99, "Hydro Flask",  "https://picsum.photos/seed/waterbottle/600/400",  sports),
            product("Resistance Bands",    "Set of 5 resistance bands with different tension levels",                           19.99, "TheraBand",    "https://picsum.photos/seed/resistbands/600/400",  sports),
            product("Tennis Racket",       "Professional graphite frame racket, pre-strung, all levels",                        79.99, "Wilson",       "https://picsum.photos/seed/tennisracket/600/400", sports),
            product("Football",            "Official size 5 football, FIFA quality pro, butyl bladder",                         34.99, "Adidas",       "https://picsum.photos/seed/football/600/400",     sports),
            product("Jump Rope",           "Speed jump rope with ball bearings, adjustable cable",                              14.99, "Crossrope",    "https://picsum.photos/seed/jumprope/600/400",     sports),
            product("Gym Gloves",          "Weight lifting gloves with wrist support, anti-slip palm",                          19.99, "Harbinger",    "https://picsum.photos/seed/gymgloves/600/400",    sports),
            product("Foam Roller",         "High-density foam roller for muscle recovery and massage",                           24.99, "TriggerPoint", "https://picsum.photos/seed/foamroller/600/400",   sports)
        ));

        // Books
        productRepository.saveAll(List.of(
            product("Clean Code",               "A Handbook of Agile Software Craftsmanship by Robert C. Martin",              34.99, "Prentice Hall",  "https://picsum.photos/seed/cleancode/600/400",      books),
            product("The Pragmatic Programmer", "Your Journey to Mastery by David Thomas and Andrew Hunt",                     39.99, "Addison-Wesley", "https://picsum.photos/seed/pragprog/600/400",       books),
            product("Atomic Habits",            "An Easy and Proven Way to Build Good Habits by James Clear",                  16.99, "Avery",          "https://picsum.photos/seed/atomichabits/600/400",   books),
            product("System Design Interview",  "An Insider's Guide Vol 1 by Alex Xu, essential for tech interviews",          35.99, "Independently",  "https://picsum.photos/seed/systemdesign/600/400",   books),
            product("The Alchemist",            "A magical story about following your dreams by Paulo Coelho",                 12.99, "HarperOne",      "https://picsum.photos/seed/alchemist/600/400",      books),
            product("Deep Work",                "Rules for Focused Success in a Distracted World by Cal Newport",              17.99, "Grand Central",  "https://picsum.photos/seed/deepwork/600/400",       books),
            product("Design Patterns",          "Elements of Reusable Object-Oriented Software by Gang of Four",               44.99, "Addison-Wesley", "https://picsum.photos/seed/designpatterns/600/400", books),
            product("The Psychology of Money",  "Timeless lessons on wealth, greed and happiness by Morgan Housel",            15.99, "Harriman House", "https://picsum.photos/seed/psychmoney/600/400",     books),
            product("Dune",                     "Epic science fiction novel by Frank Herbert",                                 14.99, "Ace Books",      "https://picsum.photos/seed/dunebook/600/400",       books),
            product("Harry Potter Box Set",     "Complete 7-book hardcover collection by J.K. Rowling",                       89.99, "Scholastic",     "https://picsum.photos/seed/harrypotter/600/400",    books)
        ));

        log.info("Seeding complete: 5 categories and 50 products inserted.");
    }

    private Category save(String name, String description) {
        return categoryRepository.save(Category.builder()
            .name(name)
            .description(description)
            .build());
    }

    private Product product(String name, String description, double price,
                            String brand, String imageUrl, Category category) {
        return Product.builder()
            .name(name)
            .description(description)
            .price(BigDecimal.valueOf(price))
            .brand(brand)
            .imageUrl(imageUrl)
            .active(true)
            .category(category)
            .build();
    }
}
