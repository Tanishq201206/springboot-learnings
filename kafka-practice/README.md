# Kafka Practice — Order → Inventory → Notification (Spring Boot + Spring Kafka)

This learning project (**groupId:** `com.kafkaprect`, **artifactId:** `kafkaperctice`) demonstrates a simple **event-driven pipeline**:

1. **OrderController** exposes REST endpoints under **`/orders`**.
   - `POST /orders` → publishes an **OrderRequest** to Kafka topic **`order_topic`**.
   - `GET /orders` and `GET /orders/paged` → read from the **orders** table (if persisted).
2. **InventoryConsumer** listens to **`order_topic`**, performs dummy stock logic (`inStock = quantity <= 5`),
   and publishes an **InventoryStatus** to topic **`inventory_topic"`.
3. **NotificationConsumer** listens to **`inventory_topic`** and **persists** an `InventoryStatusEntity` to MySQL,
   then logs a notification-like message.

> Key classes detected:  
> - Producer: `Service/OrderProducer.java` (`kafkaTemplate.send("order_topic", json)`)  
> - Consumers: `Component/InventoryConsumer.java` (listen `order_topic`), `Component/NotificationConsumer.java` (listen `inventory_topic`)  
> - REST: `Controller/OrderController.java` (`/orders`, `/orders/paged`)  
> - Entities: `OrderEntity` → table `orders`, `InventoryStatusEntity` → table `inventory_status`

---

## 📦 Dependencies (from `pom.xml`)
- Spring Boot (parent version from your POM)
- `spring-boot-starter-web`, `spring-kafka`, `spring-boot-starter-data-jpa`
- `mysql:mysql-connector-java:8.0.33`
- tests: `spring-boot-starter-test`, `spring-kafka-test`

---

## ⚙️ Configuration

Create `src/main/resources/application.properties` (or use the provided example below) with your **Kafka** and **MySQL** settings.

```properties
# ----- Server -----
server.port=8084

spring.application.name=kafkaperctice

# ----- Kafka -----
spring.kafka.bootstrap-servers=localhost:9092

# Producer
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer

# Consumer
spring.kafka.consumer.group-id=order-group
spring.kafka.consumer.auto-offset-reset=earliest
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.apache.kafka.common.serialization.StringDeserializer

# ----- MySQL (for JPA entities) -----
spring.datasource.url=jdbc:mysql://localhost:3306/kafka_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

> **Note:** If `@KafkaListener` methods do not start, add `@EnableKafka` to a `@Configuration` class:
> ```java
> import org.springframework.kafka.annotation.EnableKafka;
> import org.springframework.context.annotation.Configuration;
> 
> @Configuration
> @EnableKafka
> public class KafkaListenersConfig {}
> ```

---

## ▶️ Run Kafka + MySQL (Docker, optional but easy)

You can use the provided **docker-compose.yml** to spin up **Zookeeper**, **Kafka**, **MySQL**, and **Kafka UI** quickly.

```bash
docker compose up -d
```

**Services:**
- Zookeeper → `localhost:2181`
- Kafka → `localhost:9092`
- MySQL → `localhost:3306` (user: root / password: root)
- Kafka UI → http://localhost:8081 (connect to `localhost:9092`)

---

## ▶️ How to Run the App

```bash
./mvnw spring-boot:run          # macOS/Linux
mvnw.cmd spring-boot:run        # Windows
```
App will start at **http://localhost:8084** (changeable via `server.port`).

---

## 🧭 REST Endpoints

### 1) Place an order → publish to Kafka
`POST /orders`  
Request body:
```json
{
  "orderId": "ORD-1001",
  "item": "laptop",
  "quantity": 3
}
```
Response:
```
"Order placed: ORD-1001"
```

### 2) Get all orders (from DB)
`GET /orders` → returns `List<OrderEntity>`  
> Note: the sample code does **not** persist orders on `POST /orders`. Persisting is easy: map `OrderRequest` → `OrderEntity` and call `orderRepository.save(..)`.

### 3) Get paged orders
`GET /orders/paged?page=0&size=5` → returns `Page<OrderEntity>`

---

## 🔄 Message Flow (what to expect)

1. **POST /orders** publishes JSON to **`order_topic`**.
2. **InventoryConsumer** (listening to `order_topic`) reads the order, computes:
   - `inStock = (quantity <= 5)`  
   - emits an `InventoryStatus` to **`inventory_topic"`.
3. **NotificationConsumer** (listening to `inventory_topic`) saves `InventoryStatusEntity` to **MySQL**.
   - Check table **`inventory_status"`** to see the saved records.

You will see log lines like:
```
Inventory checked and message sent: {"orderId":"ORD-1001","inStock":true,"message":"Stock available"}
✅ Saved inventory status to DB: ORD-1001
📩 Notification saved: Order ORD-1001 → Stock available
```

---

## 🧪 Quick cURL Test

```bash
# 1) Place an order
curl -X POST http://localhost:8084/orders   -H "Content-Type: application/json"   -d '{"orderId":"ORD-1001","item":"laptop","quantity":3}'

# 2) List paged orders (optional, if you persist orders)
curl "http://localhost:8084/orders/paged?page=0&size=5"
```

---

## 🛠️ Troubleshooting

- **`Failed to construct kafka consumer/producer`** → Ensure Kafka is running and `spring.kafka.bootstrap-servers=localhost:9092` is set.
- **`No qualifying bean of type KafkaTemplate`** → Spring Kafka auto-config requires the `spring.kafka.*` properties. Add them.
- **`404 /orders/paged`** → Verify base path is `/orders` and mapping is `/paged`. URL should be:  
  `http://localhost:8084/orders/paged?page=0&size=5`
- **Consumers not firing** → Add `@EnableKafka` to a configuration class.
- **MySQL errors** → Confirm DB exists: `CREATE DATABASE kafka_db;` Then let JPA create tables.

---

## 📁 Suggested repo placement
```
springboot-learnings/
└── kafka-practice/
    ├── pom.xml
    ├── src/...
    ├── README.md
    └── application.properties.example
```

---

## 🙌 Credits
Learning project by **Tanishq Singh**.
