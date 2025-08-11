<%--
  Created by IntelliJ IDEA.
  User: Niwanthi
  Date: 8/4/2025
  Time: 11:21 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="sidebar.jsp" %>
<html>
<head>
    <title>Customer Management</title>
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
            transform: translateY(-4px);
            box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
        }
        .form-input-modern {
            padding: 0.75rem 1rem;
            border-radius: 0.5rem;
            border: 1px solid #e2e8f0;
            transition: all 0.2s ease;
            background-color: #ffffff;
        }
        .form-input-modern:focus {
            outline: none;
            border-color: #3b82f6;
            box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2);
        }
        .table-row-hover:hover {
            background-color: #f7faff;
            box-shadow: inset 0 0 0 1px #d1e5f8;
        }
        .modal-overlay {
            background-color: rgba(0, 0, 0, 0.5);
        }
    </style>
</head>
<body class="font-sans text-gray-900">

<div class="ml-64 p-8">
    <div class="flex justify-between items-center mb-10">
        <h1 class="text-4xl font-extrabold text-gray-900 flex items-center gap-4">
            <i data-feather="users" class="w-8 h-8 text-blue-500"></i> Customer Management
        </h1>
    </div>

    <div class="flex flex-wrap items-center justify-between gap-4 mb-8">
        <div class="flex flex-wrap items-center gap-4">
            <input type="text" placeholder="Search by name or ID"
                   class="form-input-modern w-full md:w-80" />
            <button class="bg-blue-600 text-white font-semibold px-6 py-2 rounded-lg hover:bg-blue-700 transition-colors duration-200">
                Search
            </button>
            <button class="bg-gray-200 text-gray-800 font-semibold px-6 py-2 rounded-lg hover:bg-gray-300 transition-colors duration-200">
                Clear
            </button>
        </div>
        <button id="open-add-modal-btn" class="bg-green-600 text-white font-semibold px-6 py-3 rounded-lg hover:bg-green-700 transition-colors duration-200 shadow-md flex items-center">
            <i data-feather="plus" class="w-4 h-4 inline-block mr-2"></i>
            Add Customer
        </button>
    </div>

    <div class="card-light p-8 rounded-2xl">
        <h2 class="text-2xl font-bold text-gray-900 mb-6">Customer List</h2>
        <div class="overflow-x-auto">
            <table class="min-w-full table-auto">
                <thead class="bg-gray-100 text-gray-700 font-bold">
                <tr>
                    <th class="px-6 py-3 text-left rounded-tl-xl">Customer ID</th>
                    <th class="px-6 py-3 text-left">Name</th>
                    <th class="px-6 py-3 text-left">Address</th>
                    <th class="px-6 py-3 text-left">Mobile</th>
                    <th class="px-6 py-3 text-left">Account No</th>
                    <th class="px-6 py-3 text-left">Units</th>
                    <th class="px-6 py-3 text-left rounded-tr-xl">Actions</th>
                </tr>
                </thead>
                <tbody>
                <tr class="border-b border-gray-200 table-row-hover cursor-pointer">
                    <td class="px-6 py-4 text-gray-800 font-medium">C001</td>
                    <td class="px-6 py-4 text-gray-600">Nimal Perera</td>
                    <td class="px-6 py-4 text-gray-600">Colombo</td>
                    <td class="px-6 py-4 text-gray-600">0771234567</td>
                    <td class="px-6 py-4 text-gray-600">ACC123</td>
                    <td class="px-6 py-4 text-gray-600">120</td>
                    <td class="px-6 py-4 text-gray-600">
                        <div class="flex items-center gap-2">
                            <button class="text-blue-500 hover:text-blue-700 transition-colors duration-200">
                                <i data-feather="edit" class="w-5 h-5"></i>
                            </button>
                            <button class="text-red-500 hover:text-red-700 transition-colors duration-200">
                                <i data-feather="trash-2" class="w-5 h-5"></i>
                            </button>
                            <button class="view-customer-btn text-gray-500 hover:text-gray-700 transition-colors duration-200">
                                <i data-feather="eye" class="w-5 h-5"></i>
                            </button>
                            <button class="account-details-btn text-yellow-500 hover:text-yellow-700 transition-colors duration-200">
                                <i data-feather="credit-card" class="w-5 h-5"></i>
                            </button>
                        </div>
                    </td>
                </tr>
                <tr class="border-b border-gray-200 table-row-hover cursor-pointer">
                    <td class="px-6 py-4 text-gray-800 font-medium">C002</td>
                    <td class="px-6 py-4 text-gray-600">Kamal Silva</td>
                    <td class="px-6 py-4 text-gray-600">Galle</td>
                    <td class="px-6 py-4 text-gray-600">0719876543</td>
                    <td class="px-6 py-4 text-gray-600">ACC456</td>
                    <td class="px-6 py-4 text-gray-600">90</td>
                    <td class="px-6 py-4 text-gray-600">
                        <div class="flex items-center gap-2">
                            <button class="text-blue-500 hover:text-blue-700 transition-colors duration-200">
                                <i data-feather="edit" class="w-5 h-5"></i>
                            </button>
                            <button class="text-red-500 hover:text-red-700 transition-colors duration-200">
                                <i data-feather="trash-2" class="w-5 h-5"></i>
                            </button>
                            <button class="view-customer-btn text-gray-500 hover:text-gray-700 transition-colors duration-200">
                                <i data-feather="eye" class="w-5 h-5"></i>
                            </button>
                            <button class="account-details-btn text-yellow-500 hover:text-yellow-700 transition-colors duration-200">
                                <i data-feather="credit-card" class="w-5 h-5"></i>
                            </button>
                        </div>
                    </td>
                </tr>
                <tr class="table-row-hover cursor-pointer">
                    <td class="px-6 py-4 text-gray-800 font-medium">C003</td>
                    <td class="px-6 py-4 text-gray-600">Sunil Rathnayake</td>
                    <td class="px-6 py-4 text-gray-600">Kandy</td>
                    <td class="px-6 py-4 text-gray-600">0758765432</td>
                    <td class="px-6 py-4 text-gray-600">ACC789</td>
                    <td class="px-6 py-4 text-gray-600">150</td>
                    <td class="px-6 py-4 text-gray-600">
                        <div class="flex items-center gap-2">
                            <button class="text-blue-500 hover:text-blue-700 transition-colors duration-200">
                                <i data-feather="edit" class="w-5 h-5"></i>
                            </button>
                            <button class="text-red-500 hover:text-red-700 transition-colors duration-200">
                                <i data-feather="trash-2" class="w-5 h-5"></i>
                            </button>
                            <button class="view-customer-btn text-gray-500 hover:text-gray-700 transition-colors duration-200">
                                <i data-feather="eye" class="w-5 h-5"></i>
                            </button>
                            <button class="account-details-btn text-yellow-500 hover:text-yellow-700 transition-colors duration-200">
                                <i data-feather="credit-card" class="w-5 h-5"></i>
                            </button>
                        </div>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>

