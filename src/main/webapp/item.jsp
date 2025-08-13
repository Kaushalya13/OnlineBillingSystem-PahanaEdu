<%--
  Created by IntelliJ IDEA.
  User: Niwanthi
  Date: 8/9/2025
  Time: 6:32 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%
    if (session.getAttribute("userId") == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
%>

<%@ include file="sidebar.jsp" %>

<%
    String userRole = (String) session.getAttribute("role");
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
            background-color: rgba(0, 0, 0, 0.6);
            backdrop-filter: blur(4px);
        }

        .modal-content {
            transition: all 0.3s ease;
        }

        .btn-primary {
            background-color: #2563eb;
            color: white;
        }

        .btn-primary:hover {
            background-color: #1d4ed8;
        }

        .btn-secondary {
            background-color: #e5e7eb;
            color: #1f2937;
        }

        .btn-secondary:hover {
            background-color: #d1d5db;
        }

        .btn-success {
            background-color: #16a34a;
            color: white;
        }

        .btn-success:hover {
            background-color: #15803d;
        }

        .btn-danger {
            background-color: #dc2626;
            color: white;
        }

        .btn-danger:hover {
            background-color: #b91c1c;
        }
    </style>
</head>
<body class="font-sans text-gray-900" data-context-path="<%= request.getContextPath() %>">

<input type="hidden" id="loggedInUserRole" value="<%= userRole %>">
<input type="hidden" id="loggedInUserId" value="<%= userId %>">
<input type="hidden" id="initialAdminId" value="1">

<div class="ml-64 p-8">
    <div class="flex justify-between items-center mb-10">
        <h1 class="text-4xl font-extrabold text-gray-900 flex items-center gap-4">
            <i data-feather="box" class="w-8 h-8 text-blue-500"></i> Item Management
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
            <button id="openAddItemModalBtn"
                    class="btn-success font-semibold px-6 py-3 rounded-lg transition-colors duration-200 shadow-md flex items-center">
                <i data-feather="plus" class="w-4 h-4 inline-block mr-2"></i> Add Item
            </button>
        </c:if>
    </div>

    <div class="card-light p-8 rounded-2xl">
        <h2 class="text-2xl font-bold text-gray-900 mb-6">Item List</h2>

        <div id="messageDisplay" class="hidden p-4 mb-4 text-sm rounded-lg" role="alert">
            <span id="messageText"></span>
        </div>

        <div id="loadingIndicator" class="text-center py-8 hidden">
            <div class="animate-spin rounded-full h-10 w-10 border-b-2 border-purple-600 mx-auto"></div>
            <p class="text-gray-600 mt-3">Loading items...</p>
        </div>

        <div class="overflow-x-auto">
            <table class="min-w-full table-auto">
                <thead class="bg-gray-100 text-gray-700 font-bold">
                <tr>
                    <th class="px-6 py-3 text-left rounded-tl-xl">ID</th>
                    <th class="px-6 py-3 text-left">Item Name</th>
                    <th class="px-6 py-3 text-left">Unit Price</th>
                    <th class="px-6 py-3 text-left">Stock</th>
                    <th class="px-6 py-3 text-center rounded-tr-xl">Actions</th>
                </tr>
                </thead>
                <tbody id="itemTableBody">
                </tbody>
            </table>
        </div>
    </div>
</div>

