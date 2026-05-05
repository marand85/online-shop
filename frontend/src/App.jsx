import { useEffect, useState } from "react";
import { fetchProducts } from "./api/shopApi";

function App() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadProducts() {
      try {
        setLoading(true);
        const data = await fetchProducts({page: 0, size: 12, sort: "createdAt,desc"});
        setProducts(data.content ?? []);
      } catch (err) {
        setError("Failed to load products.");
      } finally {
        setLoading(false);
      }
    }

    loadProducts();
  }, []);

  if (loading) return <main>Loading products...</main>;
  if (error) return <main>{error}</main>;

  return (
    <main>
      <h1>Online Shop</h1>
      <ul>
        {products.map((product) => (
          <li key={product.id}>
            {product.name} - {product.priceCents} {product.currency}
          </li>
        ))}
      </ul>
    </main>
  );
}

export default App;