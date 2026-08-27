@echo off
echo Starting all microservices...

set BASE=c:\Users\iramnaaz.basade\Desktop\Microservices\microservices-backend

echo [1/9] Starting Eureka Server...
start "Eureka Server" cmd /k "cd %BASE%\eureka-server && mvn spring-boot:run"

echo Waiting 20 seconds for Eureka to start...
timeout /t 20 /nobreak

echo [2/9] Starting API Gateway...
start "API Gateway" cmd /k "cd %BASE%\api-gateway && mvn spring-boot:run"

echo [3/9] Starting Auth Service...
start "Auth Service" cmd /k "cd %BASE%\auth-service && mvn spring-boot:run"

echo [4/9] Starting Product Service...
start "Product Service" cmd /k "cd %BASE%\product-service && mvn spring-boot:run"

echo [5/9] Starting Inventory Service...
start "Inventory Service" cmd /k "cd %BASE%\inventory-service && mvn spring-boot:run"

echo [6/9] Starting Cart Service...
start "Cart Service" cmd /k "cd %BASE%\cart-service && mvn spring-boot:run"

echo [7/9] Starting Order Service...
start "Order Service" cmd /k "cd %BASE%\order-service && mvn spring-boot:run"

echo [8/9] Starting Payment Service...
start "Payment Service" cmd /k "cd %BASE%\payment-service && mvn spring-boot:run"

echo [9/9] Starting Notification Service...
start "Notification Service" cmd /k "cd %BASE%\notification-service && mvn spring-boot:run"

echo All services started! Check http://localhost:8761 to verify.
pause
