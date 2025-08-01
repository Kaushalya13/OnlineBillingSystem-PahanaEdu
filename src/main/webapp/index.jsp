<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Secure Login</title>

  <script src="https://cdn.tailwindcss.com"></script>

  <script src="https://unpkg.com/feather-icons"></script>

  <style>
    .left-side-gradient {
      background: linear-gradient(135deg, #6B46C1 0%, #805AD5 100%);
    }
    input:focus {
      box-shadow: 0 0 0 3px rgba(129, 140, 248, 0.4);
    }
  </style>
</head>
<body class="bg-gray-50 min-h-screen flex items-center justify-center font-sans">

<div class="flex bg-white rounded-2xl shadow-2xl overflow-hidden w-full max-w-5xl mx-4 my-8">

  <div class="w-1/2 left-side-gradient hidden md:flex flex-col items-center justify-center p-10 text-white relative">
    <div class="absolute top-8 left-8">
      <div class="flex items-center text-4xl font-bold text-black">
        <i data-feather="book-open" class="w-24 h-24 mb-6 opacity-80 mr-3"></i>
        Pahana Edu
      </div>
    </div>
    <div class="text-center">
      <h2 class="text-4xl font-extrabold mb-4 leading-tight">Your Gateway to Sexamless Bookshop Management</h2>
      <p class="text-md opacity-90 text-black">Efficiently manage customer accounts and streamline billing for your bookshop operations.</p>
    </div>
    <div class="absolute bottom-8 right-8 text-sm opacity-70">
      © 2025 All Rights Reserved.
    </div>
  </div>

  <div class="w-full md:w-1/2 p-8 lg:p-12 flex flex-col justify-center">
    <div class="text-center mb-8 md:hidden"> <div class="flex items-center justify-center text-3xl font-bold text-gray-800 mb-3">
      <div class="w-14 h-14 bg-purple-600 text-white rounded-full flex items-center justify-center mr-3 shadow-lg">
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
                 class="w-full pl-12 pr-4 py-3 border border-gray-300 rounded-lg text-base focus:border-purple-500 focus:outline-none transition duration-200"/>
        </div>
      </div>

      <div>
        <label for="password" class="block text-sm font-medium text-gray-700 mb-2">Password</label>
        <div class="relative">
          <span class="absolute left-4 top-1/2 transform -translate-y-1/2 text-gray-400">
            <i data-feather="lock" class="w-5 h-5"></i>
          </span>
          <input type="password" id="password" name="password" placeholder="Your password" required
                 class="w-full pl-12 pr-4 py-3 border border-gray-300 rounded-lg text-base focus:border-purple-500 focus:outline-none transition duration-200"/>
        </div>
      </div>

      <div class="flex items-center justify-between text-sm">
        <div class="flex items-center">
          <input id="remember_me" name="remember_me" type="checkbox"
                 class="h-4 w-4 text-purple-600 focus:ring-purple-500 border-gray-300 rounded"/>
          <label for="remember_me" class="ml-2 block text-gray-900">
            Remember me
          </label>
        </div>
        <a href="#" class="font-medium text-purple-600 hover:text-purple-500 hover:underline">
          Forgot password?
        </a>
      </div>

      <div class="pt-4">
        <button type="submit"
                class="w-full bg-purple-600 text-white py-3 rounded-lg hover:bg-purple-700 font-semibold text-lg shadow-md hover:shadow-lg transition duration-300 ease-in-out">
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