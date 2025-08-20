<%--
  Created by IntelliJ IDEA.
  User: Niwanthi
  Date: 8/9/2025
  Time: 6:37 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="sidebar.jsp" %>

<%
    String loggedInUserRole = (String) session.getAttribute("role");
%>

<html>
<head>
    <title>Pahana Edu Help Center</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://cdn.jsdelivr.net/npm/feather-icons/dist/feather.min.js"></script>
    <style>
        body {
            font-family: 'Inter', sans-serif;
            background-color: #f0f4f8;
        }
        .card-hover {
            transition: all 0.3s ease;
            transform: translateY(0);
        }
        .card-hover:hover {
            transform: translateY(-5px);
            box-shadow: 0 20px 35px rgba(0,0,0,0.08);
        }
    </style>
</head>
<body class="flex">

<div class="ml-64 w-full p-10 space-y-10">

    <!-- Hero Section -->
    <div class="rounded-xl bg-gradient-to-r from-cyan-500 to-purple-600 text-white p-10 shadow-lg relative overflow-hidden">
        <i data-feather="help-circle" class="absolute w-24 h-24 opacity-20 top-5 right-10"></i>
        <h1 class="text-4xl font-bold mb-2">Welcome to Pahana Edu Help Center</h1>
        <p class="text-lg opacity-90">Quick guides, tutorials, and tips for managing your Online Billing System.</p>
    </div>

    <!-- Role-based Welcome Card -->
    <div class="p-6 rounded-xl shadow-md bg-white border border-gray-200">
        <c:choose>
            <c:when test='<%= "ADMIN".equals(loggedInUserRole) %>'>
                <h2 class="text-2xl font-bold text-cyan-700 mb-3">Hello Administrator!</h2>
                <p class="text-gray-700">Manage customers, items, billing, and reports efficiently. All system functionalities are available to you.</p>
            </c:when>
            <c:otherwise>
                <h2 class="text-2xl font-bold text-blue-700 mb-3">Hello User!</h2>
                <p class="text-gray-700">Here’s a guide to perform your daily tasks like creating bills, managing customers, and viewing items.</p>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- How-To Guides Grid -->
    <div>
        <h2 class="text-2xl font-semibold text-gray-800 mb-6 flex items-center gap-2">
            <i data-feather="book-open" class="w-6 h-6 text-gray-600"></i> How-To Guides
        </h2>
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">

            <!-- Card: Login/Logout -->
            <div class="card-hover bg-white rounded-xl p-6 border-l-4 border-cyan-500 shadow-sm hover:shadow-lg">
                <div class="flex items-center gap-3 mb-4">
                    <i data-feather="log-in" class="w-6 h-6 text-cyan-500"></i>
                    <h3 class="text-lg font-semibold">Login & Logout</h3>
                </div>
                <p class="text-gray-600 text-sm">Use your credentials to access the system. Always logout after completing your tasks to secure your account.</p>
            </div>

            <!-- Card: Create Bill -->
            <div class="card-hover bg-white rounded-xl p-6 border-l-4 border-purple-500 shadow-sm hover:shadow-lg">
                <div class="flex items-center gap-3 mb-4">
                    <i data-feather="credit-card" class="w-6 h-6 text-purple-500"></i>
                    <h3 class="text-lg font-semibold">Create New Bill</h3>
                </div>
                <p class="text-gray-600 text-sm">Select a customer, add items, and print invoices quickly and easily.</p>
            </div>

            <!-- Card: Customer Management (Admin Only) -->
            <c:if test='<%= "ADMIN".equals(loggedInUserRole) %>'>
                <div class="card-hover bg-white rounded-xl p-6 border-l-4 border-green-500 shadow-sm hover:shadow-lg">
                    <div class="flex items-center gap-3 mb-4">
                        <i data-feather="users" class="w-6 h-6 text-green-500"></i>
                        <h3 class="text-lg font-semibold">Manage Customers</h3>
                    </div>
                    <p class="text-gray-600 text-sm">Add, edit, or remove customers and keep your database updated efficiently.</p>
                </div>
            </c:if>

            <!-- Card: Item Management (Admin Only) -->
            <c:if test='<%= "ADMIN".equals(loggedInUserRole) %>'>
                <div class="card-hover bg-white rounded-xl p-6 border-l-4 border-yellow-500 shadow-sm hover:shadow-lg">
                    <div class="flex items-center gap-3 mb-4">
                        <i data-feather="package" class="w-6 h-6 text-yellow-500"></i>
                        <h3 class="text-lg font-semibold">Manage Items</h3>
                    </div>
                    <p class="text-gray-600 text-sm">Add new items, update prices, and manage stock with ease.</p>
                </div>
            </c:if>

        </div>
    </div>

    <!-- Contact Support -->
    <div class="mt-10 p-6 bg-gradient-to-r from-cyan-100 to-purple-100 rounded-xl text-center shadow-md border border-gray-200">
        <h3 class="text-xl font-semibold text-gray-800 mb-2">Need More Help?</h3>
        <p class="text-gray-700">Contact IT Support at <a href="pahanaedu@.lk" class="text-cyan-600 font-medium">pahanaedu@.lk</a></p>
    </div>

</div>

<script>
    feather.replace();
</script>

</body>
</html>
