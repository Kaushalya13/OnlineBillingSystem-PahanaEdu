<%--
  Created by IntelliJ IDEA.
  User: Niwanthi
  Date: 8/16/2025
  Time: 8:36 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ include file="sidebar.jsp" %>

<html>
<head>
  <title>Bill Management</title>
  <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
  <script src="https://unpkg.com/feather-icons"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf/2.5.1/jspdf.umd.min.js"></script>
  <style>
    .action-btn {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      padding: 6px;
      border-radius: 0.5rem;
      transition: all 0.2s;
    }

    .action-btn.view {
      color: #2563eb;
    }

    .action-btn.view:hover {
      background: #dbeafe;
    }

    .action-btn.delete {
      color: #dc2626;
    }

    .action-btn.delete:hover {
      background: #fee2e2;
    }

    .modal-backdrop {
      background-color: rgba(0, 0, 0, 0.5);
    }
  </style>
</head>
<body class="bg-gray-100 flex">
<div class="ml-64 p-8 w-full">
  <div class="flex flex-col sm:flex-row justify-between items-center mb-8">
    <div>
      <h1 class="text-3xl font-bold text-gray-800">Bill Management</h1>
      <p class="text-gray-600">Create new bills and manage past orders.</p>
    </div>
    <button id="generateBillBtn"
            class="bg-green-600 hover:bg-green-700 text-white font-semibold py-2 px-5 rounded-md inline-flex items-center shadow-md">
      <i data-feather="plus-circle" class="w-5 h-5 mr-2"></i> Generate New Bill
    </button>
  </div>

  <div class="bg-white rounded-lg shadow-md border border-gray-200">
    <div class="p-4 border-b">
      <input type="text" id="billSearchInput" placeholder="Search by customer name or bill ID"
             class="w-full pl-4 pr-4 py-2 border rounded-md"/>
    </div>
    <div class="overflow-x-auto">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
        <tr>
          <th class="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase">Bill ID</th>
          <th class="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase">Customer</th>
          <th class="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase">Total Amount</th>
          <th class="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase">Generated At</th>
          <th class="px-6 py-3 text-center text-xs font-semibold text-gray-700 uppercase">Actions</th>
        </tr>
        </thead>
        <tbody id="billTableBody" class="bg-white divide-y"></tbody>
      </table>
    </div>
  </div>
</div>

<div id="billModal" class="fixed inset-0 z-50 flex items-center justify-center hidden modal-backdrop">
  <div class="bg-white rounded-lg shadow-xl w-full max-w-3xl m-4">
    <div class="flex items-center justify-between p-4 border-b">
      <h3 class="text-xl font-semibold">Generate New Bill</h3>
      <button id="closeModalBtn" class="text-gray-400 hover:text-gray-600"><i data-feather="x"
                                                                              class="w-6 h-6"></i></button>
    </div>
    <div class="p-6 space-y-6">
      <div class="p-4 border rounded-md">
        <h4 class="font-bold text-gray-700 mb-2">Find Customer</h4>
        <div class="flex items-center space-x-4 w-full">
          <input type="text" id="phoneSearchInput" placeholder="Enter phone number"
                 class="flex-grow p-2 border rounded-md w-1/2">
          <div id="customerDetailsWrapper"
               class="flex-grow bg-gray-100 p-2 rounded-md hidden flex items-center justify-center min-w-[350px]">
            <h1 id="customerDisplay" class="font-semibold text-center text-gray-800"></h1>
          </div>
        </div>
      </div>
      <div class="p-4 border rounded-md">
        <h4 class="font-bold mb-3">Add Items to Bill</h4>
        <div class="grid grid-cols-1 md:grid-cols-5 gap-4 items-center">
          <select id="itemSelect" class="md:col-span-2 p-2 h-10 border rounded-md">
            <option>Loading...</option>
          </select>
          <div class="flex items-center justify-center md:col-span-2">
            <button id="decrementQty" type="button"
                    class="bg-gray-200 h-10 w-10 flex items-center justify-center rounded-l-lg"><i
                    data-feather="minus"></i></button>
            <input type="number" id="itemQuantity" value="1" min="1"
                   class="w-16 h-10 text-center border-t border-b">
            <button id="incrementQty" type="button"
                    class="bg-gray-200 h-10 w-10 flex items-center justify-center rounded-r-lg"><i
                    data-feather="plus"></i></button>
          </div>
          <button id="addItemBtn"
                  class="bg-blue-600 text-white h-10 px-4 rounded-lg inline-flex items-center justify-center">
            <i data-feather="plus" class="w-5 h-5 mr-2"></i> Add
          </button>
        </div>
      </div>
      <div>
        <h4 class="font-bold mb-2">Bill Summary</h4>
        <div class="border rounded-md overflow-hidden">
          <table class="min-w-full">
            <thead class="bg-gray-100">
            <th class="p-2 text-left text-sm font-medium uppercase">Item</th>
            <th class="p-2 text-center text-sm font-medium uppercase">Qty</th>
            <th class="p-2 text-right text-sm font-medium uppercase">Unit Price</th>
            <th class="p-2 text-right text-sm font-medium uppercase">Total</th>
            <th class="p-2 text-center text-sm font-medium uppercase">Action</th>
            </thead>
            <tbody id="billItemsTbody" class="divide-y"></tbody>
            <tfoot id="billFooter" class="bg-gray-100 border-t-2 hidden">
            <tr>
              <td colspan="4" class="p-3 text-right font-bold">Grand Total</td>
              <td id="grandTotal" class="p-3 text-right font-bold"></td>
            </tr>
            </tfoot>
          </table>
        </div>
      </div>
    </div>

    <div class="flex items-center justify-end p-4 border-t space-x-4">
      <button id="cancelBtn" class="bg-gray-200 font-semibold py-2 px-4 rounded-md">Close</button>
      <button id="createBillBtn"
              class="bg-green-600 text-white font-semibold py-2 px-4 rounded-md inline-flex items-center">
        <i data-feather="save" class="w-4 h-4 mr-2"></i> Save Bill
      </button>
    </div>
  </div>