<div id="add-customer-modal" class="fixed inset-0 hidden modal-overlay items-center justify-center z-50">
    <div class="bg-white p-8 rounded-2xl shadow-xl w-full max-w-2xl mx-4 card-light">
        <div class="flex justify-between items-center mb-6">
            <h2 class="text-2xl font-bold text-gray-900">Add New Customer</h2>
            <button id="close-add-modal-btn" class="text-gray-500 hover:text-gray-700">
                <i data-feather="x" class="w-6 h-6"></i>
            </button>
        </div>
        <form action="#" method="post" class="space-y-6">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                    <label for="new-customer-id" class="block text-sm font-medium text-gray-700 mb-1">Customer ID</label>
                    <input type="text" id="new-customer-id" placeholder="Enter Customer ID" class="form-input-modern w-full" />
                </div>
                <div>
                    <label for="new-name" class="block text-sm font-medium text-gray-700 mb-1">Name</label>
                    <input type="text" id="new-name" placeholder="Enter Name" class="form-input-modern w-full" />
                </div>
                <div>
                    <label for="new-address" class="block text-sm font-medium text-gray-700 mb-1">Address</label>
                    <input type="text" id="new-address" placeholder="Enter Address" class="form-input-modern w-full" />
                </div>
                <div>
                    <label for="new-mobile" class="block text-sm font-medium text-gray-700 mb-1">Mobile Number</label>
                    <input type="text" id="new-mobile" placeholder="Enter Mobile Number" class="form-input-modern w-full" />
                </div>
                <div>
                    <label for="new-account" class="block text-sm font-medium text-gray-700 mb-1">Account Number</label>
                    <input type="text" id="new-account" placeholder="Enter Account Number" class="form-input-modern w-full" />
                </div>
                <div>
                    <label for="new-units" class="block text-sm font-medium text-gray-700 mb-1">Units Consumed</label>
                    <input type="number" id="new-units" placeholder="Enter Units Consumed" class="form-input-modern w-full" />
                </div>
            </div>
            <div class="flex justify-end gap-4 mt-6">
                <button type="button" id="cancel-add-modal-btn" class="bg-gray-200 text-gray-800 font-semibold px-6 py-3 rounded-lg hover:bg-gray-300 transition-colors duration-200">
                    Cancel
                </button>
                <button type="submit" class="bg-green-600 text-white font-semibold px-6 py-3 rounded-lg transition-colors duration-200">
                    Save
                </button>
            </div>
        </form>
    </div>
