let cart = JSON.parse(localStorage.getItem("cart")) || [];
let supabase;

const itemsContainer = document.getElementById("items-container");
const cartItems = document.getElementById("cart-items");
const totalElement = document.getElementById("total");
const cartCount = document.getElementById("cart-count");

async function fetchSupabaseConfig() {
  try {
    const res = await fetch("/api/supabase-config");
    const config = await res.json();
    supabase = window.supabase.createClient(config.url, config.key);
  } catch (error) {
    console.error("Failed to load Supabase config:", error);
  }
}

function saveCart() {
  localStorage.setItem("cart", JSON.stringify(cart));
}

function calculateTotal() {
  return cart.reduce((sum, item) => sum + item.price * item.quantity, 0);
}

async function updateCartUI() {
  if (cartItems) {
    cartItems.innerHTML = "";
    cart.forEach((item, index) => {
      const li = document.createElement("li");
      li.innerHTML = `
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <div>
            <strong>${item.name}</strong><br>
            ₹${item.price} × ${item.quantity}
          </div>
          <div style="display: flex; gap: 5px;">
            <button onclick="decreaseQty(${index})">–</button>
            <button onclick="increaseQty(${index})">+</button>
            <button onclick="removeItem(${index})">🗑</button>
          </div>
        </div>
      `;
      cartItems.appendChild(li);
    });
  }

  if (totalElement) totalElement.textContent = calculateTotal();
  if (cartCount) {
    const count = cart.reduce((sum, item) => sum + item.quantity, 0);
    cartCount.textContent = count;
  }

  const userId = localStorage.getItem("user_id");
  if (userId) {
    const formattedCart = cart.map(item => ({
      user_id: userId,
      item_id: item.id,
      name: item.name,
      price: item.price,
      quantity: item.quantity,
      imageurl: item.imageURL || item.image_url,
      source: item.source || "items"
    }));

    try {
      await fetch("/api/cart", {
  method: "POST",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify({
    user_id: userId,
    cart_items: formattedCart
  })
});

    } catch (err) {
      console.error("Failed to sync cart with backend:", err);
    }
  }

  saveCart(); // Ensure latest local cart
}

function addToCart(item) {
  const index = cart.findIndex(i => i.id === item.id);
  if (index !== -1) {
    cart[index].quantity += 1;
  } else {
    cart.push({ ...item, quantity: 1 });
  }
  saveCart();
  updateCartUI(); // Only 1 backend sync per click
  alert(`${item.name} added to cart!`);
}

function increaseQty(index) {
  cart[index].quantity += 1;
  updateCartUI();
}

function decreaseQty(index) {
  if (cart[index].quantity > 1) {
    cart[index].quantity -= 1;
  } else {
    cart.splice(index, 1);
  }
  updateCartUI();
}

function removeItem(index) {
  cart.splice(index, 1);
  updateCartUI();
}

function checkout() {
  if (cart.length === 0) {
    alert("Cart is empty!");
    return;
  }

  fetch("/api/checkout", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(
      cart.map(item => ({
        id: item.id,
        name: item.name,
        price: item.price,
        quantity: item.quantity,
        imageURL: item.imageURL,
        source: item.source || "items"
      }))
    )
  })
    .then(res => {
      if (!res.ok) throw new Error("Checkout failed");
      return res.json();
    })
    .then(async data => {
      alert("Order placed successfully! Total: ₹" + data.total);
      cart = [];
      try {
  await fetch("/api/cart", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
  user_id: localStorage.getItem("user_id"),
  cart_items: []
})

  });
} catch (err) {
  console.error("Failed to clear cart in backend:", err);
}


      updateCartUI();
    })
    .catch(err => {
      console.error("Checkout error:", err);
      alert("Checkout failed. Please try again.");
    });
}

// Render menu items
function renderItems(items) {
  itemsContainer.innerHTML = "";
  items.forEach(item => {
    const itemDiv = document.createElement("div");
    itemDiv.classList.add("item");

    itemDiv.innerHTML = `
      <img src="${item.imageURL}" alt="${item.name}" class="item-image" />
      <h3>${item.name}</h3>
      <p>Quantity: ${item.quantity}</p>
      <p>Price: ₹${item.price}</p>
      <button 
        class="add-to-cart-btn ${item.quantity === 0 ? 'out-of-stock' : ''}" 
        ${item.quantity === 0 ? 'disabled' : `onclick='addToCart(${JSON.stringify(item)})'`}
      >
        ${item.quantity === 0 ? 'Out of Stock' : 'Add to Cart'}
      </button>
    `;

    itemsContainer.appendChild(itemDiv);
  });
}

document.addEventListener("DOMContentLoaded", async () => {
  await fetchSupabaseConfig();

  if (itemsContainer) {
    fetch("/api/items")
      .then(res => res.json())
      .then(data => renderItems(data))
      .catch(err => console.error("Failed to fetch items:", err));
  }

  updateCartUI();
});