</div>

<div id="viewBillModal" class="fixed inset-0 z-50 flex items-center justify-center hidden modal-backdrop">
  <div class="bg-white rounded-lg shadow-xl w-full max-w-2xl m-4">
    <div class="bg-blue-300 flex items-center justify-between p-4 border-b">
      <h3 id="viewBillTitle" class="text-xl font-semibold"></h3>
      <button id="closeViewModalBtn"><i data-feather="x" class="w-6 h-6"></i></button>
    </div>
    <div class="p-6 space-y-4">
      <div class="grid grid-cols-2 gap-4 text-sm">
        <div><p class="font-bold text-gray-500">Customer Details</p>
          <p id="viewCustomerName"></p></div>
        <div><p class="font-bold text-gray-500">Account Details</p>
          <p id="viewAccountNumber"></p></div>
      </div>
      <table class="min-w-full">
        <thead class="bg-gray-50">
        <tr>
          <th class="px-4 py-2 text-left text-xs font-medium uppercase">Item</th>
          <th class="px-4 py-2 text-center text-xs font-medium uppercase">Qty</th>
          <th class="px-4 py-2 text-right text-xs font-medium uppercase">Unit Price</th>
          <th class="px-4 py-2 text-right text-xs font-medium uppercase">Total</th>
        </tr>
        </thead>
        <tbody id="viewBillItemsTbody" class="divide-y"></tbody>
        <tfoot class="bg-gray-50 border-t-2">
        <tr>
          <td colspan="3" class="px-4 py-3 text-right font-bold">Grand Total</td>
          <td id="viewGrandTotal" class="px-4 py-3 text-right font-bold"></td>
        </tr>
        </tfoot>
      </table>
    </div>

    <div class="flex items-center justify-end p-4 border-t space-x-4">
      <button id="downloadPdfBtn"
              class="bg-blue-600 text-white font-semibold py-2 px-4 rounded-md inline-flex items-center">
        <i data-feather="download" class="w-4 h-4 mr-2"></i> Download PDF
      </button>
    </div>
  </div>