</div>

<div id="view-customer-modal" class="fixed inset-0 hidden modal-overlay items-center justify-center z-50">
    <div class="bg-white p-8 rounded-2xl shadow-xl w-full max-w-md mx-4 card-light">
        <div class="flex justify-between items-center mb-6">
            <h2 class="text-2xl font-bold text-gray-900">Customer Details</h2>
            <button id="close-view-modal-btn" class="text-gray-500 hover:text-gray-700">
                <i data-feather="x" class="w-6 h-6"></i>
            </button>
        </div>
        <div class="space-y-4">
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Customer ID</label>
                <p id="view-customer-id" class="p-3 bg-gray-100 rounded-lg text-gray-800 font-semibold"></p>
            </div>
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Name</label>
                <p id="view-name" class="p-3 bg-gray-100 rounded-lg text-gray-800 font-semibold"></p>
            </div>
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Address</label>
                <p id="view-address" class="p-3 bg-gray-100 rounded-lg text-gray-800 font-semibold"></p>
            </div>
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Mobile Number</label>
                <p id="view-mobile" class="p-3 bg-gray-100 rounded-lg text-gray-800 font-semibold"></p>
            </div>
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Account Number</label>
                <p id="view-account" class="p-3 bg-gray-100 rounded-lg text-gray-800 font-semibold"></p>
            </div>
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Units Consumed</label>
                <p id="view-units" class="p-3 bg-gray-100 rounded-lg text-gray-800 font-semibold"></p>
            </div>
        </div>
        <div class="flex justify-end mt-6">
            <button id="close-view-modal-btn-bottom" class="bg-gray-200 text-gray-800 font-semibold px-6 py-3 rounded-lg hover:bg-gray-300 transition-colors duration-200">
                Close
            </button>
        </div>
    </div>
</div>

<div id="account-details-modal" class="fixed inset-0 hidden modal-overlay items-center justify-center z-50">
    <div class="bg-white p-8 rounded-2xl shadow-xl w-full max-w-sm mx-4 card-light">
        <div class="flex justify-between items-center mb-6">
            <h2 class="text-2xl font-bold text-gray-900">Account Details</h2>
            <button id="close-account-modal-btn" class="text-gray-500 hover:text-gray-700">
                <i data-feather="x" class="w-6 h-6"></i>
            </button>
        </div>
        <div class="space-y-4">
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Account Number</label>
                <p id="acc-details-account" class="p-3 bg-gray-100 rounded-lg text-gray-800 font-semibold"></p>
            </div>
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Units Consumed</label>
                <p id="acc-details-units" class="p-3 bg-gray-100 rounded-lg text-gray-800 font-semibold"></p>
            </div>
        </div>
        <div class="flex justify-end mt-6">
            <button id="close-account-modal-btn-bottom" class="bg-gray-200 text-gray-800 font-semibold px-6 py-3 rounded-lg hover:bg-gray-300 transition-colors duration-200">
                Close
            </button>
        </div>
    </div>
</div>

