<%--
  Created by IntelliJ IDEA.
  User: Niwanthi
  Date: 8/4/2025
  Time: 11:21 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ include file="sidebar.jsp" %>

<%
    String userRole = (String) session.getAttribute("role");
%>
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
<body class="font-sans text-gray-900" data-context-path="<%= request.getContextPath() %>">

<input type="hidden" id="loggedInUserRole" value="<%= userRole %>">

<div class="ml-64 p-8">
    <div class="flex justify-between items-center mb-10">
        <h1 class="text-4xl font-extrabold text-gray-900 flex items-center gap-4">
            <i data-feather="users" class="w-8 h-8 text-blue-500"></i> Customer Management
        </h1>
    </div>

    <div class="flex flex-wrap items-center justify-between gap-4 mb-8">
        <div class="flex flex-wrap items-center gap-4">
            <input type="text" id="searchInput" placeholder="Search by name..."
                   class="form-input-modern w-full md:w-80"/>
            <button id="refreshBtn"
                    class="bg-gray-200 text-gray-800 font-semibold px-6 py-2 rounded-lg hover:bg-gray-300 transition-colors duration-200">
                Refresh
            </button>
        </div>
        <c:if test="${sessionScope.role == 'ADMIN'}">
            <button id="openAddCustomerModalBtn" class="btn-success bg-green-600 text-white font-semibold px-6 py-3 rounded-lg hover:bg-green-700 transition-colors duration-200 shadow-md flex items-center">
                <i data-feather="plus" class="w-4 h-4 inline-block mr-2"></i>
                Add Customer
            </button>
        </c:if>
    </div>

    <div class="card-light p-8 rounded-2xl">
        <h2 class="text-2xl font-bold text-gray-900 mb-6">Customer List</h2>

        <div id="messageDisplay" class="hidden p-4 mb-4 text-sm rounded-lg" role="alert">
            <span id="messageText"></span>
        </div>

        <div id="loadingIndicator" class="text-center py-8 hidden">
            <div class="animate-spin rounded-full h-10 w-10 border-b-2 border-purple-600 mx-auto"></div>
            <p class="text-gray-600 mt-3">Loading Customers...</p>
        </div>

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
                    <tbody id="customerTableBody">
                </tbody>
            </table>
        </div>
    </div>
</div>

<div id="customerModal" class="fixed inset-0 hidden modal-overlay items-center justify-center z-50">
    <div class="modal-content bg-white p-8 rounded-2xl shadow-xl w-full max-w-2xl mx-4 card-light">
        <div class="flex justify-between items-center mb-6">
            <h2 id="modalTitle" class="text-2xl font-bold text-gray-900">Add New Customer</h2>
            <button id="closeModalBtn" class="text-gray-500 hover:text-gray-700">
                <i data-feather="x" class="w-6 h-6"></i>
            </button>
        </div>
        <form id="customerForm" class="space-y-6">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                <input type="hidden" id="customerId" name="cus_Id">
                <div>
                    <label for="customerName" class="block text-sm font-medium text-gray-700 mb-1">Customer Name</label>
                    <input type="text" id="customerName" name="cus_Name" placeholder="Enter Name" class="form-input-modern w-full" />
                </div>
                <div>
                    <label for="address" class="block text-sm font-medium text-gray-700 mb-1">Address</label>
                    <input type="text" id="address" name="cus_Address" placeholder="Enter Address" class="form-input-modern w-full" />
                </div>
                <div>
                    <label for="mobile" class="block text-sm font-medium text-gray-700 mb-1">Mobile Number</label>
                    <input type="text" id="mobile"  name="cus_Mobile" placeholder="Enter Mobile Number" class="form-input-modern w-full" />
                </div>
                <div>
                    <label for="accountNumber" class="block text-sm font-medium text-gray-700 mb-1">Account Number</label>
                    <input type="text" id="accountNumber" name="cus_AccountNumber" placeholder="Enter Account Number" class="form-input-modern w-full" />
                </div>
                <div>
                    <label for="unitsConsumed" class="block text-sm font-medium text-gray-700 mb-1">Units Consumed</label>
                    <input type="number" id="unitsConsumed" name="units_consumed" placeholder="Enter Units Consumed" required step="0.01" min="0" class="form-input-modern w-full" />
                </div>
            </div>
            <div class="flex justify-end gap-4 mt-6">
                <button type="button" id="cancel-add-modal-btn" class="bg-gray-200 text-gray-800 font-semibold px-6 py-3 rounded-lg hover:bg-gray-300 transition-colors duration-200">
                    Cancel
                </button>
                <button type="submit" class="btn-success bg-green-600 text-white font-semibold px-6 py-3 rounded-lg transition-colors duration-200">
                    Save
                </button>
            </div>
        </form>
    </div>
