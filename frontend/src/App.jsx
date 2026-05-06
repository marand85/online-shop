import { useEffect, useState } from "react";
import { 
  fetchCategories,
  fetchProducts,
  fetchOrderByNumber } from "./api/shopApi";

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

  return (
    <main style={{ padding: "24px", maxWidth: "960px", margin: "0 auto"}}>
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
          <button type="submit" disabled={orderLookupLoading} style={{ padding: "8px 16px"}}>
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