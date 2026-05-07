import { useEffect, useState } from "react";
import { 
  fetchCategories,
  fetchProducts,
  fetchOrderByNumber,
  createOrder,
} from "./api/shopApi";

function formatPrice(priceCents, currency) {
  const value = (priceCents ?? 0) / 100;
  return new Intl.NumberFormat("pl-PL", {
    style: "currency",
    currency: currency || "PLN",
  }).format(value);
}

function App() {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);

  const [searchQuery, setSearchQuery] = useState("");
  const [selectedCategory, setSelectedCategory] = useState("");

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [orderNumberInput, setOrderNumberInput] = useState("");
  const [orderLookupResult, setOrderLookupResult] = useState(null);
  const [orderLookupError, setOrderLookupError] = useState("");
  const [orderLookupLoading, setOrderLookupLoading] = useState(false);

  const [selectedProductId, setSelectedProductId] = useState("");
  const [quickOrderEmail, setQuickOrderEmail] = useState("demo.user@example.com");
  const [orderCreateLoading, setOrderCreateLoading] = useState(false);
  const [orderCreateError, setOrderCreateError] = useState("");
  const [orderCreateResult, setOrderCreateResult] = useState(null);

  useEffect(() => {
    async function loadCategories() {
      try {
        const data = await fetchCategories();
        setCategories(data ?? []);
      } catch (err) {
        setError("Failed to load categories.");
      }
    }

    loadCategories();
  }, []);

  useEffect(() => {
    async function loadProducts() {
      try {
        setLoading(true);
        setError("");

        const params = {
          page: 0,
          size: 12,
          sort: "createdAt,desc",
        };

        if (searchQuery.trim()) {
          params.q = searchQuery.trim();
        }

        if (selectedCategory) {
          params.categorySlug = selectedCategory;
        }

        const data = await fetchProducts(params);
        setProducts(data.content ?? []);
      } catch (err) {
        setError("Failed to load products.");
      } finally {
        setLoading(false);
      }
    }

    loadProducts();
  }, [searchQuery, selectedCategory]);

  async function handleOrderLookup(event) {
    event.preventDefault();

    const normalizedOrderNumber = orderNumberInput
      .toUpperCase()
      .replace(/[^A-Z0-9-]/g, "")
      .trim();

    if (!normalizedOrderNumber) {
      setOrderLookupResult(null);
      setOrderLookupError("Order number is required.");
      return;
    }

    try {
      setOrderLookupLoading(true);
      setOrderLookupResult(null);
      setOrderLookupError("");

      const order = await fetchOrderByNumber(normalizedOrderNumber);
      setOrderLookupResult(order);
    } catch(err) {      
      if (err?.response?.status === 404) {
        setOrderLookupError("Order not found.");
      } else {
        setOrderLookupError("Failed to fetch order.")
      }
    } finally {
      setOrderLookupLoading(false);
    }
  }

  async function handleQuickOrderSubmit(event) {
    event.preventDefault();

    if(!selectedProductId) {
      setOrderCreateResult(null);
      setOrderCreateError("Please select a product.");
      return;
    }

    if (!quickOrderEmail.trim()) {
      setOrderCreateResult(null);
      setOrderCreateError("Email is required.");
      return;
    }

    try {
      setOrderCreateLoading(true);
      setOrderCreateError("");
      setOrderCreateResult(null);

      const payload = {
        items: [
          {
            productId: Number(selectedProductId),
            quantity: 1,
          }
        ],
        shipping: {
          name: "Frontend Demo User",
          line1: "Demo Street 1",
          line2: null,
          city: "Warsaw",
          state: null,
          postal: "00-001",
          country: "PL",
        },
        contactEmail: quickOrderEmail.trim(),
        contactPhone: null,
        currency: "PLN",
      };

      const createdOrder = await createOrder(payload);
      setOrderCreateResult(createdOrder);
      setOrderNumberInput(createdOrder.orderNumber);
    } catch(err) {
      setOrderCreateError("Failed to create order.");
    } finally {
      setOrderCreateLoading(false);
    }
  }

  return (
    <main style={{ padding: "24px", maxWidth: "960px", margin: "0 auto" }}>
      <h1>Online Shop</h1>

      <section style={{ display: "flex", gap: "12px", marginBottom: "16px" }}>
        <input
          type="text"
          placeholder="Search products..."
          value={searchQuery}
          onChange={(event) => setSearchQuery(event.target.value)}
          style={{ flex: 1, padding: "8px" }}
        />

        <select
          value={selectedCategory}
          onChange={(event) => setSelectedCategory(event.target.value)}
          style={{ padding: "8px" }}
        >
          <option value="">All categories</option>
          {categories.map((category) => (
            <option key={category.id} value={category.slug}>
              {category.name}
            </option>
          ))}
        </select>
      </section>

      <section style={{ marginBottom: "24px" }}>
        <h2>Place quick order</h2>

        <form
          onSubmit={handleQuickOrderSubmit}
          style={{ display: "grid", gap: "12px", maxWidth: "560px" }}
        >
          <select
            value={selectedProductId}
            onChange={(event) => setSelectedProductId(event.target.value)}
            style={{ padding: "8px" }}
            disabled={products.length === 0 || orderCreateLoading}
          >
            {products.length === 0 ? (
              <option value="">No products available</option>
            ) : (
              products.map((product) => (
                <option key={product.id} value={String(product.id)}>
                  {product.name} - {formatPrice(product.priceCents, product.currency)}
                </option>
              ))
            )}
          </select>

          <input
            type="email"
            placeholder="Contact email"
            value={quickOrderEmail}
            onChange={(event) => setQuickOrderEmail(event.target.value)}
            style={{ padding: "8px" }}
            disabled={orderCreateLoading}
          />

          <button
            type="submit"
            disabled={orderCreateLoading || products.length === 0}
            style={{ padding: "8px 16px", width: "fit-content" }}
          >
            {orderCreateLoading ? "Placing order..." : "Place order"}
          </button>
        </form>

        {orderCreateError && <p style={{ marginTop: "8px"}}>{orderCreateError}</p>}

        {orderCreateResult && (
          <div style={{ marginTop: "12px" }}>
            <p>
              <strong>Order created:</strong> {orderCreateResult.orderNumber}
            </p>
            <p>
              <strong>Status:</strong> {orderCreateResult.status}
            </p>
            <p>
              <strong>Total:</strong>{" "}
              {formatPrice(orderCreateResult.totalCents, orderCreateResult.currency)}
            </p>
          </div>
        )}
      </section>

      <section style = {{ marginBottom: "24px" }}>
        <h2>Order lookup</h2>
        <form onSubmit={handleOrderLookup} style={{ display: "flex", gap: "12px" }}>
          <input
            type="text"
            placeholder="Enter order number..."
            value={orderNumberInput}
            onChange={(event) => setOrderNumberInput(event.target.value)}
            style={{ flex: 1, padding: "8px" }}
          />
          <button type="submit" disabled={orderLookupLoading} style={{ padding: "8px 16px" }}>
            {orderLookupLoading ? "Checking..." : "Check order"}
          </button>
        </form>

        {orderLookupError && <p style={{ marginTop: "8px" }}>{orderLookupError}</p>}

        {orderLookupResult && (
          <div style={{ marginTop: "12px" }}>
            <p>
              <strong>Order number:</strong> {orderLookupResult.orderNumber}
            </p>
            <p>
              <strong>Status:</strong> {orderLookupResult.status}
            </p>
            <p>
              <strong>Total:</strong>{" "}
              {formatPrice(orderLookupResult.totalCents, orderLookupResult.currency)}
            </p>
          </div>
        )}
      </section>

      {loading && <p>Loading products...</p>}
      {error && <p>{error}</p>}

      {!loading && !error && (
        <ul>
          {products.map((product) => (
          <li key={product.id}>
            {product.name} - {formatPrice(product.priceCents, product.currency)}
          </li>
        ))}
        </ul>
      )}
    </main>
  );
}

export default App;