@echo off
echo ================================================
echo   Seeding Categories and Products
echo ================================================

set BASE=http://localhost:8080

:: -----------------------------------------------
:: STEP 1 - Register Admin User
:: -----------------------------------------------
echo.
echo [1/3] Registering admin user...
curl -s -X POST %BASE%/api/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"admin@shop.com\",\"password\":\"Admin@123\",\"firstName\":\"Admin\",\"lastName\":\"User\",\"role\":\"ADMIN\"}" > nul

:: -----------------------------------------------
:: STEP 2 - Login and extract token
:: -----------------------------------------------
echo [2/3] Logging in as admin...
curl -s -X POST %BASE%/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"admin@shop.com\",\"password\":\"Admin@123\"}" > token_response.json

:: Extract token using PowerShell
for /f "delims=" %%i in ('powershell -Command "(Get-Content token_response.json | ConvertFrom-Json).accessToken"') do set TOKEN=%%i

if "%TOKEN%"=="" (
  echo ERROR: Could not get token. Make sure all services are running.
  pause
  exit /b 1
)

echo Token obtained successfully.
echo.

:: -----------------------------------------------
:: STEP 3 - Create 5 Categories
:: -----------------------------------------------
echo [3/3] Creating categories and products...

echo Creating category: Electronics
curl -s -X POST %BASE%/api/categories ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer %TOKEN%" ^
  -d "{\"name\":\"Electronics\",\"description\":\"Gadgets, phones, laptops and electronic devices\"}" > nul

echo Creating category: Clothing
curl -s -X POST %BASE%/api/categories ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer %TOKEN%" ^
  -d "{\"name\":\"Clothing\",\"description\":\"Men and women fashion, apparel and accessories\"}" > nul

echo Creating category: Home and Kitchen
curl -s -X POST %BASE%/api/categories ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer %TOKEN%" ^
  -d "{\"name\":\"Home and Kitchen\",\"description\":\"Furniture, cookware and home essentials\"}" > nul

echo Creating category: Sports
curl -s -X POST %BASE%/api/categories ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer %TOKEN%" ^
  -d "{\"name\":\"Sports\",\"description\":\"Sports equipment, fitness gear and outdoor accessories\"}" > nul

echo Creating category: Books
curl -s -X POST %BASE%/api/categories ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer %TOKEN%" ^
  -d "{\"name\":\"Books\",\"description\":\"Fiction, non-fiction, educational and self-help books\"}" > nul

echo Categories created. Waiting 2 seconds...
timeout /t 2 /nobreak > nul

:: -----------------------------------------------
:: STEP 4 - Create 10 Products per Category
:: -----------------------------------------------

:: --- ELECTRONICS (categoryId=1) ---
echo Creating Electronics products...

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"iPhone 15 Pro\",\"description\":\"Apple iPhone 15 Pro with A17 chip, 48MP camera and titanium design.\",\"price\":999.99,\"brand\":\"Apple\",\"categoryId\":1,\"imageUrl\":\"https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"Samsung Galaxy S24\",\"description\":\"Samsung flagship with Snapdragon 8 Gen 3 and 200MP camera.\",\"price\":849.99,\"brand\":\"Samsung\",\"categoryId\":1,\"imageUrl\":\"https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"MacBook Pro 14\",\"description\":\"Apple MacBook Pro with M3 chip, 16GB RAM and 512GB SSD.\",\"price\":1999.99,\"brand\":\"Apple\",\"categoryId\":1,\"imageUrl\":\"https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"Sony WH-1000XM5\",\"description\":\"Industry leading noise cancelling wireless headphones.\",\"price\":349.99,\"brand\":\"Sony\",\"categoryId\":1,\"imageUrl\":\"https://images.unsplash.com/photo-1618366712010-f4ae9c647dcb?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"iPad Air 5\",\"description\":\"Apple iPad Air with M1 chip, 10.9 inch Liquid Retina display.\",\"price\":599.99,\"brand\":\"Apple\",\"categoryId\":1,\"imageUrl\":\"https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"Dell XPS 15\",\"description\":\"Dell XPS 15 with Intel Core i7, 32GB RAM and OLED display.\",\"price\":1799.99,\"brand\":\"Dell\",\"categoryId\":1,\"imageUrl\":\"https://images.unsplash.com/photo-1593642632559-0c6d3fc62b89?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"Apple Watch Series 9\",\"description\":\"Smartwatch with health monitoring, GPS and always-on display.\",\"price\":399.99,\"brand\":\"Apple\",\"categoryId\":1,\"imageUrl\":\"https://images.unsplash.com/photo-1546868871-7041f2a55e12?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"Canon EOS R50\",\"description\":\"Mirrorless camera with 24.2MP sensor and 4K video recording.\",\"price\":679.99,\"brand\":\"Canon\",\"categoryId\":1,\"imageUrl\":\"https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"LG 27 4K Monitor\",\"description\":\"27 inch 4K UHD IPS monitor with USB-C and HDR support.\",\"price\":449.99,\"brand\":\"LG\",\"categoryId\":1,\"imageUrl\":\"https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"Logitech MX Master 3\",\"description\":\"Advanced wireless mouse with ergonomic design and fast scrolling.\",\"price\":99.99,\"brand\":\"Logitech\",\"categoryId\":1,\"imageUrl\":\"https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=600\"}" > nul