</div>

<div id="viewCustomerModal" class="fixed inset-0 hidden modal-overlay items-center justify-center z-50">
    <div class="modal-content bg-white p-8 rounded-2xl shadow-xl w-full max-w-md mx-4 card-light">
        <div class="flex justify-between items-center mb-6">
            <h2 class="text-2xl font-bold text-gray-900">Customer Details</h2>
            <button id="close-view-modal-btn" class="text-gray-500 hover:text-gray-700">
                <i data-feather="x" class="w-6 h-6"></i>
            </button>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6 space-y-4">
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
            <button id="cancel-view-modal-btn-bottom" class="bg-gray-200 text-gray-800 font-semibold px-6 py-3 rounded-lg hover:bg-gray-300 transition-colors duration-200">
                Cancel
            </button>
        </div>
    </div>
</div>

<div id="accountDetailsModal" class="fixed inset-0 hidden modal-overlay items-center justify-center z-50">
    <div class="modal-content bg-white p-8 rounded-2xl shadow-xl w-full max-w-sm mx-4 card-light">
        <div class="flex justify-between items-center mb-6">
            <h2 class="text-2xl font-bold text-gray-900">Account Details</h2>
            <button id="close-account-modal-btn" class="text-gray-500 hover:text-gray-700">
                <i data-feather="x" class="w-6 h-6"></i>
            </button>
        </div>
        <form id="accountDetailsForm">
            <div class="space-y-4">
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">Account Number</label>
                    <p id="accDetailsAccount" class="p-3 bg-gray-100 rounded-lg text-gray-800 font-semibold"></p>
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">Units Consumed</label>
                    <p id="accDetailsUnits" class="p-3 bg-gray-100 rounded-lg text-gray-800 font-semibold"></p>
                </div>
            </div>
            <div class="flex justify-end mt-6">
                <button id="close-account-modal-btn-bottom" class="bg-gray-200 text-gray-800 font-semibold px-6 py-3 rounded-lg hover:bg-gray-300 transition-colors duration-200">
                    Close
                </button>
            </div>
        </form>
    </div>
</div>

