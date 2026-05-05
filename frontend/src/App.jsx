import { useEffect, useState } from "react";
import { fetchCategories, fetchProducts } from "./api/shopApi";

function App() {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);

  const [searchQuery, setSearchQuery] = useState("");
  const [selectedCategory, setSelectedCategory] = useState("");

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

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

      {loading && <p>Loading products...</p>}
      {error && <p>{error}</p>}

      {!loading && !error && (
        <ul>
          {products.map((product) => (
          <li key={product.id}>
            {product.name} - {product.priceCents} {product.currency}
          </li>
        ))}
        </ul>
      )}
    </main>
  );
}

export default App;