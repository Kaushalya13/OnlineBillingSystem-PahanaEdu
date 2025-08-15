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
    <title>User Management</title>
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
            <i data-feather="user-check" class="w-8 h-8 text-blue-500"></i> User Management
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
            <button id="openAddUserModalBtn" class="btn-success bg-green-600 text-white font-semibold px-6 py-3 rounded-lg hover:bg-green-700 transition-colors duration-200 shadow-md flex items-center">
                <i data-feather="plus" class="w-4 h-4 inline-block mr-2"></i>
                Add User
            </button>
        </c:if>
    </div>

    <div class="card-light p-8 rounded-2xl">
        <h2 class="text-2xl font-bold text-gray-900 mb-6">User List</h2>

        <div id="messageDisplay" class="hidden p-4 mb-4 text-sm rounded-lg" role="alert">
            <span id="messageText"></span>
        </div>

        <div id="loadingIndicator" class="text-center py-8 hidden">
            <div class="animate-spin rounded-full h-10 w-10 border-b-2 border-purple-600 mx-auto"></div>
            <p class="text-gray-600 mt-3">Loading Users...</p>
        </div>

        <div class="overflow-x-auto">
            <table class="min-w-full table-auto">
                <thead class="bg-gray-100 text-gray-700 font-bold">
                <tr>
                    <th class="px-6 py-3 text-left rounded-tl-xl">User ID</th>
                    <th class="px-6 py-3 text-left">Username</th>
                    <th class="px-6 py-3 text-left">Role</th>
                    <th class="px-6 py-3 text-left rounded-tr-xl">Actions</th>
                </tr>
                </thead>
                <tbody>
                    <tbody id="userTableBody">
                </tbody>
            </table>
        </div>
    </div>
</div>

<div id="userModal" class="fixed inset-0 hidden modal-overlay items-center justify-center z-50">
    <div class="modal-content bg-white p-8 rounded-2xl shadow-xl w-full max-w-2xl mx-4 card-light">
        <div class="flex justify-between items-center mb-6">
            <h2 id="modalTitle" class="text-2xl font-bold text-gray-900">Add New User</h2>
            <button id="closeModalBtn" class="text-gray-500 hover:text-gray-700">
                <i data-feather="x" class="w-6 h-6"></i>
            </button>
        </div>
        <form id="userForm" class="space-y-6">
            <input type="hidden" id="userId" name="id">
            <div>
                <label for="username" class="block text-sm font-medium text-gray-700 mb-1">Username</label>
                <input type="text" id="username" name="username" placeholder="Enter Username" class="form-input-modern w-full" />
            </div>
            <div>
                <label for="password" class="block text-sm font-medium text-gray-700 mb-1">Password</label>
                <input type="password" id="password" name="password" placeholder="Enter Password" class="form-input-modern w-full" />
            </div>
            <div>
                <label for="role" class="block text-sm font-medium text-gray-700 mb-1">Role</label>
                <select id="role" name="role" class="form-input-modern w-full text-gray-500">
                    <option value="">Select Role</option>
                    <option value="ADMIN">Admin</option>
                    <option value="USER">User</option>
                </select>
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

<div id="viewUserModal" class="fixed inset-0 hidden modal-overlay items-center justify-center z-50">
    <div class="bg-white p-8 rounded-2xl shadow-xl w-full max-w-md mx-4 card-light">
        <div class="flex justify-between items-center mb-6">
            <h2 class="text-2xl font-bold text-gray-900">User Details</h2>
            <button id="close-view-modal-btn" class="text-gray-500 hover:text-gray-700">
                <i data-feather="x" class="w-6 h-6"></i>
            </button>
        </div>
        <div class="space-y-4">
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">User ID</label>
                <p id="view-user-id" class="p-3 bg-gray-100 rounded-lg text-gray-800 font-semibold"></p>
            </div>
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Username</label>
                <p id="view-username" class="p-3 bg-gray-100 rounded-lg text-gray-800 font-semibold"></p>
            </div>
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Role</label>
                <p id="view-role" class="p-3 bg-gray-100 rounded-lg text-gray-800 font-semibold"></p>
            </div>
        </div>
        <div class="flex justify-end mt-6">
            <button id="close-view-modal-btn-bottom" class="bg-gray-200 text-gray-800 font-semibold px-6 py-3 rounded-lg hover:bg-gray-300 transition-colors duration-200">
                Close
            </button>
        </div>
    </div>
