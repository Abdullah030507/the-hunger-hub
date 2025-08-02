let supabase;
let isLoginMode = true;

async function fetchSupabaseConfig() {
  try {
    const res = await fetch("/api/supabase-config");
    const config = await res.json();
    supabase = window.supabase.createClient(config.url, config.key);
  } catch (error) {
    console.error("Failed to load Supabase config:", error);
  }
}

async function handleAuth() {
  const email = document.getElementById("email").value;
  const password = document.getElementById("password").value;
  const errorMsg = document.getElementById("auth-error");

  errorMsg.textContent = "";

  if (!email || !password) {
    errorMsg.textContent = "Please enter email and password.";
    return;
  }

  if (isLoginMode) {
    const { data, error } = await supabase.auth.signInWithPassword({ email, password });
    if (error) return errorMsg.textContent = "Login failed: " + error.message;

    const user = data.user;
    localStorage.setItem("user_id", user.id);

    const { data: userCart } = await supabase
      .from("cart_items")
      .select("*")
      .eq("user_id", user.id);

    if (userCart) {
      const parsedCart = userCart.map(item => ({
        id: item.item_id,
        name: item.name,
        price: item.price,
        quantity: item.quantity,
        imageURL: item.image_url
      }));
      localStorage.setItem("cart", JSON.stringify(parsedCart));
    }

    alert("Login successful!");
    window.location.href = "index.html";
    } else {
    // Step 1: Check if user already exists in auth.users
    const { data: existingUser, error: checkError } = await supabase
      .from("users")
      .select("id")
      .eq("email", email)
      .single();

    if (existingUser) {
      errorMsg.textContent = "User already exists. Please log in.";
      return;
    }

    // Step 2: Sign up using Supabase Auth
    const { data, error } = await supabase.auth.signUp({ email, password });
    if (error) {
      errorMsg.textContent = "Signup failed: " + error.message;
      return;
    }

    // Step 3: Check if user is truly new
    if (data.user?.identities?.length === 0) {
      errorMsg.textContent = "User already exists. Please log in.";
      return;
    }

    // Step 4: Insert into custom users table
    const userId = data.user?.id;
    if (userId) {
      const { error: insertError } = await supabase.from("users").insert({ id: userId, email });
      if (insertError) {
        console.error("Failed to insert into users table:", insertError.message);
      }
      localStorage.setItem("user_id", userId);
    }

    alert("Signup successful! Please check your email.");
  }

}

function toggleAuthMode() {
  isLoginMode = !isLoginMode;

  const formTitle = document.getElementById("form-title");
  const authButton = document.getElementById("auth-button");
  const toggleText = document.getElementById("toggle-auth");
  const errorMsg = document.getElementById("auth-error");
  const loginBox = document.querySelector(".login-box");

  // Trigger fade animation
  loginBox.classList.remove("slideFade");
  void loginBox.offsetWidth; // trigger reflow
  loginBox.classList.add("slideFade");

  formTitle.textContent = isLoginMode ? "Login" : "Signup";
  authButton.textContent = isLoginMode ? "Login" : "Signup";
  toggleText.innerHTML = isLoginMode
    ? `Don't have an account? <span class="link">Signup</span>`
    : `Already have an account? <span class="link">Login</span>`;
    
  errorMsg.textContent = "";
}


document.addEventListener("DOMContentLoaded", async () => {
  await fetchSupabaseConfig();
  document.getElementById("auth-button").addEventListener("click", handleAuth);
  document.getElementById("toggle-auth").addEventListener("click", toggleAuthMode);
});
