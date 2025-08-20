<%--
  Created by IntelliJ IDEA.
  User: Niwanthi
  Date: 8/4/2025
  Time: 11:12 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="sidebar.jsp" %>
<%@ page import="java.time.LocalDateTime, java.time.format.DateTimeFormatter" %>
<%
    int itemStock = 152;
    int placedOrders = 37;
    LocalDateTime now = LocalDateTime.now();
    String formattedDateTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
%>

<html>
<head>
    <title>Dashboard - Pahana Edu</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://unpkg.com/feather-icons"></script>
    <style>
        body { font-family: 'Inter', sans-serif; background-color: #f0f4f8; }
        .card-hover { transition: all 0.3s ease; transform: translateY(0); }
        .card-hover:hover { transform: translateY(-6px); box-shadow: 0 20px 35px rgba(0,0,0,0.08); }
        .icon-circle { padding: 12px; border-radius: 9999px; display: inline-flex; align-items: center; justify-content: center; }
        .gradient-cyan { background: linear-gradient(135deg,#06b6d4,#3b82f6); color: white; }
        .gradient-purple { background: linear-gradient(135deg,#9333ea,#8b5cf6); color: white; }
        .gradient-green { background: linear-gradient(135deg,#16a34a,#22c55e); color: white; }
        .gradient-yellow { background: linear-gradient(135deg,#facc15,#fcd34d); color: white; }
    </style>
</head>
<body class="flex">

<div class="ml-64 p-10 space-y-10 w-full">

    <div class="rounded-xl bg-gradient-to-r from-cyan-500 to-purple-600 text-white p-10 shadow-lg relative overflow-hidden">
        <i data-feather="book-open" class="absolute w-24 h-24 opacity-20 top-5 right-10"></i>

        <h1 class="text-4xl font-bold mb-3 flex items-center gap-3">
            <i data-feather="book" class="w-10 h-10"></i>
            Welcome to Pahana Edu Dashboard
        </h1>
        <p class="text-lg opacity-90 mb-4">
            Get insights and manage your <span class="font-semibold">Pahana Edu Bookshop</span> efficiently. Track customers, orders, and inventory with ease.
        </p>
        <p class="text-sm opacity-80">
            Current Date & Time: <span class="font-bold"><%= formattedDateTime %></span>
        </p>
    </div>

    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-8">

        <div class="p-6 rounded-2xl bg-white card-hover border-l-4 border-cyan-500 shadow-sm">
            <div class="flex items-center gap-4 mb-4">
                <div class="icon-circle gradient-cyan">
                    <i data-feather="users" class="w-6 h-6"></i>
                </div>
                <h3 class="text-lg font-semibold text-gray-800">Customers Total</h3>
            </div>
            <h2 class="text-3xl font-bold text-gray-900" id="totalCustomers">0</h2>
            <p class="text-gray-500 text-sm">Customers registered today</p>
        </div>

        <div class="p-6 rounded-2xl bg-white card-hover border-l-4 border-green-500 shadow-sm">
            <div class="flex items-center gap-4 mb-4">
                <div class="icon-circle gradient-green">
                    <i data-feather="package" class="w-6 h-6"></i>
                </div>
                <h3 class="text-lg font-semibold text-gray-800">Item Stock</h3>
            </div>
            <h2 class="text-3xl font-bold text-gray-900"><%= itemStock %></h2>
            <p class="text-gray-500 text-sm">Total items available</p>
        </div>

        <div class="p-6 rounded-2xl bg-white card-hover border-l-4 border-purple-500 shadow-sm">
            <div class="flex items-center gap-4 mb-4">
                <div class="icon-circle gradient-purple">
                    <i data-feather="shopping-cart" class="w-6 h-6"></i>
                </div>
                <h3 class="text-lg font-semibold text-gray-800">Orders Placed</h3>
            </div>
            <h2 class="text-3xl font-bold text-gray-900"><%= placedOrders %></h2>
            <p class="text-gray-500 text-sm">Orders processed today</p>
        </div>

        <div class="p-6 rounded-2xl bg-white card-hover border-l-4 border-yellow-500 shadow-sm">
            <div class="flex items-center gap-4 mb-4">
                <div class="icon-circle gradient-yellow">
                    <i data-feather="help-circle" class="w-6 h-6"></i>
                </div>
                <h3 class="text-lg font-semibold text-gray-800">Support</h3>
            </div>
            <p class="text-gray-600 text-sm">Contact IT: <a href="mailto:pahnaedu@bookshop.lk" class="text-cyan-600 font-medium">pahnaedu@bookshop.lk</a></p>
        </div>

    </div>

    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
        <div class="p-6 rounded-2xl bg-white shadow-sm hover:shadow-lg transition cursor-pointer">
            <div class="flex items-center gap-3 mb-3">
                <i data-feather="plus-circle" class="w-6 h-6 text-cyan-500"></i>
                <h4 class="font-semibold text-gray-800">Add Customer</h4>
            </div>
            <p class="text-gray-500 text-sm">Quickly add a new customer account</p>
        </div>
        <div class="p-6 rounded-2xl bg-white shadow-sm hover:shadow-lg transition cursor-pointer">
            <div class="flex items-center gap-3 mb-3">
                <i data-feather="package" class="w-6 h-6 text-green-500"></i>
                <h4 class="font-semibold text-gray-800">Manage Items</h4>
            </div>
            <p class="text-gray-500 text-sm">Update stock, prices or add new items</p>
        </div>
        <div class="p-6 rounded-2xl bg-white shadow-sm hover:shadow-lg transition cursor-pointer">
            <div class="flex items-center gap-3 mb-3">
                <i data-feather="shopping-cart" class="w-6 h-6 text-purple-500"></i>
                <h4 class="font-semibold text-gray-800">Place Order</h4>
            </div>
            <p class="text-gray-500 text-sm">Create a new customer order quickly</p>
        </div>
        <div class="p-6 rounded-2xl bg-white shadow-sm hover:shadow-lg transition cursor-pointer">
            <div class="flex items-center gap-3 mb-3">
                <i data-feather="bar-chart-2" class="w-6 h-6 text-yellow-500"></i>
                <h4 class="font-semibold text-gray-800">Reports</h4>
            </div>
            <p class="text-gray-500 text-sm">View analytics and system reports</p>
        </div>
    </div>

</div>

<script>
    feather.replace();

    async function loadTotalCustomers() {
        try {
            const contextPath = window.location.pathname.split("/")[1] ? "/" + window.location.pathname.split("/")[1] : "";
            const response = await fetch(contextPath + '/customers/count');
            const data = await response.json();
            if (data.total !== undefined) {
                document.getElementById('totalCustomers').textContent = data.total;
            }
        } catch (error) {
            console.error("Failed to load total customers:", error);
        }
    }

    document.addEventListener('DOMContentLoaded', loadTotalCustomers);
</script>
</body>
</html>