<div id="itemModal" class="fixed inset-0 hidden modal-overlay items-center justify-center z-50">
    <div class="modal-content bg-white p-8 rounded-2xl shadow-xl w-full max-w-lg mx-4">
        <div class="flex justify-between items-center mb-6">
            <h2 id="modalTitle" class="text-2xl font-bold text-gray-900">Add New Item</h2>
            <button class="close-modal-btn text-gray-500 hover:text-gray-700"><i data-feather="x" class="w-6 h-6"></i>
            </button>
        </div>
        <form id="itemForm" class="space-y-6">
            <input type="hidden" id="itemId" name="id">
            <div>
                <label for="itemName" class="block text-sm font-medium text-gray-700 mb-1">Item Name</label>
                <input type="text" id="itemName" name="itemName" required placeholder="Enter Item Name"
                       class="form-input-modern w-full"/>
            </div>
            <div>
                <label for="unitPrice" class="block text-sm font-medium text-gray-700 mb-1">Unit Price</label>
                <input type="number" id="unitPrice" name="unitPrice" required step="0.01" min="0"
                       placeholder="Enter Unit Price" class="form-input-modern w-full"/>
            </div>
            <div>
                <label for="quantity" class="block text-sm font-medium text-gray-700 mb-1">Stock Quantity</label>
                <input type="number" id="quantity" name="quantity" required min="0" placeholder="Enter Stock Quantity"
                       class="form-input-modern w-full"/>
            </div>
            <div class="flex justify-end gap-4 mt-6">
                <button type="button" class="close-modal-btn btn-secondary font-semibold px-6 py-3 rounded-lg">
                    Cancel
                </button>
                <button type="submit" class="btn-success font-semibold px-6 py-3 rounded-lg">Save Item</button>
            </div>
        </form>
    </div>
</div>

<div id="viewItemModal" class="fixed inset-0 hidden modal-overlay items-center justify-center z-50">
    <div class="modal-content bg-white p-8 rounded-2xl shadow-xl w-full max-w-md mx-4">
        <div class="flex justify-between items-center mb-6">
            <h2 class="text-2xl font-bold text-gray-900">Item Details</h2>
            <button class="close-modal-btn text-gray-500 hover:text-gray-700"><i data-feather="x" class="w-6 h-6"></i>
            </button>
        </div>
        <div class="space-y-4">
            <div><label class="block text-sm font-medium text-gray-500">Item ID</label>
                <p id="view-itemId" class="p-3 bg-gray-100 rounded-lg font-semibold"></p></div>
            <div><label class="block text-sm font-medium text-gray-500">Item Name</label>
                <p id="view-itemName" class="p-3 bg-gray-100 rounded-lg font-semibold"></p></div>
            <div><label class="block text-sm font-medium text-gray-500">Unit Price</label>
                <p id="view-unitPrice" class="p-3 bg-gray-100 rounded-lg font-semibold"></p></div>
            <div><label class="block text-sm font-medium text-gray-500">Stock</label>
                <p id="view-quantity" class="p-3 bg-gray-100 rounded-lg font-semibold"></p></div>
        </div>
        <div class="flex justify-end mt-6">
            <button class="close-modal-btn btn-secondary font-semibold px-6 py-3 rounded-lg">Close</button>
        </div>
    </div>
</div>

<div id="restockModal" class="fixed inset-0 hidden modal-overlay items-center justify-center z-50">
    <div class="modal-content bg-white p-8 rounded-2xl shadow-xl w-full max-w-md mx-4">
        <div class="flex justify-between items-center mb-6">
            <h2 class="text-2xl font-bold text-gray-900">Restock Item</h2>
            <button class="close-modal-btn text-gray-500 hover:text-gray-700"><i data-feather="x" class="w-6 h-6"></i>
            </button>
        </div>
        <form id="restockForm" class="space-y-6">
            <input type="hidden" id="restockItemId" name="id">
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Item Name</label>
                <p id="restockItemName" class="p-3 bg-gray-100 rounded-lg font-semibold"></p>
            </div>
            <div>
                <label for="quantityToAdd" class="block text-sm font-medium text-gray-700 mb-1">Quantity to Add</label>
                <input type="number" id="quantityToAdd" name="quantityToAdd" required min="1" value="1"
                       class="form-input-modern w-full"/>
            </div>
            <div class="flex justify-end gap-4 mt-6">
                <button type="button" class="close-modal-btn btn-secondary font-semibold px-6 py-3 rounded-lg">Cancel
                </button>
                <button type="submit" class="btn-primary font-semibold px-6 py-3 rounded-lg">Add Stock</button>
            </div>
        </form>
    </div>
</div>

