<%--
  Created by IntelliJ IDEA.
  User: Niwanthi
  Date: 8/9/2025
  Time: 6:32 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="sidebar.jsp" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%
    String role = (String) session.getAttribute("role");
    Integer userId = (Integer) session.getAttribute("userId");
%>

<html>
<head>
    <title>Item Management</title>
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
        .loading-spinner {
            border: 4px solid #f3f3f3;
            border-top: 4px solid #3b82f6;
            border-radius: 50%;
            width: 40px;
            height: 40px;
            animation: spin 1s linear infinite;
        }
        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }
    </style>
</head>
<body class="font-sans text-gray-900">

<input type="hidden" id="userRoleHiddenInput" value="<%= role %>">
<input type="hidden" id="userIdHiddenInput" value="<%= userId %>">
<input type="hidden" id="contextPath" value="<%= request.getContextPath() %>">

<div class="ml-64 p-8">
    <div class="flex justify-between items-center mb-10">
        <h1 class="text-4xl font-extrabold text-gray-900 flex items-center gap-4">
            <i data-feather="box" class="w-8 h-8 text-blue-500"></i> Item Management
        </h1>
        <c:if test='<%= "ADMIN".equals(role) %>'>
            <button id="open-add-modal-btn" class="bg-green-600 text-white font-semibold px-6 py-3 rounded-lg hover:bg-green-700 transition-colors duration-200 shadow-md flex items-center">
                <i data-feather="plus" class="w-4 h-4 inline-block mr-2"></i>
                Add Item
            </button>
        </c:if>
    </div>

    <div class="flex flex-wrap items-center justify-between gap-4 mb-8">
        <div class="relative w-full md:w-80">
            <input type="text" id="search-input" placeholder="Search by name or ID"
                   class="form-input-modern w-full pl-10" />
            <i data-feather="search" class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 w-5 h-5"></i>
        </div>
        <button id="refresh-btn" class="bg-gray-200 text-gray-800 font-semibold px-6 py-2 rounded-lg hover:bg-gray-300 transition-colors duration-200 flex items-center gap-2">
            <i data-feather="refresh-cw" class="w-4 h-4"></i>
            Refresh
        </button>
    </div>

    <div id="message-container" class="mb-4 hidden p-4 rounded-lg text-sm transition-opacity duration-300">
        <p id="message-text"></p>
    </div>

    <div class="card-light p-8 rounded-2xl">
        <h2 class="text-2xl font-bold text-gray-900 mb-6">Item List</h2>
        <div id="loading-indicator" class="flex flex-col items-center justify-center p-10 hidden">
            <div class="loading-spinner"></div>
            <p class="mt-4 text-gray-500">Loading items...</p>
        </div>
        <div id="item-table-container" class="overflow-x-auto">
            <table class="min-w-full table-auto">
                <thead class="bg-gray-100 text-gray-700 font-bold">
                <tr>
                    <th class="px-6 py-3 text-left rounded-tl-xl">ID</th>
                    <th class="px-6 py-3 text-left">Item Name</th>
                    <th class="px-6 py-3 text-left">Unit Price</th>
                    <th class="px-6 py-3 text-left">Stock</th>
                    <th class="px-6 py-3 text-left">Created At</th>
                    <th class="px-6 py-3 text-center rounded-tr-xl">Actions</th>
                </tr>
                </thead>
                <tbody id="item-table-body">
                <tr>
                    <td colspan="6" class="text-center py-8 text-gray-500">No items found.</td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>