<script>

    // --- 1. GLOBAL VARIABLE DECLARATIONS ---
    let customerTableBody, loadingIndicator, messageDisplay, messageText, searchInput, refreshBtn;
    let customerModal,viewCustomerModal,accountDetailsModal,customerForm,accountDetailsForm;
    let searchTimeout;
    let contextPath;


    function getContextPath() {
        const path = window.location.pathname;
        const secondSlashIndex = path.indexOf("/", 1);
        if (secondSlashIndex !== -1) {
            return path.substring(0, secondSlashIndex);
        }
        return "";
    }

    function showMessage(message, type = 'success') {
        messageText.textContent = message;
        messageDisplay.className = 'p-4 mb-4 text-sm rounded-lg'; // Reset classes
        messageDisplay.classList.add(type === 'success' ? 'bg-green-100' : 'bg-red-100', type === 'success' ? 'text-green-800' : 'text-red-800');
        setTimeout(() => {
            messageDisplay.classList.add('hidden');
        }, 5000);
    }

    function showLoading(show) {
        loadingIndicator.classList.toggle('hidden', !show);
        customerTableBody.classList.toggle('hidden', show);
    }

    function openModal(modal) {
        modal.classList.remove('hidden');
        modal.classList.add('flex');
    }

    function closeModal(modal) {
        modal.classList.remove('flex');
        modal.classList.add('hidden');
    }


    // --- API Call Functions ---
    async function fetchCustomer(searchTerm = '') {
        let url = getContextPath() + '/customers';
        if (searchTerm) {
            url += '?search=' + encodeURIComponent(searchTerm);
        }
        try {
            const response = await fetch(url);
            const data = await response.json();
            if (!response.ok) throw new Error(data.message || 'Failed to fetch customers.');
            renderCustomers(data);
        } catch (error) {
            showMessage(error.message, 'error');
            customerTableBody.innerHTML = `<tr><td colspan="6" class="text-center py-8 text-red-500">${error.message}</td></tr>`;
        } finally {
            showLoading(false);
        }
    }

    // --- Rendering Function ---
    function renderCustomers(customers) {
        console.log(customers);

        customerTableBody.innerHTML = '';
        if (!customers || customers.length === 0) {
            customerTableBody.innerHTML = '<tr><td colspan="6" class="text-center py-8 text-gray-500">Customer not found.</td></tr>';
            return;
        }

        const loggedInUserRole = document.getElementById('loggedInUserRole').value;

        customers.forEach(customer => {
            const row = document.createElement('tr');
            let rowHtml = '';
            rowHtml += '<td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">' + customer.cus_Id + '</td>';
            rowHtml += '<td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">' + customer.cus_Name + '</td>';
            rowHtml += '<td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">' + customer.cus_Address + '</td>';
            rowHtml += '<td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">' + customer.cus_Mobile + '</td>';
            rowHtml += '<td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">' + customer.cus_AccountNumber + '</td>';
            rowHtml += '<td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">' + parseFloat(customer.units_consumed).toFixed(2) + '</td>';

            rowHtml += '<td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-center">';

            rowHtml += '<button title="View Item Details" class="text-blue-600 hover:text-blue-900 mr-3 view-btn" data-customer-id="' + customer.cus_Id + '">' +
                '<i data-feather="eye" class="w-4 h-4 inline-block align-middle"></i></button>';

            let canEdit = false;
            if (loggedInUserRole === 'ADMIN') {
                canEdit = true;
            }

            if (canEdit) {
                rowHtml += '<button title="Edit Customer" class="text-gray-600 hover:text-gray-900 mr-3 edit-btn" data-customer-id="' + customer.cus_Id + '">' +
                    '<i data-feather="edit" class="w-4 h-4 inline-block align-middle"></i></button>';
            }

            let canDelete = false;
            if (loggedInUserRole === 'ADMIN') {
                canDelete = true;
            }

            if (canDelete) {
                rowHtml += '<button title="Delete Customer" class="text-red-600 hover:text-red-900 delete-btn" data-customer-id="' + customer.cus_Id + '">' +
                    '<i data-feather="trash-2" class="w-4 h-4 inline-block align-middle"></i></button>';
            }

            rowHtml += '</td>';
            row.innerHTML = rowHtml;
            customerTableBody.appendChild(row);
        });

        feather.replace();

    }

    // --- Event Handler Functions ---
    function handleOpenAddModal() {
        customerForm.reset();
        document.getElementById('customerId').value = '';
        document.getElementById('modalTitle').textContent = 'Add New Customer';
        openModal(customerModal);
    }

    async function handleOpenViewModal(id) {
        try {
            let url = getContextPath() + '/customers?id=' + encodeURIComponent(id);
            const response = await fetch(url);
            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.message || 'Failed to fetch customer details.');
            }

            const customer = data.customer;

            document.getElementById('view-customer-id').textContent = customer.cus_Id;
            document.getElementById('view-name').textContent = customer.cus_Name;
            document.getElementById('view-address').textContent = customer.cus_Address;
            document.getElementById('view-mobile').textContent = customer.cus_Mobile;
            document.getElementById('view-account').textContent = customer.cus_AccountNumber;
            document.getElementById('view-units').textContent = customer.units_consumed;
            openModal(viewCustomerModal);

        } catch (error) {
            showMessage(error.message, 'error');
        }
    }

    async function handleOpenEditModal(id) {
        try {
            let url = getContextPath() + '/customers?id=' + encodeURIComponent(id);
            const response = await fetch(url);
            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.message || 'Failed to fetch customer details.');
            }

            const customer = data.customer;

            document.getElementById('modalTitle').textContent = 'Edit customer';
            document.getElementById('customerId').value = customer.cus_Id;
            document.getElementById('customerName').value = customer.cus_Name;
            document.getElementById('address').value = customer.cus_Address;
            document.getElementById('mobile').value = customer.cus_Mobile;
            document.getElementById('accountNumber').value = customer.cus_AccountNumber;
            document.getElementById('unitsConsumed').value = customer.units_consumed;
            openModal(customerModal);

        } catch (error) {
            showMessage(error.message, 'error');
        }
    }

    async function handleCustomerFormSubmit(e) {
        e.preventDefault();
        const id = document.getElementById('customerId').value;
        const isEdit = !!id;
        const formData = new URLSearchParams(new FormData(customerForm));

        if (isEdit) {
            formData.append('action', 'update');
        }

        try {

            const url = getContextPath() + '/customers';

            const response = await fetch(url, {
                method: isEdit ? 'PUT' : 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                body: formData.toString()
            });
            const data = await response.json();
            if (!response.ok) throw new Error(data.message);
            showMessage(data.message, 'success');
            closeModal(customerModal);
            fetchCustomer(searchInput.value);
        } catch (error) {
            showMessage(error.message, 'error');
        }
    }

    async function handleDeleteCustomer(id) {
        if (!confirm('Are you sure you want to delete this customer?')) return;
        try {
            let url = getContextPath() + '/customers?id=' + encodeURIComponent(id);
            const response = await fetch(url,{method: 'DELETE'});
            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.message || 'Failed to delete customer.');
            }

            showMessage(data.message, 'success');
            fetchCustomer(searchInput.value);
        } catch (error) {
            showMessage(error.message, 'error');
        }
    }

    function handleSearchInput() {
        clearTimeout(searchTimeout);
        searchTimeout = setTimeout(() => fetchCustomer(searchInput.value), 300);
    }

    // --- 3. MAIN INITIALIZATION FUNCTION ---
    function initCustomerPage() {
        customerTableBody = document.getElementById('customerTableBody');
        loadingIndicator = document.getElementById('loadingIndicator');
        messageDisplay = document.getElementById('messageDisplay');
        messageText = document.getElementById('messageText');
        searchInput = document.getElementById('searchInput');
        refreshBtn = document.getElementById('refreshBtn');
        customerModal = document.getElementById('customerModal');
        viewCustomerModal = document.getElementById('viewCustomerModal');
        accountDetailsModal = document.getElementById('accountDetailsModal');
        customerForm = document.getElementById('customerForm');
        accountDetailsForm = document.getElementById('accountDetailsForm');
        const openAddCustomerModalBtn = document.getElementById('openAddCustomerModalBtn');
        const allModals = [customerModal, viewCustomerModal, accountDetailsModal];

        if (openAddCustomerModalBtn) {
            openAddCustomerModalBtn.addEventListener('click', handleOpenAddModal);
        }

        refreshBtn.addEventListener('click', () => fetchCustomer(searchInput.value));
        customerForm.addEventListener('submit', handleCustomerFormSubmit);
        searchInput.addEventListener('input', handleSearchInput);

        // Close/Cancel buttons for the customerModal (add/edit)
        document.getElementById('closeModalBtn').addEventListener('click', () => closeModal(customerModal));
        document.getElementById('cancel-add-modal-btn').addEventListener('click', () => closeModal(customerModal));

        // Close buttons for the viewCustomerModal
        document.getElementById('close-view-modal-btn').addEventListener('click', () => closeModal(viewCustomerModal));
        document.getElementById('cancel-view-modal-btn-bottom').addEventListener('click', () => closeModal(viewCustomerModal));

        // Close buttons for the accountDetailsModal
        document.getElementById('close-account-modal-btn').addEventListener('click', () => closeModal(accountDetailsModal));
        document.getElementById('close-account-modal-btn-bottom').addEventListener('click', () => closeModal(accountDetailsModal));

        // Use event delegation for buttons inside the dynamic table and modals
        // Corrected event delegation listener in initCustomerPage
        document.body.addEventListener('click', (e) => {
            const viewBtn = e.target.closest('.view-btn');
            const editBtn = e.target.closest('.edit-btn');
            const deleteBtn = e.target.closest('.delete-btn');
            const closeModalBtn = e.target.closest('.close-modal-btn');

            if (closeModalBtn) closeModal(closeModalBtn.closest('.modal-overlay'));
            if (viewBtn) handleOpenViewModal(viewBtn.dataset.customerId);
            if (editBtn) handleOpenEditModal(editBtn.dataset.customerId);
            if (deleteBtn) handleDeleteCustomer(deleteBtn.dataset.customerId);
        });

        allModals.forEach(modal => {
            if (modal) {
                modal.addEventListener('click', e => { if (e.target === modal) closeModal(modal); });
            }
        });

        // --- Initial Data Load ---
        fetchCustomer();
        feather.replace();
    }

    // --- 4. ENTRY POINT ---
    document.addEventListener('DOMContentLoaded', initCustomerPage);

</script>
</body>
</html>