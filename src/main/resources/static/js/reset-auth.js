// File: js/reset-auth.js
import { createClient } from "https://cdn.jsdelivr.net/npm/@supabase/supabase-js/+esm";

let supabase;

async function initSupabase() {
  const res = await fetch("/api/supabase-config");
  const config = await res.json();
  supabase = createClient(config.url, config.key);
}

initSupabase().then(() => {
  const requestForm = document.getElementById("reset-request-form");
  const passwordForm = document.getElementById("reset-password-form");

  // Step 1: Handle reset request
  if (requestForm) {
    requestForm.addEventListener("submit", async (e) => {
  e.preventDefault();
  const email = document.getElementById("reset-email").value;
  const msg = document.getElementById("reset-request-message");

  // Step 1: Check if user exists in our `users` table
  const { data: user, error: fetchError } = await supabase
    .from("users")
    .select("email")
    .eq("email", email)
    .single();

  if (fetchError || !user) {
    msg.textContent = "❌ No account found with this email.";
    return;
  }

  // Step 2: Proceed to send reset link
  const { error } = await supabase.auth.resetPasswordForEmail(email, {
    redirectTo: "https://the-hunger-hub.onrender.com/reset-password.html",
  });

  if (error) {
    msg.textContent = "❌ " + error.message;
  } else {
    msg.textContent = "✅ Reset link sent to your email.";
  }
});

  }

  // Step 2: Handle actual reset via token
  if (passwordForm) {
    passwordForm.addEventListener("submit", async (e) => {
      e.preventDefault();
      const newPassword = document.getElementById("new-password").value;
      const confirmPassword = document.getElementById("confirm-password").value;
      const msg = document.getElementById("reset-password-message");

      if (newPassword !== confirmPassword) {
        msg.textContent = "❌ Passwords do not match.";
        return;
      }

      const { data, error } = await supabase.auth.updateUser({
        password: newPassword,
      });

      if (error) {
        msg.textContent = "❌ " + error.message;
      } else {
        msg.textContent = "✅ Password updated! Redirecting to login...";
        setTimeout(() => {
          window.location.href = "login.html";
        }, 2000);
      }
    });
  }
});
