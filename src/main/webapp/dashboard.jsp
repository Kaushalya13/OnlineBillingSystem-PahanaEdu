<%--
  Created by IntelliJ IDEA.
  User: Niwanthi
  Date: 8/4/2025
  Time: 11:12 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="sidebar.jsp" %>
<html>
<head>
    <title>Dashboard</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://unpkg.com/feather-icons"></script>
    <style>
        body {
            background-color: #f7fafc;
        }
        .card-light {
            transition: all 0.3s ease;
            box-shadow: 0 6px 18px rgba(0, 0, 0, 0.08);
            border: 1px solid #e2e8f0;
            background-color: #ffffff;
        }
        .card-light:hover {
            transform: translateY(-8px);
            box-shadow: 0 16px 32px rgba(0, 0, 0, 0.1);
        }
        .icon-accent {
            background-color: #e0f2fe; /* Light blue accent background */
            color: #0c4a6e;
        }
        .icon-accent-green {
            background-color: #d1fae5;
            color: #065f46;
        }
        .icon-accent-pink {
            background-color: #fce7f3;
            color: #9d174d;
        }
        .icon-accent-yellow {
            background-color: #fef3c7;
            color: #92400e;
        }
    </style>
</head>
<body class="font-sans text-gray-900">

<div class="ml-64 p-8">
    <div class="flex justify-between items-center mb-10">
        <h1 class="text-4xl font-extrabold text-gray-900">Creative Analytics</h1>
    </div>

    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-8 mb-12">
        <div class="p-8 rounded-2xl card-light">
            <div class="flex items-center justify-between mb-6">
                <div class="p-3 rounded-full icon-accent-green">
                    <i data-feather="trending-up" class="w-7 h-7"></i>
                </div>
                <p class="text-lg font-semibold text-gray-500">Total Revenue</p>
            </div>
            <h3 class="text-5xl font-bold text-gray-900">$12,450</h3>
            <div class="mt-4 text-sm flex items-center text-green-600">
                <i data-feather="arrow-up-right" class="w-4 h-4 mr-1"></i>
                <span class="font-bold">25.5%</span> this month
            </div>
        </div>

        <div class="p-8 rounded-2xl card-light">
            <div class="flex items-center justify-between mb-6">
                <div class="p-3 rounded-full icon-accent">
                    <i data-feather="users" class="w-7 h-7"></i>
                </div>
                <p class="text-lg font-semibold text-gray-500">New Sign-ups</p>
            </div>
            <h3 class="text-5xl font-bold text-gray-900">3,124</h3>
            <div class="mt-4 text-sm flex items-center text-blue-600">
                <i data-feather="arrow-up-right" class="w-4 h-4 mr-1"></i>
                <span class="font-bold">18.2%</span> this month
            </div>
        </div>

        <div class="p-8 rounded-2xl card-light">
            <div class="flex items-center justify-between mb-6">
                <div class="p-3 rounded-full icon-accent-pink">
                    <i data-feather="shopping-bag" class="w-7 h-7"></i>
                </div>
                <p class="text-lg font-semibold text-gray-500">New Orders</p>
            </div>
            <h3 class="text-5xl font-bold text-gray-900">867</h3>
            <div class="mt-4 text-sm flex items-center text-pink-600">
                <i data-feather="arrow-up-right" class="w-4 h-4 mr-1"></i>
                <span class="font-bold">15.0%</span> this month
            </div>
        </div>

        <div class="p-8 rounded-2xl card-light">
            <div class="flex items-center justify-between mb-6">
                <div class="p-3 rounded-full icon-accent-yellow">
                    <i data-feather="message-circle" class="w-7 h-7"></i>
                </div>
                <p class="text-lg font-semibold text-gray-500">Open Tickets</p>
            </div>
            <h3 class="text-5xl font-bold text-gray-900">42</h3>
            <div class="mt-4 text-sm flex items-center text-yellow-600">
                <i data-feather="trending-down" class="w-4 h-4 mr-1"></i>
                <span class="font-bold">8.5%</span> last month
            </div>
        </div>
    </div>

</div>

<script>
    feather.replace();
</script>
</body>
</html>