:: --- CLOTHING (categoryId=2) ---
echo Creating Clothing products...

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"Classic White T-Shirt\",\"description\":\"Premium 100% cotton white t-shirt, comfortable everyday wear.\",\"price\":29.99,\"brand\":\"Zara\",\"categoryId\":2,\"imageUrl\":\"https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"Slim Fit Jeans\",\"description\":\"Dark blue slim fit denim jeans with stretch fabric.\",\"price\":59.99,\"brand\":\"Levi's\",\"categoryId\":2,\"imageUrl\":\"https://images.unsplash.com/photo-1542272604-787c3835535d?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"Wool Overcoat\",\"description\":\"Elegant wool blend overcoat for winter, available in black and grey.\",\"price\":189.99,\"brand\":\"H&M\",\"categoryId\":2,\"imageUrl\":\"https://images.unsplash.com/photo-1539533018447-63fcce2678e3?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"Running Sneakers\",\"description\":\"Lightweight breathable running shoes with cushioned sole.\",\"price\":89.99,\"brand\":\"Nike\",\"categoryId\":2,\"imageUrl\":\"https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"Floral Summer Dress\",\"description\":\"Light floral print summer dress, perfect for warm weather.\",\"price\":49.99,\"brand\":\"Zara\",\"categoryId\":2,\"imageUrl\":\"https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"Leather Belt\",\"description\":\"Genuine leather belt with silver buckle, fits waist 28-40.\",\"price\":34.99,\"brand\":\"Tommy Hilfiger\",\"categoryId\":2,\"imageUrl\":\"https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"Hooded Sweatshirt\",\"description\":\"Cozy fleece hoodie with kangaroo pocket, unisex fit.\",\"price\":44.99,\"brand\":\"Adidas\",\"categoryId\":2,\"imageUrl\":\"https://images.unsplash.com/photo-1556821840-3a63f15732ce?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"Formal Blazer\",\"description\":\"Slim fit formal blazer for office and events, navy blue.\",\"price\":129.99,\"brand\":\"Marks and Spencer\",\"categoryId\":2,\"imageUrl\":\"https://images.unsplash.com/photo-1507679799987-c73779587ccf?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"Silk Scarf\",\"description\":\"100% silk scarf with elegant print, 90x90cm.\",\"price\":39.99,\"brand\":\"Gucci\",\"categoryId\":2,\"imageUrl\":\"https://images.unsplash.com/photo-1601924994987-69e26d50dc26?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"Cargo Shorts\",\"description\":\"Comfortable cargo shorts with multiple pockets, khaki color.\",\"price\":39.99,\"brand\":\"Gap\",\"categoryId\":2,\"imageUrl\":\"https://images.unsplash.com/photo-1591195853828-11db59a44f43?w=600\"}" > nul

