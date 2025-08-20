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
    LocalDateTime now = LocalDateTime.now();
    String formattedDateTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
%>

<html>
<head>
    <title>Dashboard - Pahana Edu</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://unpkg.com/feather-icons"></script>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        body { font-family: 'Inter', sans-serif; background-color: #f0f4f8; }
        .card-hover { transition: all 0.3s ease; transform: translateY(0); }
        .card-hover:hover { transform: translateY(-6px); box-shadow: 0 20px 35px rgba(0,0,0,0.08); }
        .icon-circle { padding: 12px; border-radius: 9999px; display: inline-flex; align-items: center; justify-content: center; }
        .gradient-cyan { background: linear-gradient(135deg,#06b6d4,#3b82f6); color: white; }
        .gradient-purple { background: linear-gradient(135deg,#9333ea,#8b5cf6); color: white; }
        .gradient-green { background: linear-gradient(135deg,#16a34a,#22c55e); color: white; }
    </style>
</head>
<body class="flex">

<div class="ml-64 p-10 w-full space-y-10">

    <div class="rounded-xl bg-gradient-to-r from-cyan-500 to-purple-600 text-white p-10 shadow-lg relative overflow-hidden">
        <i data-feather="book-open" class="absolute w-24 h-24 opacity-20 top-5 right-10"></i>
        <h1 class="text-4xl font-bold mb-3 flex items-center gap-3">
            <i data-feather="book" class="w-10 h-10"></i>
            Welcome to Pahana Edu Dashboard
        </h1>
        <p class="text-lg opacity-90 mb-4">
            Get insights and manage your <span class="font-semibold">Pahana Edu Bookshop</span> efficiently.
        </p>
        <p class="text-sm opacity-80">
            Current Date & Time: <span class="font-bold"><%= formattedDateTime %></span>
        </p>
    </div>

    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8">
        <div class="p-6 rounded-2xl bg-white card-hover border-l-4 border-cyan-500 shadow-sm">
            <div class="flex items-center gap-4 mb-4">
                <div class="icon-circle gradient-cyan"><i data-feather="users" class="w-6 h-6"></i></div>
                <h3 class="text-lg font-semibold text-gray-800">Customers Total</h3>
            </div>
            <h2 class="text-3xl font-bold text-gray-900" id="totalCustomers">0</h2>
            <p class="text-gray-500 text-sm">Total Customers</p>
        </div>

        <div class="p-6 rounded-2xl bg-white card-hover border-l-4 border-green-500 shadow-sm">
            <div class="flex items-center gap-4 mb-4">
                <div class="icon-circle gradient-green"><i data-feather="package" class="w-6 h-6"></i></div>
                <h3 class="text-lg font-semibold text-gray-800">Item Stock</h3>
            </div>
            <h2 class="text-3xl font-bold text-gray-900" id="itemStock">0</h2>
            <p class="text-gray-500 text-sm">Total items available</p>
        </div>

        <div class="p-6 rounded-2xl bg-white card-hover border-l-4 border-purple-500 shadow-sm">
            <div class="flex items-center gap-4 mb-4">
                <div class="icon-circle gradient-purple"><i data-feather="shopping-cart" class="w-6 h-6"></i></div>
                <h3 class="text-lg font-semibold text-gray-800">Orders Placed</h3>
            </div>
            <h2 class="text-3xl font-bold text-gray-900" id="placedOrders">0</h2>
            <p class="text-gray-500 text-sm">Orders processed today</p>
        </div>
    </div>

    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
        <div class="p-6 rounded-2xl bg-white shadow-sm hover:shadow-lg transition cursor-pointer">
            <div class="flex items-center gap-3 mb-3">
                <i data-feather="plus-circle" class="w-6 h-6 text-cyan-500"></i>
                <h4 class="font-semibold text-gray-800">Add Customer</h4>
            </div>
            <p class="text-gray-500 text-sm">Quickly add a new customer</p>
        </div>
        <div class="p-6 rounded-2xl bg-white shadow-sm hover:shadow-lg transition cursor-pointer">
            <div class="flex items-center gap-3 mb-3">
                <i data-feather="package" class="w-6 h-6 text-green-500"></i>
                <h4 class="font-semibold text-gray-800">Manage Items</h4>
            </div>
            <p class="text-gray-500 text-sm">Update stock or add items</p>
        </div>
        <div class="p-6 rounded-2xl bg-white shadow-sm hover:shadow-lg transition cursor-pointer">
            <div class="flex items-center gap-3 mb-3">
                <i data-feather="shopping-cart" class="w-6 h-6 text-purple-500"></i>
                <h4 class="font-semibold text-gray-800">Place Order</h4>
            </div>
            <p class="text-gray-500 text-sm">Create a new customer order</p>
        </div>
    </div>

    <div class="bg-white p-6 rounded-2xl shadow-sm max-w-sm">
        <h3 class="text-lg font-semibold text-gray-800 mb-4 text-center">Available Items Stock</h3>
        <canvas id="itemsChart"></canvas>
    </div>

</div>

<script>
    feather.replace();

    const contextPath = window.location.pathname.split("/")[1] ? "/" + window.location.pathname.split("/")[1] : "";

    async function loadDashboardData() {
        try {
            const response = await fetch(contextPath + '/dashboard/data');
            if (!response.ok) throw new Error("Network response was not ok");
            const data = await response.json();

            document.getElementById('totalCustomers').textContent = data.totalCustomers;
            document.getElementById('itemStock').textContent = data.itemStock;
            document.getElementById('placedOrders').textContent = data.placedOrders;

            const itemNames = data.items.map(item => item.itemName);
            const itemStocks = data.items.map(item => item.quantity);

            const ctx = document.getElementById('itemsChart').getContext('2d');
            new Chart(ctx, {
                type: 'pie',
                data: {
                    labels: itemNames,
                    datasets: [{
                        label: 'Available Stock',
                        data: itemStocks,
                        backgroundColor: [
                            'rgba(255, 99, 132, 0.7)',
                            'rgba(54, 162, 235, 0.7)',
                            'rgba(255, 206, 86, 0.7)',
                            'rgba(75, 192, 192, 0.7)',
                            'rgba(153, 102, 255, 0.7)',
                            'rgba(255, 159, 64, 0.7)'
                        ],
                        borderColor: [
                            'rgba(255, 99, 132, 1)',
                            'rgba(54, 162, 235, 1)',
                            'rgba(255, 206, 86, 1)',
                            'rgba(75, 192, 192, 1)',
                            'rgba(153, 102, 255, 1)',
                            'rgba(255, 159, 64, 1)'
                        ],
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: {
                            display: true,
                            position: 'top'
                        },
                        tooltip: { enabled: true }
                    }
                }
            });
        } catch (error) {
            console.error("Failed to load dashboard data:", error);
        }
    }

    document.addEventListener('DOMContentLoaded', loadDashboardData);
</script>
</body>
</html>