<%--
  Created by IntelliJ IDEA.
  User: Niwanthi
  Date: 8/4/2025
  Time: 11:16 PM
  To change this template use File | Settings | File Templates.
--%>

<%
  String currentPage = request.getRequestURI();
%>

<style>
  .sidebar-container {
    background-color: #ffffff;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
    border-right: 1px solid #e2e8f0;
  }
  .sidebar-link {
    color: #4a5568;
    transition: all 0.2s ease;
    border-radius: 0.75rem;
  }
  .sidebar-link:hover {
    background-color: #f7fafc;
    color: #1f2937;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
    transform: translateX(4px);
  }
  .sidebar-link.active {
    background-color: #2563eb;
    color: #000000;
    font-weight: 600;
    box-shadow: 0 4px 12px rgba(80, 209, 198, 0.25);
  }
  .sidebar-link.active .link-icon,
  .sidebar-link.active .link-text {
    color: #000000 !important;
  }
  .logout-button {
    background-color: #2563eb;
    transition: all 0.2s ease;
    box-shadow: 0 4px 12px rgba(90, 213, 190, 0.2);
  }
  .logout-button:hover {
    background-color: #2563eb;
    box-shadow: 0 6px 16px rgba(90, 213, 203, 0.3);
  }
</style>

<aside id="sidebar" class="sidebar-container w-64 text-gray-900 flex-shrink-0 min-h-screen fixed flex-col z-40 md:flex">
  <div class="flex items-center justify-center p-6 border-b border-gray-200">
    <i data-feather="book-open" class="w-8 h-8 text-blue-600 mr-3"></i>
    <h1 class="text-2xl font-bold text-gray-900">Pahana Edu</h1>
  </div>

  <nav class="flex-grow p-6">
    <ul class="space-y-4">
      <li>
        <a href="dashboard.jsp"
           class="sidebar-link flex items-center p-3 rounded-xl font-medium transition duration-200 <%= currentPage.contains("dashboard.jsp") ? "active" : "" %>">
          <i data-feather="home" class="link-icon w-5 h-5 mr-4 <%= currentPage.contains("dashboard.jsp") ? "" : "text-gray-500" %>"></i>
          <span class="link-text">Dashboard</span>
        </a>
      </li>
      <li>
        <a href="user.jsp"
           class="sidebar-link flex items-center p-3 rounded-xl font-medium transition duration-200 <%= currentPage.contains("user.jsp") ? "active" : "" %>">
          <i data-feather="user-check" class="link-icon w-5 h-5 mr-4 <%= currentPage.contains("user.jsp") ? "" : "text-gray-500" %>"></i>
          <span class="link-text">Users</span>
        </a>
      </li>
      <li>
        <a href="customer.jsp"
           class="sidebar-link flex items-center p-3 rounded-xl font-medium transition duration-200 <%= currentPage.contains("customer.jsp") ? "active" : "" %>">
          <i data-feather="users" class="link-icon w-5 h-5 mr-4 <%= currentPage.contains("customer.jsp") ? "" : "text-gray-500" %>"></i>
          <span class="link-text">Customers</span>
        </a>
      </li>
      <li>
        <a href="item.jsp"
           class="sidebar-link flex items-center p-3 rounded-xl font-medium transition duration-200 <%= currentPage.contains("item.jsp") ? "active" : "" %>">
          <i data-feather="box" class="link-icon w-5 h-5 mr-4 <%= currentPage.contains("item.jsp") ? "" : "text-gray-500" %>"></i>
          <span class="link-text">Items</span>
        </a>
      </li>
      <li>
        <a href="bill.jsp"
           class="sidebar-link flex items-center p-3 rounded-xl font-medium transition duration-200 <%= currentPage.contains("bill.jsp") ? "active" : "" %>">
          <i data-feather="file-text" class="link-icon w-5 h-5 mr-4 <%= currentPage.contains("bill.jsp") ? "" : "text-gray-500" %>"></i>
          <span class="link-text">Bill</span>
        </a>
      </li>
      <li>
        <a href="help.jsp"
           class="sidebar-link flex items-center p-3 rounded-xl font-medium transition duration-200 <%= currentPage.contains("help.jsp") ? "active" : "" %>">
          <i data-feather="help-circle" class="link-icon w-5 h-5 mr-4 <%= currentPage.contains("help.jsp") ? "" : "text-gray-500" %>"></i>
          <span class="link-text">Help</span>
        </a>
      </li>
    </ul>
  </nav>

  <div class="p-6 border-t border-gray-200">
    <a href="${pageContext.request.contextPath}/users?action=logout"
       class="logout-button w-full flex items-center justify-center text-black font-semibold py-3 px-4 rounded-lg transition duration-200 shadow-md">
      <i data-feather="log-out" class="w-5 h-5 mr-3 text-red-700"></i>
      Log Out
    </a>
  </div>
</aside>

<script>
  feather.replace();
</script>