</div>

<script>
    // --- 1. GLOBAL VARIABLE DECLARATIONS ---
    let userTableBody, loadingIndicator, messageDisplay, messageText, searchInput, refreshBtn;
    let userModal, viewUserModal, userForm;
    let searchTimeout;

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
        messageDisplay.className = 'p-4 mb-4 text-sm rounded-lg transition-all duration-300';
        messageDisplay.classList.add(type === 'success' ? 'bg-green-100' : 'bg-red-100', type === 'success' ? 'text-green-800' : 'text-red-800');
        messageDisplay.classList.remove('hidden');
        setTimeout(() => {
            messageDisplay.classList.add('hidden');
        }, 5000);
    }

    function showLoading(show) {
        loadingIndicator.classList.toggle('hidden', !show);
        userTableBody.parentElement.classList.toggle('hidden', show);
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
    async function fetchUser(searchTerm = '') {
        showLoading(true);
        let url = getContextPath() + '/users';
        if (searchTerm) {
            url += '?search=' + encodeURIComponent(searchTerm);
        }
        try {
            const response = await fetch(url);
            const data = await response.json();
            if (!response.ok) throw new Error(data.message || 'Failed to fetch users.');
            renderUsers(data);
        } catch (error) {
            showMessage(error.message, 'error');
            userTableBody.innerHTML = `<tr><td colspan="4" class="text-center py-8 text-red-500">${error.message}</td></tr>`;
        } finally {
            showLoading(false);
        }
    }

    // --- Rendering Function (Rewritten as requested) ---
    function renderUsers(users) {
        userTableBody.innerHTML = '';
        if (!users || users.length === 0) {
            userTableBody.innerHTML = '<tr><td colspan="4" class="text-center py-8 text-gray-500">User not found.</td></tr>';
            return;
        }

        const loggedInUserRole = document.getElementById('loggedInUserRole').value;

        users.forEach(user => {
            const row = document.createElement('tr');
            row.className = 'table-row-hover';

            let rowHtml = '';
            rowHtml += '<td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">' + user.id + '</td>';
            rowHtml += '<td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">' + user.username + '</td>';
            rowHtml += '<td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">' + user.role + '</td>';
            rowHtml += '<td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-left">';

            rowHtml += '<button title="View User Details" class="text-blue-600 hover:text-blue-900 mr-3 view-btn" data-user-id="' + user.id + '">';
            rowHtml += '<i data-feather="eye" class="w-4 h-4 inline-block align-middle"></i></button>';

            if (loggedInUserRole === 'ADMIN') {
                rowHtml += '<button title="Edit User" class="text-gray-600 hover:text-gray-900 mr-3 edit-btn" data-user-id="' + user.id + '">';
                rowHtml += '<i data-feather="edit" class="w-4 h-4 inline-block align-middle"></i></button>';

                rowHtml += '<button title="Delete User" class="text-red-600 hover:text-red-900 delete-btn" data-user-id="' + user.id + '">';
                rowHtml += '<i data-feather="trash-2" class="w-4 h-4 inline-block align-middle"></i></button>';
            }

            rowHtml += '</td>';
            row.innerHTML = rowHtml;
            userTableBody.appendChild(row);
        });

        feather.replace();
    }

    function handleOpenAddModal() {
        userForm.reset();
        document.getElementById('userId').value = '';
        document.getElementById('modalTitle').textContent = 'Add New User';
        document.querySelector('#password').parentElement.classList.remove('hidden');
        openModal(userModal);
    }

    async function handleOpenViewModal(id) {
        try {
            let url = getContextPath() + '/users?id=' + encodeURIComponent(id);
            const response = await fetch(url);
            const data = await response.json();
            if (!response.ok) throw new Error(data.message || 'Failed to fetch user details.');

            const user = data.user;
            document.getElementById('view-user-id').textContent = user.id;
            document.getElementById('view-username').textContent = user.username;
            document.getElementById('view-role').textContent = user.role;
            openModal(viewUserModal);
        } catch (error) {
            showMessage(error.message, 'error');
        }
    }

    async function handleOpenEditModal(id) {
        try {
            let url = getContextPath() + '/users?id=' + encodeURIComponent(id);
            const response = await fetch(url);
            const data = await response.json();
            if (!response.ok) throw new Error(data.message || 'Failed to fetch user details.');

            const user = data.user;
            document.getElementById('modalTitle').textContent = 'Edit User';
            document.getElementById('userId').value = user.id;
            document.getElementById('username').value = user.username;
            document.getElementById('role').value = user.role;

            const passwordInput = document.querySelector('#password');
            passwordInput.value = '';
            passwordInput.placeholder = 'Enter new password (optional)';

            openModal(userModal);
        } catch (error) {
            showMessage(error.message, 'error');
        }
    }

    async function handleUserFormSubmit(e) {
        e.preventDefault();
        const id = document.getElementById('userId').value;
        const isEdit = !!id;
        const formData = new URLSearchParams(new FormData(userForm));

        if (isEdit) {
            formData.append('action', 'update');
        } else {
            formData.append('action', 'add');
        }

        if (!document.getElementById('password').value.trim()) {
            formData.delete('password');
        }

        try {
            const url = getContextPath() + '/users';
            const response = await fetch(url, {
                method: isEdit ? 'PUT' : 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                body: formData.toString()
            });
            const data = await response.json();
            if (!response.ok) throw new Error(data.message);
            showMessage(data.message, 'success');
            closeModal(userModal);
            fetchUser(searchInput.value);
        } catch (error) {
            showMessage(error.message, 'error');
        }
    }

    async function handleDeleteUser(id) {
        if (!confirm('Are you sure you want to delete this user?')) return;
        try {
            let url = getContextPath() + '/users?id=' + encodeURIComponent(id);
            const response = await fetch(url, { method: 'DELETE' });
            const data = await response.json();
            if (!response.ok) throw new Error(data.message || 'Failed to delete user.');

            showMessage(data.message, 'success');
            fetchUser(searchInput.value);
        } catch (error) {
            showMessage(error.message, 'error');
        }
    }

    function handleSearchInput() {
        clearTimeout(searchTimeout);
        searchTimeout = setTimeout(() => fetchUser(searchInput.value), 1000);
    }

    // --- 3. MAIN INITIALIZATION FUNCTION ---
    function initUserPage() {
        userTableBody = document.getElementById('userTableBody');
        loadingIndicator = document.getElementById('loadingIndicator');
        messageDisplay = document.getElementById('messageDisplay');
        messageText = document.getElementById('messageText');
        searchInput = document.getElementById('searchInput');
        refreshBtn = document.getElementById('refreshBtn');
        userModal = document.getElementById('userModal');
        viewUserModal = document.getElementById('viewUserModal');
        userForm = document.getElementById('userForm');
        const openAddUserModalBtn = document.getElementById('openAddUserModalBtn');
        const allModals = [userModal, viewUserModal];

        if (openAddUserModalBtn) {
            openAddUserModalBtn.addEventListener('click', handleOpenAddModal);
        }

        refreshBtn.addEventListener('click', () => fetchUser(searchInput.value));
        userForm.addEventListener('submit', handleUserFormSubmit);
        searchInput.addEventListener('input', handleSearchInput);

        document.getElementById('closeModalBtn').addEventListener('click', () => closeModal(userModal));
        document.getElementById('cancel-add-modal-btn').addEventListener('click', () => closeModal(userModal));
        document.getElementById('close-view-modal-btn').addEventListener('click', () => closeModal(viewUserModal));
        document.getElementById('close-view-modal-btn-bottom').addEventListener('click', () => closeModal(viewUserModal));

        document.body.addEventListener('click', (e) => {
            const viewBtn = e.target.closest('.view-btn');
            const editBtn = e.target.closest('.edit-btn');
            const deleteBtn = e.target.closest('.delete-btn');

            if (viewBtn) handleOpenViewModal(viewBtn.dataset.userId);
            if (editBtn) handleOpenEditModal(editBtn.dataset.userId);
            if (deleteBtn) handleDeleteUser(deleteBtn.dataset.userId);
        });

        allModals.forEach(modal => {
            if (modal) {
                modal.addEventListener('click', e => { if (e.target === modal) closeModal(modal); });
            }
        });

        // --- Initial Data Load ---
        fetchUser();
    }

    // --- 4. ENTRY POINT (Corrected) ---
    window.addEventListener('load', initUserPage);
</script>
</body>
</html>