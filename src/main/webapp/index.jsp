<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Secure Login</title>

  <script src="https://cdn.tailwindcss.com"></script>
  <script src="https://unpkg.com/feather-icons"></script>

  <style>
    .left-side-bg {
      background-color: #1e3a8a;
    }
    input:focus {
      box-shadow: 0 0 0 3px rgba(96, 165, 250, 0.4);
    }
    .login-button {
      background-color: #2563eb;
    }
    .login-button:hover {
      background-color: #1d4ed8;
    }
  </style>
</head>
<body class="bg-gray-50 min-h-screen flex items-center justify-center font-sans">

<div class="flex bg-white rounded-2xl shadow-2xl overflow-hidden w-full max-w-5xl mx-4 my-8">

  <div class="w-1/2 left-side-bg hidden md:flex flex-col items-center justify-center p-10 text-white relative overflow-hidden">
    <img src="https://images.unsplash.com/photo-1521587760476-6c12a4b040da?q=80&w=2070&auto=format&fit=crop"
         alt="Modern library interior"
         class="absolute inset-0 w-full h-full object-cover opacity-45">

    <div class="relative z-10 text-center">
      <div class="flex items-center justify-center text-4xl font-bold text-white mb-6">
        <i data-feather="book-open" class="w-12 h-12 mr-4"></i>
        <span>Pahana Edu</span>
      </div>
      <h2 class="text-3xl font-extrabold mb-4 leading-tight">Your Gateway to Seamless Bookshop Management</h2>
      <p class="text-md opacity-80">Efficiently manage customer accounts and streamline billing for your bookshop operations.</p>
    </div>

    <div class="absolute bottom-8 right-8 text-sm z-10">
      © 2025 All Rights Reserved.
    </div>
  </div>

  <div class="w-full md:w-1/2 p-8 lg:p-12 flex flex-col justify-center">
    <div class="text-center mb-8 md:hidden">
      <div class="flex items-center justify-center text-3xl font-bold text-gray-800 mb-3">
        <div class="w-14 h-14 bg-blue-600 text-white rounded-full flex items-center justify-center mr-3 shadow-lg">
          <i data-feather="book-open" class="w-6 h-6"></i>
        </div>
        Pahana Edu
      </div>
      <p class="text-gray-600 text-sm mt-1">Online Billing System</p>
    </div>

    <h2 class="text-3xl font-bold text-gray-800 text-center mb-4">Account Login</h2>
    <p class="text-gray-600 text-center mb-8 text-sm">Please enter your credentials to access the system.</p>

    <form action="${pageContext.request.contextPath}/users" method="post" class="space-y-6">
      <div>
        <label for="username" class="block text-sm font-medium text-gray-700 mb-2">Username</label>
        <div class="relative">
          <span class="absolute left-4 top-1/2 transform -translate-y-1/2 text-gray-400">
            <i data-feather="user" class="w-5 h-5"></i>
          </span>
          <input type="text" id="username" name="username" placeholder="Your username" required
                 class="w-full pl-12 pr-4 py-3 border border-gray-300 rounded-lg text-base focus:border-blue-500 focus:outline-none transition duration-200"/>
        </div>
      </div>

      <div>
        <label for="password" class="block text-sm font-medium text-gray-700 mb-2">Password</label>
        <div class="relative">
          <span class="absolute left-4 top-1/2 transform -translate-y-1/2 text-gray-400">
            <i data-feather="lock" class="w-5 h-5"></i>
          </span>
          <input type="password" id="password" name="password" placeholder="Your password" required
                 class="w-full pl-12 pr-4 py-3 border border-gray-300 rounded-lg text-base focus:border-blue-500 focus:outline-none transition duration-200"/>
        </div>
      </div>

      <div class="pt-4">
        <button type="submit"
                class="login-button w-full text-white py-3 rounded-lg font-semibold text-lg shadow-md hover:shadow-lg transition duration-300 ease-in-out">
          Log In
        </button>
      </div>
    </form>
  </div>
</div>

<script>
  feather.replace()
</script>

</body>
</html>
