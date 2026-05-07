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
        setCategories(Array.isArray(data) ? data : []);
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
    <main className="min-h-screen bg-white text-black">
      <div className="mx-auto max-w-5xl px-4 py-8">
        <h1 className="mb-6 text-3xl font-bold tracking-tight">Online Shop</h1>

        <section className="mb-6 grid gap-3 md:grid-cols-[1fr_260px]">
          <input
            type="text"
            placeholder="Search products..."
            value={searchQuery}
            onChange={(event) => setSearchQuery(event.target.value)}
            className="w-full rounded-md border border-gray-300 px-3 py-2 outline-none ring-0 transition focus:border-gray-500"
          />

          <select
            value={selectedCategory}
            onChange={(event) => setSelectedCategory(event.target.value)}
            className="w-full rounded-md border border-gray-300 bg-white px-3 py-2 outline-none transition focus:border-gray-500"
          >
            <option value="">All categories</option>
            {categories.map((category) => (
              <option key={category.id} value={category.slug}>
                {category.name}
              </option>
            ))}
          </select>
        </section>

        <section className="mb-6 rounded-lg border border-gray-200 bg-gray-50 p-4">
          <h2 className="mb-3 text-xl font-semibold">Place quick order</h2>

          <form onSubmit={handleQuickOrderSubmit} className="grid max-w-xl gap-3">
            <select
              value={selectedProductId}
              onChange={(event) => setSelectedProductId(event.target.value)}
              disabled={products.length === 0 || orderCreateLoading}
              className="w-full rounded-md border border-gray-300 bg-white px-3 py-2 outline-none transition focus:border-gray-500 disabled:cursor-not-allowed disabled:bg-gray-100"
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
              disabled={orderCreateLoading}
              className="w-full rounded-md border border-gray-300 px-3 py-2 outline-none transition focus:border-gray-500 disabled:cursor-not-allowed disabled:bg-gray-100"
            />

            <button
              type="submit"
              disabled={orderCreateLoading || products.length === 0}
              className="w-fit rounded-md bg-black px-4 py-2 text-white transition hover:bg-gray-800 disabled:cursor-not-allowed disabled:bg-gray-400"
            >
              {orderCreateLoading ? "Placing order..." : "Place order"}
            </button>
          </form>

          {orderCreateError && <p className="mt-2 text-sm text-red-600">{orderCreateError}</p>}

          {orderCreateResult && (
            <div className="mt-3 rounded-md border border-gray-200 bg-white p-3 text-sm">
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

        <section className="mb-6 rounded-lg border border-gray-200 bg-gray-50 p-4">
          <h2 className="mb-3 text-xl font-semibold">Order lookup</h2>

          <form onSubmit={handleOrderLookup} className="flex flex-col gap-3 md:flex-row">
            <input
              type="text"
              placeholder="Enter order number..."
              value={orderNumberInput}
              onChange={(event) => setOrderNumberInput(event.target.value)}
              className="w-full rounded-md border border-gray-300 px-3 py-2 outline-none transition focus:border-gray-500"
            />
            <button
              type="submit"
              disabled={orderLookupLoading}
              className="w-fit rounded-md bg-black px-4 py-2 text-white transition hover:bg-gray-800 disabled:cursor-not-allowed disabled:bg-gray-400"
            >
              {orderLookupLoading ? "Checking..." : "Check order"}
            </button>
          </form>

          {orderLookupError && <p className="mt-2 text-sm text-red-600">{orderLookupError}</p>}

          {orderLookupResult && (
            <div className="mt-3 rounded-md border border-gray-200 bg-white p-3 text-sm">
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

        {loading && <p className="text-sm text-gray-600">Loading products...</p>}
        {error && <p className="text-sm text-red-600">{error}</p>}

        {!loading && !error && (
          <section className="rounded-lg border border-gray-200 bg-white p-4">
            <h2 className="mb-3 text-xl font-semibold">Products</h2>
            <ul className="space-y-2">
              {products.map((product) => (
                <li key={product.id} className="rounded-md border border-gray-200 px-3 py-2">
                  {product.name} - {formatPrice(product.priceCents, product.currency)}
                </li>
              ))}
            </ul>
          </section>
        )}
      </div>
    </main>
  );
}

export default App;