:: --- HOME AND KITCHEN (categoryId=3) ---
echo Creating Home and Kitchen products...

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"Nespresso Coffee Machine\",\"description\":\"Compact espresso machine with 19 bar pressure and fast heat-up.\",\"price\":149.99,\"brand\":\"Nespresso\",\"categoryId\":3,\"imageUrl\":\"https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"Cast Iron Skillet\",\"description\":\"Pre-seasoned 12 inch cast iron skillet for stovetop and oven use.\",\"price\":44.99,\"brand\":\"Lodge\",\"categoryId\":3,\"imageUrl\":\"https://images.unsplash.com/photo-1585515320310-259814833e62?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"KitchenAid Stand Mixer\",\"description\":\"5 quart tilt-head stand mixer with 10 speeds and multiple attachments.\",\"price\":379.99,\"brand\":\"KitchenAid\",\"categoryId\":3,\"imageUrl\":\"https://images.unsplash.com/photo-1594385208974-2e75f8d7bb48?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"Bamboo Cutting Board\",\"description\":\"Extra large bamboo cutting board with juice groove and handles.\",\"price\":29.99,\"brand\":\"OXO\",\"categoryId\":3,\"imageUrl\":\"https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"Instant Pot Duo 7-in-1\",\"description\":\"Multi-use pressure cooker, slow cooker, rice cooker and steamer.\",\"price\":89.99,\"brand\":\"Instant Pot\",\"categoryId\":3,\"imageUrl\":\"https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"Ceramic Dinner Set\",\"description\":\"16 piece ceramic dinner set, microwave and dishwasher safe.\",\"price\":69.99,\"brand\":\"Corelle\",\"categoryId\":3,\"imageUrl\":\"https://images.unsplash.com/photo-1603199506016-b9a594b593c0?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"Air Purifier\",\"description\":\"HEPA air purifier covering 500 sq ft, removes 99.97% of particles.\",\"price\":129.99,\"brand\":\"Dyson\",\"categoryId\":3,\"imageUrl\":\"https://images.unsplash.com/photo-1585771724684-38269d6639fd?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"Scented Candle Set\",\"description\":\"Set of 6 soy wax scented candles with wooden wicks, 40hr burn time.\",\"price\":34.99,\"brand\":\"Yankee Candle\",\"categoryId\":3,\"imageUrl\":\"https://images.unsplash.com/photo-1602607144535-11be3fe48d5e?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"Non-Stick Cookware Set\",\"description\":\"10 piece non-stick cookware set with glass lids, oven safe to 400F.\",\"price\":119.99,\"brand\":\"Tefal\",\"categoryId\":3,\"imageUrl\":\"https://images.unsplash.com/photo-1584990347449-a2d4c2c044c9?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"Electric Kettle\",\"description\":\"1.7L stainless steel electric kettle with temperature control.\",\"price\":49.99,\"brand\":\"Breville\",\"categoryId\":3,\"imageUrl\":\"https://images.unsplash.com/photo-1544787219-7f47ccb76574?w=600\"}" > nul

:: --- SPORTS (categoryId=4) ---
echo Creating Sports products...

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"Yoga Mat\",\"description\":\"6mm thick non-slip yoga mat with carrying strap, eco-friendly TPE.\",\"price\":34.99,\"brand\":\"Lululemon\",\"categoryId\":4,\"imageUrl\":\"https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"Adjustable Dumbbells\",\"description\":\"Adjustable dumbbell set 5-52.5 lbs, replaces 15 sets of weights.\",\"price\":299.99,\"brand\":\"Bowflex\",\"categoryId\":4,\"imageUrl\":\"https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"Resistance Bands Set\",\"description\":\"Set of 5 resistance bands with handles, door anchor and ankle straps.\",\"price\":24.99,\"brand\":\"TheraBand\",\"categoryId\":4,\"imageUrl\":\"https://images.unsplash.com/photo-1598289431512-b97b0917affc?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"Cycling Helmet\",\"description\":\"Lightweight road cycling helmet with MIPS protection and ventilation.\",\"price\":79.99,\"brand\":\"Giro\",\"categoryId\":4,\"imageUrl\":\"https://images.unsplash.com/photo-1558618047-3c8c76ca7d13?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"Tennis Racket\",\"description\":\"Professional tennis racket with graphite frame and grip size 4 3/8.\",\"price\":89.99,\"brand\":\"Wilson\",\"categoryId\":4,\"imageUrl\":\"https://images.unsplash.com/photo-1617083934555-ac7b4d500c2a?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"Running Water Bottle\",\"description\":\"BPA-free 32oz insulated water bottle keeps drinks cold 24 hours.\",\"price\":19.99,\"brand\":\"Hydro Flask\",\"categoryId\":4,\"imageUrl\":\"https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"Jump Rope\",\"description\":\"Speed jump rope with ball bearings and adjustable cable, for all levels.\",\"price\":14.99,\"brand\":\"Crossrope\",\"categoryId\":4,\"imageUrl\":\"https://images.unsplash.com/photo-1598971639058-fab3c3109a00?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"Foam Roller\",\"description\":\"High density foam roller for muscle recovery and deep tissue massage.\",\"price\":29.99,\"brand\":\"TriggerPoint\",\"categoryId\":4,\"imageUrl\":\"https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"Basketball\",\"description\":\"Official size 7 indoor outdoor basketball with deep channel design.\",\"price\":39.99,\"brand\":\"Spalding\",\"categoryId\":4,\"imageUrl\":\"https://images.unsplash.com/photo-1546519638405-a9f9b4a5b8b7?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"Gym Gloves\",\"description\":\"Weight lifting gloves with wrist support and anti-slip grip.\",\"price\":17.99,\"brand\":\"Harbinger\",\"categoryId\":4,\"imageUrl\":\"https://images.unsplash.com/photo-1583454110551-21f2fa2afe61?w=600\"}" > nul