<div id="add-edit-item-modal" class="fixed inset-0 hidden modal-overlay items-center justify-center z-50">
    <div class="bg-white p-8 rounded-2xl shadow-xl w-full max-w-lg mx-4 card-light">
        <div class="flex justify-between items-center mb-6">
            <h2 id="modal-title" class="text-2xl font-bold text-gray-900">Add New Item</h2>
            <button id="close-add-edit-modal-btn" class="text-gray-500 hover:text-gray-700">
                <i data-feather="x" class="w-6 h-6"></i>
            </button>
        </div>
        <form id="add-edit-item-form" class="space-y-6">
            <input type="hidden" id="item-id-input" name="id">

            <div>
                <label for="item-name-input" class="block text-sm font-medium text-gray-700 mb-1">Item Name</label>
                <input type="text" id="item-name-input" name="itemName" placeholder="Enter Item Name" class="form-input-modern w-full" required />
                <p id="item-name-error" class="text-red-500 text-xs mt-1 hidden">Item name is required.</p>
            </div>
            <div>
                <label for="unit-price-input" class="block text-sm font-medium text-gray-700 mb-1">Unit Price</label>
                <input type="number" id="unit-price-input" name="unitPrice" placeholder="Enter Unit Price" class="form-input-modern w-full" step="0.01" min="0" required />
                <p id="unit-price-error" class="text-red-500 text-xs mt-1 hidden">Valid unit price is required.</p>
            </div>
            <div>
                <label for="quantity-input" class="block text-sm font-medium text-gray-700 mb-1">Stock</label>
                <input type="number" id="quantity-input" name="quantity" placeholder="Enter Stock Quantity" class="form-input-modern w-full" min="0" required />
                <p id="quantity-error" class="text-red-500 text-xs mt-1 hidden">Valid stock quantity is required.</p>
            </div>
            <div class="flex justify-end gap-4 mt-6">
                <button type="button" id="cancel-add-edit-modal-btn" class="bg-gray-200 text-gray-800 font-semibold px-6 py-3 rounded-lg hover:bg-gray-300 transition-colors duration-200">
                    Cancel
                </button>
                <button type="submit" class="bg-green-600 text-white font-semibold px-6 py-3 rounded-lg hover:bg-green-700 transition-colors duration-200">
                    Save
                </button>
            </div>
        </form>
    </div>
</div>

<div id="view-item-modal" class="fixed inset-0 hidden modal-overlay items-center justify-center z-50">
    <div class="bg-white p-8 rounded-2xl shadow-xl w-full max-w-md mx-4 card-light">
        <div class="flex justify-between items-center mb-6">
            <h2 class="text-2xl font-bold text-gray-900">Item Details</h2>
            <button id="close-view-modal-btn" class="text-gray-500 hover:text-gray-700">
                <i data-feather="x" class="w-6 h-6"></i>
            </button>
        </div>
        <div class="space-y-4">
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Item ID</label>
                <p id="view-item-id" class="p-3 bg-gray-100 rounded-lg text-gray-800 font-semibold"></p>
            </div>
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Item Name</label>
                <p id="view-item-name" class="p-3 bg-gray-100 rounded-lg text-gray-800 font-semibold"></p>
            </div>
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Unit Price</label>
                <p id="view-unit-price" class="p-3 bg-gray-100 rounded-lg text-gray-800 font-semibold"></p>
            </div>
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Stock</label>
                <p id="view-stock" class="p-3 bg-gray-100 rounded-lg text-gray-800 font-semibold"></p>
            </div>
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Created At</label>
                <p id="view-created-at" class="p-3 bg-gray-100 rounded-lg text-gray-800 font-semibold"></p>
            </div>
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Updated At</label>
                <p id="view-updated-at" class="p-3 bg-gray-100 rounded-lg text-gray-800 font-semibold"></p>
            </div>
        </div>
        <div class="flex justify-end mt-6">
            <button id="close-view-modal-btn-bottom" class="bg-gray-200 text-gray-800 font-semibold px-6 py-3 rounded-lg hover:bg-gray-300 transition-colors duration-200">
                Close
            </button>
        </div>
    </div>
</div>

