const itemsContainer = document.getElementById("items-container");
const cartCount = document.getElementById("cart-count");

let cart = JSON.parse(localStorage.getItem("cart")) || [];

function saveCart() {
  localStorage.setItem("cart", JSON.stringify(cart));
}

function updateCartUI() {
  if (cartCount) {
    const count = cart.reduce((sum, item) => sum + item.quantity, 0);
    cartCount.textContent = count;
  }
  saveCart();
}

function addToCart(item) {
  const userId = localStorage.getItem("user_id");
  if (!userId) {
    alert("Please log in to add items.");
    return;
  }

  const existing = cart.find(i => i.id === item.id && i.source === "ingredients");
  if (existing) {
    existing.quantity += 1;
  } else {
    cart.push({ ...item, quantity: 1, source: "ingredients" });
  }

  updateCartUI();
  alert(`${item.name} added to cart!`);

  // Sync to backend
  const formattedCart = cart.map(i => ({
    user_id: userId,
    item_id: i.id,
    name: i.name,
    price: i.price,
    quantity: i.quantity,
    imageurl: i.imageURL || i.image_url,
    source: i.source || "items"
  }));

  fetch("/api/cart", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      user_id: userId,
      cart_items: formattedCart
    })
  }).catch(err => console.error("Failed to sync ingredients cart:", err));
}



let ingredients = [];

function renderIngredients(items) {
  ingredients = items;
  itemsContainer.innerHTML = "";

  items.forEach(item => {
    item.source = "ingredients";

    const itemDiv = document.createElement("div");
    itemDiv.classList.add("item");

    itemDiv.innerHTML = `
      <img src="${item.imageURL}" alt="${item.name}" class="item-image" />
      <h3>${item.name}</h3>
      <p>Price: ₹${item.price}</p>
      <p>Available: ${item.quantity}</p>
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


document.addEventListener("DOMContentLoaded", () => {
  fetch("/api/ingredients")
    .then(res => res.json())
    .then(data => renderIngredients(data))
    .catch(err => console.error("Failed to fetch ingredients:", err));

  updateCartUI();
});