:: --- BOOKS (categoryId=5) ---
echo Creating Books products...

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"Atomic Habits\",\"description\":\"An easy and proven way to build good habits and break bad ones by James Clear.\",\"price\":16.99,\"brand\":\"Penguin\",\"categoryId\":5,\"imageUrl\":\"https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"The Pragmatic Programmer\",\"description\":\"Your journey to mastery, 20th anniversary edition by David Thomas.\",\"price\":49.99,\"brand\":\"Addison-Wesley\",\"categoryId\":5,\"imageUrl\":\"https://images.unsplash.com/photo-1532012197267-da84d127e765?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"Clean Code\",\"description\":\"A handbook of agile software craftsmanship by Robert C. Martin.\",\"price\":44.99,\"brand\":\"Prentice Hall\",\"categoryId\":5,\"imageUrl\":\"https://images.unsplash.com/photo-1589998059171-988d887df646?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"The Alchemist\",\"description\":\"A magical story about following your dreams by Paulo Coelho.\",\"price\":13.99,\"brand\":\"HarperCollins\",\"categoryId\":5,\"imageUrl\":\"https://images.unsplash.com/photo-1512820790803-83ca734da794?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"Sapiens\",\"description\":\"A brief history of humankind by Yuval Noah Harari.\",\"price\":17.99,\"brand\":\"Harper Perennial\",\"categoryId\":5,\"imageUrl\":\"https://images.unsplash.com/photo-1495640388908-05fa85288e61?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"Design Patterns\",\"description\":\"Elements of reusable object-oriented software by Gang of Four.\",\"price\":54.99,\"brand\":\"Addison-Wesley\",\"categoryId\":5,\"imageUrl\":\"https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"Rich Dad Poor Dad\",\"description\":\"What the rich teach their kids about money by Robert Kiyosaki.\",\"price\":14.99,\"brand\":\"Plata Publishing\",\"categoryId\":5,\"imageUrl\":\"https://images.unsplash.com/photo-1553729459-efe14ef6055d?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"Harry Potter Box Set\",\"description\":\"Complete 7-book Harry Potter series by J.K. Rowling, hardcover edition.\",\"price\":89.99,\"brand\":\"Bloomsbury\",\"categoryId\":5,\"imageUrl\":\"https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"The Great Gatsby\",\"description\":\"Classic American novel by F. Scott Fitzgerald, Scribner edition.\",\"price\":11.99,\"brand\":\"Scribner\",\"categoryId\":5,\"imageUrl\":\"https://images.unsplash.com/photo-1543002588-bfa74002ed7e?w=600\"}" > nul

curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\":\"Think and Grow Rich\",\"description\":\"The landmark bestseller on the secrets of success by Napoleon Hill.\",\"price\":12.99,\"brand\":\"Sound Wisdom\",\"categoryId\":5,\"imageUrl\":\"https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8?w=600\"}" > nul

:: Cleanup
del token_response.json > nul 2>&1

echo.
echo ================================================
echo   Done! 5 categories and 50 products created.
echo   Visit http://localhost:8080/api/products
echo ================================================
pause