<div id="restock-item-modal" class="fixed inset-0 hidden modal-overlay items-center justify-center z-50">
    <div class="bg-white p-8 rounded-2xl shadow-xl w-full max-w-md mx-4 card-light">
        <div class="flex justify-between items-center mb-6">
            <h2 class="text-2xl font-bold text-gray-900">Restock Item</h2>
            <button id="close-restock-modal-btn" class="text-gray-500 hover:text-gray-700">
                <i data-feather="x" class="w-6 h-6"></i>
            </button>
        </div>
        <form id="restock-item-form" class="space-y-6">
            <input type="hidden" id="restock-item-id-input" name="id">
            <input type="hidden" name="action" value="restock">

            <div>
                <label for="restock-item-name-display" class="block text-sm font-medium text-gray-700 mb-1">Item Name</label>
                <input type="text" id="restock-item-name-display" class="form-input-modern w-full" readonly />
            </div>
            <div>
                <label for="quantity-to-add-input" class="block text-sm font-medium text-gray-700 mb-1">Quantity to Add</label>
                <input type="number" id="quantity-to-add-input" name="quantityToAdd" placeholder="Enter quantity to add" class="form-input-modern w-full" min="1" required />
            </div>
            <div class="flex justify-end gap-4 mt-6">
                <button type="button" id="cancel-restock-modal-btn" class="bg-gray-200 text-gray-800 font-semibold px-6 py-3 rounded-lg hover:bg-gray-300 transition-colors duration-200">
                    Cancel
                </button>
                <button type="submit" class="bg-blue-600 text-white font-semibold px-6 py-3 rounded-lg hover:bg-blue-700 transition-colors duration-200">
                    Restock
                </button>
            </div>
        </form>
    </div>
</div>