<script>

    // --- 1. GLOBAL VARIABLE DECLARATIONS ---
    let itemTableBody, loadingIndicator, messageDisplay, messageText, searchInput, refreshBtn;
    let itemModal, viewItemModal, restockModal, itemForm, restockForm;
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
        messageDisplay.className = 'p-4 mb-4 text-sm rounded-lg';
        messageDisplay.classList.add(type === 'success' ? 'bg-green-100' : 'bg-red-100', type === 'success' ? 'text-green-800' : 'text-red-800');
        setTimeout(() => {
            messageDisplay.classList.add('hidden');
        }, 5000);
    }

    function showLoading(show) {
        loadingIndicator.classList.toggle('hidden', !show);
        itemTableBody.classList.toggle('hidden', show);
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
    async function fetchItems(searchTerm = '') {
        let url = getContextPath() + '/items';
        if (searchTerm) {
            url += '?search=' + encodeURIComponent(searchTerm);
        }
        try {
            const response = await fetch(url);
            const data = await response.json();
            if (!response.ok) throw new Error(data.message || 'Failed to fetch items.');
            renderItems(data);
        } catch (error) {
            showMessage(error.message, 'error');
            itemTableBody.innerHTML = `<tr><td colspan="5" class="text-center py-8 text-red-500">${error.message}</td></tr>`;
        } finally {
            showLoading(false);
        }
    }

    // --- Rendering Function ---
    function renderItems(items) {

        itemTableBody.innerHTML = '';
        if (!items || items.length === 0) {
            itemTableBody.innerHTML = '<tr><td colspan="5" class="text-center py-8 text-gray-500">No items found.</td></tr>';
            return;
        }

        const loggedInUserRole = document.getElementById('loggedInUserRole').value;
        const loggedInUserId = parseInt(document.getElementById('loggedInUserId').value, 10);
        const INITIAL_ADMIN_ID = parseInt(document.getElementById('initialAdminId').value, 10);

        console.log(loggedInUserRole, loggedInUserId, INITIAL_ADMIN_ID);


        items.forEach(item => {
            const row = document.createElement('tr');
            let rowHtml = '';
            rowHtml += '<td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">' + item.id + '</td>';
            rowHtml += '<td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">' + item.itemName + '</td>';
            rowHtml += '<td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">Rs. ' + parseFloat(item.unitPrice).toFixed(2) + '</td>';
            rowHtml += '<td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">';
            rowHtml += '<span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full ' + (item.quantity < 10 ? 'bg-red-100 text-red-800' : 'bg-green-100 text-green-800') + '">' + item.quantity + ' stock</span>';
            rowHtml += '</td>';
            rowHtml += '<td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-center">';

            rowHtml += '<button title="View Item Details" class="text-blue-600 hover:text-blue-900 mr-3 view-btn" data-item-id="' + item.id + '">' +
                '<i data-feather="eye" class="w-4 h-4 inline-block align-middle"></i></button>';

            let canEdit = false;
            if (loggedInUserRole === 'ADMIN') {
                canEdit = true;
            }

            if (canEdit) {
                rowHtml += '<button title="Edit Item" class="text-gray-600 hover:text-gray-900 mr-3 edit-btn" data-item-id="' + item.id + '">' +
                    '<i data-feather="edit" class="w-4 h-4 inline-block align-middle"></i></button>';
            }

            let canDelete = false;
            if (loggedInUserRole === 'ADMIN') {
                canDelete = true;
            }

            if (canDelete) {
                rowHtml += '<button title="Delete Item" class="text-red-600 hover:text-red-900 delete-btn" data-item-id="' + item.id + '">' +
                    '<i data-feather="trash-2" class="w-4 h-4 inline-block align-middle"></i></button>';
            }

            rowHtml += '</td>';
            row.innerHTML = rowHtml;
            itemTableBody.appendChild(row);
        });

        feather.replace();

    }

    // --- Event Handler Functions ---
    function handleOpenAddModal() {
        itemForm.reset();
        document.getElementById('itemId').value = '';
        document.getElementById('modalTitle').textContent = 'Add New Item';
        openModal(itemModal);
    }

    async function handleOpenViewModal(id) {
        try {
            let url = getContextPath() + '/items?id=' + encodeURIComponent(id);
            const response = await fetch(url);
            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.message || 'Failed to fetch item details.');
            }

            document.getElementById('view-itemId').textContent = data.id;
            document.getElementById('view-itemName').textContent = data.itemName;
            document.getElementById('view-unitPrice').textContent = data.unitPrice;
            document.getElementById('view-quantity').textContent = data.quantity;
            openModal(viewItemModal);

        } catch (error) {
            showMessage(error.message, 'error');
        }
    }

    async function handleOpenEditModal(id) {
        try {
            let url = getContextPath() + '/items?id=' + encodeURIComponent(id);
            const response = await fetch(url);
            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.message || 'Failed to fetch item details.');
            }

            document.getElementById('modalTitle').textContent = 'Edit Item';
            document.getElementById('itemId').value = data.id;
            document.getElementById('itemName').value = data.itemName;
            document.getElementById('unitPrice').value = data.unitPrice;
            document.getElementById('quantity').value = data.quantity;
            openModal(itemModal);
        } catch (error) {
            showMessage(error.message, 'error');
        }
    }

    async function handleItemFormSubmit(e) {
        e.preventDefault();
        const id = document.getElementById('itemId').value;
        const isEdit = !!id;
        const formData = new URLSearchParams(new FormData(itemForm));

        if (isEdit) {
            formData.append('action', 'update');
        }

        try {

            const url = getContextPath() + '/items';

            const response = await fetch(url, {
                method: isEdit ? 'PUT' : 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                body: formData.toString()
            });
            const data = await response.json();
            if (!response.ok) throw new Error(data.message);
            showMessage(data.message, 'success');
            closeModal(itemModal);
            fetchItems(searchInput.value);
        } catch (error) {
            showMessage(error.message, 'error');
        }
    }

    async function handleDeleteItem(id) {
        if (!confirm('Are you sure you want to delete this item?')) return;
        try {
            let url = getContextPath() + '/items?id=' + encodeURIComponent(id);
            const response = await fetch(url,{method: 'DELETE'});
            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.message || 'Failed to delete item.');
            }

            showMessage(data.message, 'success');
            fetchItems(searchInput.value);
        } catch (error) {
            showMessage(error.message, 'error');
        }
    }

    function handleSearchInput() {
        clearTimeout(searchTimeout);
        searchTimeout = setTimeout(() => fetchItems(searchInput.value), 300);
    }

    // --- 3. MAIN INITIALIZATION FUNCTION ---
    function initItemPage() {
        itemTableBody = document.getElementById('itemTableBody');
        loadingIndicator = document.getElementById('loadingIndicator');
        messageDisplay = document.getElementById('messageDisplay');
        messageText = document.getElementById('messageText');
        searchInput = document.getElementById('searchInput');
        refreshBtn = document.getElementById('refreshBtn');
        itemModal = document.getElementById('itemModal');
        viewItemModal = document.getElementById('viewItemModal');
        restockModal = document.getElementById('restockModal');
        itemForm = document.getElementById('itemForm');
        restockForm = document.getElementById('restockForm');
        const openAddItemModalBtn = document.getElementById('openAddItemModalBtn');
        const allModals = [itemModal, viewItemModal, restockModal];

        if (openAddItemModalBtn) {
            openAddItemModalBtn.addEventListener('click', handleOpenAddModal);
        }

        refreshBtn.addEventListener('click', () => fetchItems(searchInput.value));
        itemForm.addEventListener('submit', handleItemFormSubmit);
        searchInput.addEventListener('input', handleSearchInput);

        document.body.addEventListener('click', (e) => {
            const viewBtn = e.target.closest('.view-btn');
            const editBtn = e.target.closest('.edit-btn');
            const restockBtn = e.target.closest('.restock-btn');
            const deleteBtn = e.target.closest('.delete-btn');
            const closeModalBtn = e.target.closest('.close-modal-btn');

            if (closeModalBtn) closeModal(closeModalBtn.closest('.modal-overlay'));
            if (viewBtn) handleOpenViewModal(viewBtn.dataset.itemId);
            if (editBtn) handleOpenEditModal(editBtn.dataset.itemId);
            if (deleteBtn) handleDeleteItem(deleteBtn.dataset.itemId);
        });

        allModals.forEach(modal => {
            if (modal) {
                modal.addEventListener('click', e => { if (e.target === modal) closeModal(modal); });
            }
        });

        // --- Initial Data Load ---
        fetchItems();
        feather.replace();
    }

    // --- 4. ENTRY POINT ---
    document.addEventListener('DOMContentLoaded', initItemPage);
</script>
</body>
</html>