<script>
    feather.replace();

    // Renaming variables for clarity
    const openAddModalBtn = document.getElementById('open-add-modal-btn');
    const closeAddModalBtn = document.getElementById('close-add-modal-btn');
    const cancelAddModalBtn = document.getElementById('cancel-add-modal-btn');
    const addModal = document.getElementById('add-customer-modal');

    const viewCustomerBtns = document.querySelectorAll('.view-customer-btn');
    const viewModal = document.getElementById('view-customer-modal');
    const closeViewModalBtn = document.getElementById('close-view-modal-btn');
    const closeViewModalBtnBottom = document.getElementById('close-view-modal-btn-bottom');
    const viewCustomerId = document.getElementById('view-customer-id');
    const viewName = document.getElementById('view-name');
    const viewAddress = document.getElementById('view-address');
    const viewMobile = document.getElementById('view-mobile');
    const viewAccount = document.getElementById('view-account');
    const viewUnits = document.getElementById('view-units');

    // New variables for Account Details modal
    const accountDetailsBtns = document.querySelectorAll('.account-details-btn');
    const accountDetailsModal = document.getElementById('account-details-modal');
    const closeAccountModalBtn = document.getElementById('close-account-modal-btn');
    const closeAccountModalBtnBottom = document.getElementById('close-account-modal-btn-bottom');
    const accDetailsAccount = document.getElementById('acc-details-account');
    const accDetailsUnits = document.getElementById('acc-details-units');

    // Add Modal functionality
    openAddModalBtn.addEventListener('click', () => {
        addModal.classList.remove('hidden');
        addModal.classList.add('flex');
    });
    closeAddModalBtn.addEventListener('click', () => {
        addModal.classList.remove('flex');
        addModal.classList.add('hidden');
    });
    cancelAddModalBtn.addEventListener('click', () => {
        addModal.classList.remove('flex');
        addModal.classList.add('hidden');
    });
    window.addEventListener('click', (event) => {
        if (event.target === addModal) {
            addModal.classList.remove('flex');
            addModal.classList.add('hidden');
        }
    });

    // View Modal functionality
    viewCustomerBtns.forEach(button => {
        button.addEventListener('click', (event) => {
            const row = event.target.closest('tr');
            if (row) {
                const cells = row.querySelectorAll('td');
                // Populate the modal with data from the row
                viewCustomerId.textContent = cells[0].textContent;
                viewName.textContent = cells[1].textContent;
                viewAddress.textContent = cells[2].textContent;
                viewMobile.textContent = cells[3].textContent;
                viewAccount.textContent = cells[4].textContent;
                viewUnits.textContent = cells[5].textContent;

                viewModal.classList.remove('hidden');
                viewModal.classList.add('flex');
            }
        });
    });

    closeViewModalBtn.addEventListener('click', () => {
        viewModal.classList.remove('flex');
        viewModal.classList.add('hidden');
    });
    closeViewModalBtnBottom.addEventListener('click', () => {
        viewModal.classList.remove('flex');
        viewModal.classList.add('hidden');
    });
    window.addEventListener('click', (event) => {
        if (event.target === viewModal) {
            viewModal.classList.remove('flex');
            viewModal.classList.add('hidden');
        }
    });

    // Account Details Modal functionality
    accountDetailsBtns.forEach(button => {
        button.addEventListener('click', (event) => {
            const row = event.target.closest('tr');
            if (row) {
                const cells = row.querySelectorAll('td');
                // Populate the modal with Account and Units data
                accDetailsAccount.textContent = cells[4].textContent;
                accDetailsUnits.textContent = cells[5].textContent;

                accountDetailsModal.classList.remove('hidden');
                accountDetailsModal.classList.add('flex');
            }
        });
    });

    closeAccountModalBtn.addEventListener('click', () => {
        accountDetailsModal.classList.remove('flex');
        accountDetailsModal.classList.add('hidden');
    });
    closeAccountModalBtnBottom.addEventListener('click', () => {
        accountDetailsModal.classList.remove('flex');
        accountDetailsModal.classList.add('hidden');
    });
    window.addEventListener('click', (event) => {
        if (event.target === accountDetailsModal) {
            accountDetailsModal.classList.remove('flex');
            accountDetailsModal.classList.add('hidden');
        }
    });
</script>
</body>
</html>