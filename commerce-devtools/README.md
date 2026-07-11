# commerce-devtools

A small e-commerce backend that shows **Ligero devtools** on a realistic,
multi-feature dependency graph — the kind of app where the visual debugger
earns its keep.

Three feature modules (`catalog`, `customers`, `orders`) wire **~11
interface-typed beans** with cross-module dependencies. The `OrderService`
orchestrates most of them, so placing an order lights up a deep path across the
graph.

```bash
./gradlew :commerce-devtools:run
# open http://localhost:8080/ligero/dev
```

## The graph

```
OrderService ──► OrderRepository
             ├─► CustomerService ──► CustomerRepository
             ├─► CatalogService ──► ProductRepository
             │                 └─► InventoryService ──► InventoryRepository
             ├─► PricingService ──► ProductRepository
             ├─► InventoryService ──► InventoryRepository
             └─► NotificationService        (@Component)

RecommendationService ──► CatalogService, OrderRepository
```

15 beans in all (4 repositories, 6 services, 1 component, 4 controllers), with
shared dependencies (`ProductRepository`, `InventoryService`) and fan-in on
`OrderService` — so the graph is genuinely worth exploring.

## Endpoints

| Method | Path | Touches |
|---|---|---|
| `GET`  | `/api/catalog/products` | CatalogService → ProductRepository + InventoryService |
| `GET`  | `/api/catalog/products/{sku}` | CatalogService (+ inventory) |
| `GET`  | `/api/catalog/products/{sku}/quote?qty=` | PricingService → ProductRepository |
| `GET`  | `/api/customers/{id}` | CustomerService → CustomerRepository |
| `GET`  | `/api/customers/{id}/recommendations` | RecommendationService → CatalogService, OrderRepository |
| `POST` | `/api/orders` | **the deep one** — customer, catalogue, pricing, inventory, save, notify |
| `GET`  | `/api/orders/{id}` | OrderService → OrderRepository |
| `GET`  | `/api/orders?customerId=` | OrderService → OrderRepository |

## Try it in devtools

1. Open **/ligero/dev**.
2. Pick **`POST /api/orders`**, send:
   ```json
   { "customerId": 1, "items": [ { "sku": "KB-1", "qty": 2 }, { "sku": "CB-3", "qty": 3 } ] }
   ```
3. Watch the execution path light up (blue, for POST) across `OrderService` →
   catalogue/pricing/inventory/customer services → repositories → notification —
   ~21 calls, up to 3 levels deep. Click any node to see its arguments and JSON
   result for that request. Branches the run didn't take (e.g.
   `RecommendationService`) stay visible as dependencies but aren't highlighted.