</div>
<script>
  document.addEventListener('DOMContentLoaded', () => {
    feather.replace();

    const billTableBody = document.getElementById('billTableBody');
    const generateBillBtn = document.getElementById('generateBillBtn');
    const billModal = document.getElementById('billModal');
    const closeModalBtn = document.getElementById('closeModalBtn');
    const cancelBtn = document.getElementById('cancelBtn');
    const phoneSearchInput = document.getElementById('phoneSearchInput');
    const customerDetailsWrapper = document.getElementById('customerDetailsWrapper');
    const customerDisplay = document.getElementById('customerDisplay');
    const itemSelect = document.getElementById('itemSelect');
    const itemQuantity = document.getElementById('itemQuantity');
    const decrementQty = document.getElementById('decrementQty');
    const incrementQty = document.getElementById('incrementQty');
    const addItemBtn = document.getElementById('addItemBtn');
    const billItemsTbody = document.getElementById('billItemsTbody');
    const grandTotalEl = document.getElementById('grandTotal');
    const billFooter = document.getElementById('billFooter');
    const createBillBtn = document.getElementById('createBillBtn');
    const viewBillModal = document.getElementById('viewBillModal');
    const closeViewModalBtn = document.getElementById('closeViewModalBtn');
    const viewBillTitle = document.getElementById('viewBillTitle');
    const viewCustomerName = document.getElementById('viewCustomerName');
    const viewAccountNumber = document.getElementById('viewAccountNumber');
    const viewBillItemsTbody = document.getElementById('viewBillItemsTbody');
    const viewGrandTotal = document.getElementById('viewGrandTotal');
    const downloadPdfBtn = document.getElementById('downloadPdfBtn');


    let selectedCustomer = null;
    let billItems = [];
    let availableItems = [];
    let currentBillData = null;

    const openCreateModal = () => {
      resetForm();
      fetchItems();
      billModal.classList.remove('hidden');
    };
    const closeCreateModal = () => billModal.classList.add('hidden');
    const openViewModal = () => viewBillModal.classList.remove('hidden');
    const closeViewModal = () => viewBillModal.classList.add('hidden');

    generateBillBtn.addEventListener('click', openCreateModal);
    closeModalBtn.addEventListener('click', closeCreateModal);
    cancelBtn.addEventListener('click', closeCreateModal);
    closeViewModalBtn.addEventListener('click', closeViewModal);

    const fetchItems = async () => {
      try {
        const response = await fetch('items');
        if (!response.ok) throw new Error('Could not fetch items');
        availableItems = await response.json();
        itemSelect.innerHTML = '<option value="">Select an Item</option>';
        availableItems.forEach(item => {
          const option = document.createElement('option');
          option.value = item.id;
          option.textContent = item.itemName;
          option.dataset.price = item.unitPrice;
          itemSelect.appendChild(option);
        });
      } catch (error) {
        itemSelect.innerHTML = '<option>Could not load items</option>';
      }
    };

    const searchCustomer = async (phone) => {
      if (!phone) {
        customerDetailsWrapper.classList.add('hidden');
        selectedCustomer = null;
        return;
      }
      customerDetailsWrapper.classList.remove('hidden');
      customerDisplay.textContent = 'Searching...';
      customerDetailsWrapper.classList.remove('bg-green-100', 'bg-red-100');
      try {
        const response = await fetch(`\${getContextPath()}/customers?phone=\${encodeURIComponent(phone)}`);

        if (!response.ok) throw new Error('Customer not found');
        const customerData = await response.json();

        if (customerData && customerData.length > 0) {
          const customer = customerData[0];

          const name = customer.cus_Name || "Unknown";
          const account = customer.cus_AccountNumber || "N/A";

          console.log("Customer name:", name);
          console.log("Account:", account);

          customerDisplay.textContent = `${name} (Acc: ${account})`;

          console.log("customer object:", customer);
          console.log("customerDisplay element:", customerDisplay);

          customerDetailsWrapper.classList.remove('hidden');
          customerDetailsWrapper.classList.add('bg-green-100');
          selectedCustomer = customer;
        } else {
          throw new Error('Customer data is empty');
        }
      } catch (error) {
        customerDisplay.textContent = 'Customer Not Found';
        customerDetailsWrapper.classList.add('bg-red-100');
        selectedCustomer = null;
      }
    };

    const renderBillItems = () => {
      billItemsTbody.innerHTML = '';
      if (billItems.length === 0) {
        billItemsTbody.innerHTML = '<tr><td colspan="5" class="px-4 py-8 text-center text-gray-500">No items have been added yet.</td></tr>';
        billFooter.classList.add('hidden');
        return;
      }
      billFooter.classList.remove('hidden');
      let grandTotal = 0;
      billItems.forEach((item, index) => {
        const total = item.quantity * item.unitPrice;
        grandTotal += total;
        const row = document.createElement('tr');
        let rowHtml = '';
        rowHtml += '<td class="px-4 py-2">' + item.name + '</td>';
        rowHtml += '<td class="px-4 py-2 text-center">' + item.quantity + '</td>';
        rowHtml += '<td class="px-4 py-2 text-right">Rs. ' + item.unitPrice.toFixed(2) + '</td>';
        rowHtml += '<td class="px-4 py-2 text-right">Rs. ' + total.toFixed(2) + '</td>';
        rowHtml += '<td class="px-4 py-2 text-center">';
        rowHtml += '<button class="action-btn delete" data-index="' + index + '" title="Remove"><i data-feather="x-circle" class="w-5 h-5"></i></button>';
        rowHtml += '</td>';
        row.innerHTML = rowHtml;
        billItemsTbody.appendChild(row);
      });
      grandTotalEl.textContent = 'Rs. ' + grandTotal.toFixed(2);
      feather.replace();
    };

    const resetForm = () => {
      selectedCustomer = null;
      billItems = [];
      phoneSearchInput.value = '';
      customerDetailsWrapper.classList.add('hidden');
      customerDetailsWrapper.classList.remove('bg-green-100', 'bg-red-100');
      customerDisplay.textContent = '';
      itemQuantity.value = '1';
      renderBillItems();
    };

    phoneSearchInput.addEventListener('keydown', (e) => {
      if (e.key === 'Enter') {
        e.preventDefault();
        searchCustomer(e.target.value.trim());
      }
    });

    incrementQty.addEventListener('click', () => itemQuantity.value++);

    decrementQty.addEventListener('click', () => {
      if (itemQuantity.value > 1) itemQuantity.value--;
    });

    addItemBtn.addEventListener('click', () => {
      const itemId = itemSelect.value;
      const quantity = parseInt(itemQuantity.value, 10);
      if (!itemId || quantity < 1) return;
      const selectedOption = itemSelect.options[itemSelect.selectedIndex];
      const itemName = selectedOption.textContent;
      const unitPrice = parseFloat(selectedOption.dataset.price);
      const existingItem = billItems.find(item => item.id == itemId);
      if (existingItem) {
        existingItem.quantity += quantity;
      } else {
        billItems.push({id: itemId, name: itemName, quantity, unitPrice});
      }
      renderBillItems();
    });

    billItemsTbody.addEventListener('click', (e) => {
      const removeButton = e.target.closest('.delete');
      if (removeButton) {
        const indexToRemove = parseInt(removeButton.dataset.index, 10);
        billItems.splice(indexToRemove, 1);
        renderBillItems();
      }
    });

    createBillBtn.addEventListener('click', async () => {
      if (selectedCustomer) {
        console.log(`ID that will be sent to backend: ${selectedCustomer.cus_Id}`);
      } else {
        console.log("The 'selectedCustomer' variable is null!");
      }
      if (!selectedCustomer || !selectedCustomer.cus_Id) {
        alert('Please find and select a valid customer first.');
        return;
      }
      if (billItems.length === 0) {
        alert('Please add at least one item to the bill.');
        return;
      }

      const formData = new URLSearchParams();
      formData.append('customerId', selectedCustomer.cus_Id);
      billItems.forEach(item => {
        formData.append('item_id', item.id);
        formData.append('units', item.quantity);
      });


      try {
        const response = await fetch('bills', {
          method: 'POST',
          body: formData
        });
        if (!response.ok) {
          const errorData = await response.json();
          throw new Error(errorData.message || 'Failed to create bill');
        }
        const result = await response.json();
        alert(`Bill ${result.id} created successfully!`);
        handleDownloadPdf(result);
        closeCreateModal();
        await fetchBills();
      } catch (error) {
        console.error('Error during bill creation or refresh:', error);
        alert(`Error: ${error.message}`);
      }
    });

    const fetchBills = async (searchTerm = '') => {
      let url = 'bills';
      if (searchTerm) url += '?search=' + encodeURIComponent(searchTerm);
      try {
        const response = await fetch(url);
        if (!response.ok) throw new Error('Could not load bills');
        const bills = await response.json();
        renderBillTable(bills);
      } catch (error) {
        billTableBody.innerHTML = '<tr><td colspan="5" class="text-center py-4 text-red-500">Could not load bills.</td></tr>';
      }
    };

    const renderBillTable = (bills) => {
      billTableBody.innerHTML = '';
      if (!bills || bills.length === 0) {
        billTableBody.innerHTML = '<tr><td colspan="5" class="text-center py-4">No bills found.</td></tr>';
        return;
      }
      bills.forEach(bill => {
        const row = document.createElement('tr');
        let rowHtml = '';
        rowHtml += '<td class="px-6 py-4 font-medium">' + bill.id + '</td>';
        rowHtml += '<td class="px-6 py-4">' + (bill.customerName || 'N/A') + '</td>';
        rowHtml += '<td class="px-6 py-4">Rs. ' + parseFloat(bill.totalAmount || 0).toFixed(2) + '</td>';
        rowHtml += '<td class="px-6 py-4">' + (bill.generatedAt ? new Date(bill.generatedAt).toLocaleDateString() : 'N/A') + '</td>';
        rowHtml += '<td class="px-6 py-4 text-center">';
        rowHtml += '<button class="action-btn view" data-bill-id="' + bill.id + '" title="View Bill"><i data-feather="eye"></i></button>';
        rowHtml += '</td>';
        row.innerHTML = rowHtml;
        billTableBody.appendChild(row);
      });
      feather.replace();
    };

    function getContextPath() {
      const path = window.location.pathname;
      const secondSlashIndex = path.indexOf("/", 1);
      if (secondSlashIndex !== -1) {
        return path.substring(0, secondSlashIndex);
      }
      return "";
    }

    const handleOpenViewModal = async (billId) => {
      try {
        const response = await fetch(`\${getContextPath()}/bills?id=\${billId}`);
        if (!response.ok) throw new Error('Failed to fetch bill details.');

        const bill = await response.json();

        currentBillData = bill;
        viewBillTitle.textContent = `Bill Details ${bill.id}`;
        viewCustomerName.textContent = bill.customerName || 'N/A';
        viewAccountNumber.textContent = bill.customerAccountNumber || 'N/A';

        viewBillItemsTbody.innerHTML = '';
        if (bill.details && bill.details.length > 0) {
          bill.details.forEach(item => {
            const row = document.createElement('tr');
            let rowHtml = '';
            rowHtml += '<td class="px-4 py-2">' + item.itemNameAtSale + '</td>';
            rowHtml += '<td class="px-4 py-2 text-center">' + item.units + '</td>';
            rowHtml += '<td class="px-4 py-2 text-right">Rs. ' + parseFloat(item.unitPriceAtSale).toFixed(2) + '</td>';
            rowHtml += '<td class="px-4 py-2 text-right">Rs. ' + parseFloat(item.total).toFixed(2) + '</td>';
            row.innerHTML = rowHtml;
            viewBillItemsTbody.appendChild(row);
          });
        } else {
          viewBillItemsTbody.innerHTML = '<tr><td colspan="4" class="text-center py-4">No items found for this bill.</td></tr>';
        }

        viewGrandTotal.textContent = 'Rs. ' + parseFloat(bill.totalAmount).toFixed(2);
        openViewModal();
      } catch (error) {
        alert('Error: ' + error.message);
      }
    };

    const handleDownloadPdf = () => {
      if (!currentBillData) {
        alert("No bill data to download.");
        return;
      }

      const {jsPDF} = window.jspdf;
      const doc = new jsPDF();
      const bill = currentBillData;
      let y = 15;

      doc.setFontSize(20);
      doc.text("Pahana Edu - Bill Receipt", 105, y, {align: 'center'});
      y += 10;
      doc.setFontSize(12);
      doc.text("Bill ID: " + bill.id, 14, y);
      doc.text("Date: " + new Date(bill.generatedAt || Date.now()).toLocaleDateString(), 196, y, {align: 'right'});
      y += 10;

      doc.line(14, y, 196, y);
      y += 8;
      doc.setFontSize(10);
      doc.text("Customer: " + bill.customerName, 14, y);
      doc.text("Account No: " + bill.customerAccountNumber, 196, y, {align: 'right'});
      y += 8;
      doc.line(14, y, 196, y);
      y += 10;

      doc.setFont(undefined, 'bold');
      doc.text("Item", 14, y);
      doc.text("Qty", 120, y, {align: 'center'});
      doc.text("Unit Price", 155, y, {align: 'right'});
      doc.text("Total", 196, y, {align: 'right'});
      doc.setFont(undefined, 'normal');
      y += 7;


      if (bill.details && bill.details.length > 0) {
        bill.details.forEach(item => {
          doc.text(item.itemNameAtSale, 14, y);
          doc.text(item.units.toString(), 120, y, {align: 'center'});
          doc.text("Rs. " + parseFloat(item.unitPriceAtSale).toFixed(2), 155, y, {align: 'right'});
          doc.text("Rs. " + parseFloat(item.total).toFixed(2), 196, y, {align: 'right'});
          y += 7;
        });
      }

      y += 5;
      doc.line(14, y, 196, y);
      y += 8;
      doc.setFont(undefined, 'bold');
      doc.setFontSize(14);
      doc.text("Grand Total:", 155, y, {align: 'right'});
      doc.text("Rs. " + parseFloat(bill.totalAmount).toFixed(2), 196, y, {align: 'right'});

      doc.save("Bill-" + bill.id + ".pdf");
    };

    downloadPdfBtn.addEventListener('click', () => {
      handleDownloadPdf();
    });
    billTableBody.addEventListener('click', (e) => {
      const viewButton = e.target.closest('.view');
      if (viewButton) {
        handleOpenViewModal(viewButton.dataset.billId);
      }
    });

    fetchBills();
  });
</script>
</body>
</html>