<script>
    feather.replace();

    // Context path for AJAX calls
    const contextPath = document.getElementById('contextPath').value;

    // Global variables for DOM elements
    const openAddModalBtn = document.getElementById('open-add-modal-btn');
    const refreshBtn = document.getElementById('refresh-btn');
    const searchInput = document.getElementById('search-input');
    const loadingIndicator = document.getElementById('loading-indicator');
    const itemTableBody = document.getElementById('item-table-body');
    const messageContainer = document.getElementById('message-container');
    const messageText = document.getElementById('message-text');

    // Add/Edit Modal
    const addEditModal = document.getElementById('add-edit-item-modal');
    const addEditForm = document.getElementById('add-edit-item-form');
    const modalTitle = document.getElementById('modal-title');
    const itemIdInput = document.getElementById('item-id-input');
    const itemNameInput = document.getElementById('item-name-input');
    const unitPriceInput = document.getElementById('unit-price-input');
    const quantityInput = document.getElementById('quantity-input');
    const closeAddEditBtn = document.getElementById('close-add-edit-modal-btn');
    const cancelAddEditBtn = document.getElementById('cancel-add-edit-modal-btn');
    const itemNameError = document.getElementById('item-name-error');
    const unitPriceError = document.getElementById('unit-price-error');
    const quantityError = document.getElementById('quantity-error');

    // View Modal
    const viewModal = document.getElementById('view-item-modal');
    const closeViewModalBtn = document.getElementById('close-view-modal-btn');
    const closeViewModalBtnBottom = document.getElementById('close-view-modal-btn-bottom');
    const viewItemId = document.getElementById('view-item-id');
    const viewItemName = document.getElementById('view-item-name');
    const viewUnitPrice = document.getElementById('view-unit-price');
    const viewStock = document.getElementById('view-stock');
    const viewCreatedAt = document.getElementById('view-created-at');
    const viewUpdatedAt = document.getElementById('view-updated-at');

    // Restock Modal
    const restockModal = document.getElementById('restock-item-modal');
    const restockForm = document.getElementById('restock-item-form');
    const restockItemIdInput = document.getElementById('restock-item-id-input');
    const restockItemNameDisplay = document.getElementById('restock-item-name-display');
    const quantityToAddInput = document.getElementById('quantity-to-add-input');
    const closeRestockModalBtn = document.getElementById('close-restock-modal-btn');
    const cancelRestockModalBtn = document.getElementById('cancel-restock-modal-btn');

    // User roles and ID from JSTL (set at the top of the file)
    const userRole = document.getElementById('userRoleHiddenInput').value;
    const userId = parseInt(document.getElementById('userIdHiddenInput').value); // Parse to integer
    const initialAdminId = 1;

    // Helper function to show notifications
    function showMessage(message, type = 'success') {
        messageText.textContent = message;
        messageContainer.classList.remove('hidden', 'bg-green-100', 'text-green-800', 'bg-red-100', 'text-red-800');
        if (type === 'success') {
            messageContainer.classList.add('bg-green-100', 'text-green-800');
        } else {
            messageContainer.classList.add('bg-red-100', 'text-red-800');
        }
        setTimeout(() => {
            messageContainer.classList.add('hidden');
        }, 5000);
    }

    // Helper function to show/hide modals
    function toggleModal(modal, show = true) {
        modal.classList.toggle('hidden', !show);
        modal.classList.toggle('flex', show);
    }

    // Clear validation errors
    function clearValidationErrors() {
        itemNameError.classList.add('hidden');
        unitPriceError.classList.add('hidden');
        quantityError.classList.add('hidden');
    }

    // Validate form for Add/Edit
    function validateForm() {
        clearValidationErrors();
        let isValid = true;
        if (itemNameInput.value.trim() === '') {
            itemNameError.textContent = 'Item name is required.';
            itemNameError.classList.remove('hidden');
            isValid = false;
        }
        if (parseFloat(unitPriceInput.value) < 0 || isNaN(parseFloat(unitPriceInput.value))) {
            unitPriceError.textContent = 'Unit price must be a non-negative number.';
            unitPriceError.classList.remove('hidden');
            isValid = false;
        }
        if (parseInt(quantityInput.value) < 0 || isNaN(parseInt(quantityInput.value))) {
            quantityError.textContent = 'Stock quantity must be a non-negative integer.';
            quantityError.classList.remove('hidden');
            isValid = false;
        }
        return isValid;
    }

    // Fetch items from the backend
    async function fetchItems(searchTerm = '') {
        loadingIndicator.classList.remove('hidden');
        itemTableBody.innerHTML = '';
        messageContainer.classList.add('hidden');

        let url = `${contextPath}/items`;
        if (searchTerm) {
            url += `?search=${encodeURIComponent(searchTerm)}`;
        }

        try {
            const response = await fetch(url);
            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.message || 'Failed to fetch items.');
            }

            renderItems(data);
        } catch (error) {
            console.error('Error fetching items:', error);
            showMessage(error.message || 'Failed to load items. Please try again.', 'error');
            itemTableBody.innerHTML = `<tr><td colspan="6" class="text-center py-8 text-red-500">${error.message || 'Error loading items.'}</td></tr>`;
        } finally {
            loadingIndicator.classList.add('hidden');
            feather.replace();
        }
    }

    // Render items to the table (using the corrected concatenation method)
    function renderItems(items) {
        itemTableBody.innerHTML = ''; // Clear existing rows

        if (items.length === 0) {
            itemTableBody.innerHTML = '<tr><td colspan="6" class="text-center py-8 text-gray-500">No items found.</td></tr>';
            return;
        }

        items.forEach(item => {
            const row = document.createElement('tr');
            row.className = 'border-b border-gray-200 table-row-hover cursor-pointer';

            let rowHtml = '';
            rowHtml += '<td class="px-6 py-4 text-gray-800 font-medium">' + item.id + '</td>';
            rowHtml += '<td class="px-6 py-4 text-gray-600">' + item.itemName + '</td>';
            rowHtml += '<td class="px-6 py-4 text-gray-600">Rs. ' + parseFloat(item.unitPrice).toFixed(2) + '</td>';
            rowHtml += '<td class="px-6 py-4 text-gray-600">';
            rowHtml += '    <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full ' + (item.quantity < 10 ? 'bg-red-100 text-red-800' : 'bg-green-100 text-green-800') + '">';
            rowHtml += '        ' + item.quantity;
            rowHtml += '    </span>';
            rowHtml += '</td>';
            rowHtml += '<td class="px-6 py-4 text-gray-600">' + (item.createdAt ? new Date(item.createdAt).toLocaleString('en-GB') : '-') + '</td>';
            rowHtml += '<td class="px-6 py-4 text-gray-600">';
            rowHtml += '    <div class="flex items-center justify-center gap-2">';

            // View Button (always visible)
            rowHtml += '        <button class="view-item-btn text-gray-500 hover:text-gray-700 transition-colors duration-200" data-id="' + item.id + '">';
            rowHtml += '            <i data-feather="eye" class="w-5 h-5"></i>';
            rowHtml += '        </button>';

            // Conditionally render buttons based on user role and ID
            if (userRole === 'ADMIN') {
                // Edit Button
                rowHtml += '        <button class="edit-item-btn text-blue-500 hover:text-blue-700 transition-colors duration-200" data-id="' + item.id + '">';
                rowHtml += '            <i data-feather="edit" class="w-5 h-5"></i>';
                rowHtml += '        </button>';

                // Restock Button
                rowHtml += '        <button class="restock-item-btn text-yellow-500 hover:text-yellow-700 transition-colors duration-200" data-id="' + item.id + '">';
                rowHtml += '            <i data-feather="refresh-cw" class="w-5 h-5"></i>';
                rowHtml += '        </button>';

                // Delete Button (only for initial admin)
                if (userId === initialAdminId) {
                    rowHtml += '        <button class="delete-item-btn text-red-500 hover:text-red-700 transition-colors duration-200" data-id="' + item.id + '">';
                    rowHtml += '            <i data-feather="trash-2" class="w-5 h-5"></i>';
                    rowHtml += '        </button>';
                }
            }

            rowHtml += '    </div>';
            rowHtml += '</td>';

            row.innerHTML = rowHtml;
            itemTableBody.appendChild(row);
        });

        feather.replace();
    }

    // Handle form submission for Add/Edit
    async function handleAddEditFormSubmit(e) {
        e.preventDefault();
        if (!validateForm()) return;

        const isEdit = itemIdInput.value !== '';
        const method = isEdit ? 'PUT' : 'POST';
        const url = `${contextPath}/items`;

        // Prepare form data
        const formData = new URLSearchParams();
        if (isEdit) {
            formData.append('action', 'update'); // Action for update
            formData.append('id', itemIdInput.value);
        }
        formData.append('itemName', itemNameInput.value);
        formData.append('unitPrice', unitPriceInput.value);
        formData.append('quantity', quantityInput.value);

        try {
            const response = await fetch(url, {
                method: method,
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: formData.toString()
            });
            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.message || `Failed to ${isEdit ? 'update' : 'add'} item.`);
            }

            showMessage(data.message, 'success');
            toggleModal(addEditModal, false);
            fetchItems(searchInput.value);
        } catch (error) {
            console.error(`Error ${isEdit ? 'updating' : 'adding'} item:`, error);
            showMessage(error.message || `Failed to ${isEdit ? 'update' : 'add'} item.`, 'error');
        }
    }

    // Handle restock form submission
    async function handleRestockFormSubmit(e) {
        e.preventDefault();

        const itemId = restockItemIdInput.value;
        const quantityToAdd = quantityToAddInput.value;

        if (parseInt(quantityToAdd) <= 0 || isNaN(parseInt(quantityToAdd))) {
            showMessage('Quantity to add must be a positive number.', 'error');
            return;
        }

        const formData = new URLSearchParams();
        formData.append('action', 'restock');
        formData.append('id', itemId);
        formData.append('quantityToAdd', quantityToAdd);

        try {
            const response = await fetch(`${contextPath}/items`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: formData.toString()
            });
            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.message || 'Failed to restock item.');
            }

            showMessage(data.message, 'success');
            toggleModal(restockModal, false);
            fetchItems(searchInput.value);
        } catch (error) {
            console.error('Error restocking item:', error);
            showMessage(error.message || 'Failed to restock item. Please check your inputs.', 'error');
        }
    }

    // Handle Delete action
    async function deleteItem(itemId) {
        if (!confirm('Are you sure you want to delete this item? This action cannot be undone.')) {
            return;
        }

        try {
            const response = await fetch(`${contextPath}/items?id=${itemId}`, {
                method: 'DELETE'
            });
            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.message || 'Failed to delete item.');
            }

            showMessage(data.message, 'success');
            fetchItems(searchInput.value);
        } catch (error) {
            console.error('Error deleting item:', error);
            showMessage(error.message || 'Failed to delete item. Please try again.', 'error');
        }
    }

    // Event listeners
    document.addEventListener('DOMContentLoaded', () => {
        fetchItems();

        openAddModalBtn.addEventListener('click', () => {
            modalTitle.textContent = 'Add New Item';
            addEditForm.reset();
            itemIdInput.value = '';
            clearValidationErrors();
            toggleModal(addEditModal, true);
        });

        closeAddEditBtn.addEventListener('click', () => toggleModal(addEditModal, false));
        cancelAddEditBtn.addEventListener('click', () => toggleModal(addEditModal, false));
        addEditForm.addEventListener('submit', handleAddEditFormSubmit);

        closeViewModalBtn.addEventListener('click', () => toggleModal(viewModal, false));
        closeViewModalBtnBottom.addEventListener('click', () => toggleModal(viewModal, false));

        closeRestockModalBtn.addEventListener('click', () => toggleModal(restockModal, false));
        cancelRestockModalBtn.addEventListener('click', () => toggleModal(restockModal, false));
        restockForm.addEventListener('submit', handleRestockFormSubmit);

        refreshBtn.addEventListener('click', () => fetchItems(searchInput.value));

        let searchTimeout;
        searchInput.addEventListener('input', () => {
            clearTimeout(searchTimeout);
            searchTimeout = setTimeout(() => {
                fetchItems(searchInput.value);
            }, 300);
        });

        // Event delegation for dynamically created buttons
        itemTableBody.addEventListener('click', async (e) => {
            const target = e.target.closest('button');
            if (!target) return;

            const itemId = target.dataset.id;

            if (target.classList.contains('view-item-btn')) {
                try {
                    const response = await fetch(`${contextPath}/items?id=${itemId}`);
                    const item = await response.json();
                    if (!response.ok) throw new Error(item.message || 'Failed to fetch item details.');
                    viewItemId.textContent = item.id;
                    viewItemName.textContent = item.itemName;
                    viewUnitPrice.textContent = `Rs. ${parseFloat(item.unitPrice).toFixed(2)}`;
                    viewStock.textContent = item.quantity;
                    viewCreatedAt.textContent = item.createdAt ? new Date(item.createdAt).toLocaleString('en-GB') : '-';
                    viewUpdatedAt.textContent = item.updatedAt ? new Date(item.updatedAt).toLocaleString('en-GB') : '-';
                    toggleModal(viewModal, true);
                } catch (error) {
                    showMessage(error.message, 'error');
                }
            } else if (target.classList.contains('edit-item-btn')) {
                try {
                    const response = await fetch(`${contextPath}/items?id=${itemId}`);
                    const item = await response.json();
                    if (!response.ok) throw new Error(item.message || 'Failed to fetch item details.');
                    modalTitle.textContent = 'Edit Item';
                    itemIdInput.value = item.id;
                    itemNameInput.value = item.itemName;
                    unitPriceInput.value = item.unitPrice;
                    quantityInput.value = item.quantity;
                    clearValidationErrors();
                    toggleModal(addEditModal, true);
                } catch (error) {
                    showMessage(error.message, 'error');
                }
            } else if (target.classList.contains('restock-item-btn')) {
                try {
                    const response = await fetch(`${contextPath}/items?id=${itemId}`);
                    const item = await response.json();
                    if (!response.ok) throw new Error(item.message || 'Failed to fetch item details.');
                    restockItemIdInput.value = item.id;
                    restockItemNameDisplay.value = item.itemName;
                    quantityToAddInput.value = 1;
                    toggleModal(restockModal, true);
                } catch (error) {
                    showMessage(error.message, 'error');
                }
            } else if (target.classList.contains('delete-item-btn')) {
                deleteItem(itemId);
            }
        });
    });
</script>

</body>
</html>