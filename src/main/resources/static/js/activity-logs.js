let currentPage = 0;
const pageSize = 10;

document.addEventListener('DOMContentLoaded', () => {
    loadLogs(0);
});

function getCsrfConfig() {
    const token = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
    return (token && header) ? { [header]: token } : {};
}

function loadLogs(page) {
    currentPage = page;
    const tableBody = document.getElementById('logTableBody');
    tableBody.innerHTML = '<tr><td colspan="6" class="text-center py-4 text-muted">Loading activity logs...</td></tr>';

    fetch(`/api/activity-logs?page=${page}&size=${pageSize}&sort=createdAt,desc`)
        .then(response => {
            if (!response.ok) throw new Error('Failed to fetch activity logs');
            return response.json();
        })
        .then(data => {
            renderTable(data.content);
            renderPagination(data.totalPages, data.number);
        })
        .catch(error => {
            tableBody.innerHTML = `<tr><td colspan="6" class="text-center text-danger py-4">${error.message}</td></tr>`;
        });
}

function renderTable(logs) {
    const tableBody = document.getElementById('logTableBody');
    if (!logs || logs.length === 0) {
        tableBody.innerHTML = '<tr><td colspan="6" class="text-center text-muted py-4">No activity logs found.</td></tr>';
        return;
    }

    tableBody.innerHTML = logs.map(log => {
        const formattedDate = log.createdAt ? new Date(log.createdAt).toLocaleString('vi-VN') : 'N/A';
        const userDisplay = log.userEmail ? `${log.userEmail} (ID: ${log.userId})` : `User ID: ${log.userId}`;

        return `
            <tr>
                <td class="ps-3 fw-semibold text-secondary">#${log.id}</td>
                <td><small class="text-muted">${formattedDate}</small></td>
                <td><span class="badge bg-primary-subtle text-primary border border-primary-subtle">${log.action}</span></td>
                <td><small>${userDisplay}</small></td>
                <td><small class="text-secondary">${log.description || '-'}</small></td>
                <td class="text-center">
                    <button class="btn btn-outline-danger btn-sm py-0 px-2" onclick="deleteLog(${log.id})">Delete</button>
                </td>
            </tr>
        `;
    }).join('');
}

function renderPagination(totalPages, activePage) {
    const pagination = document.getElementById('pagination');
    if (totalPages <= 1) {
        pagination.innerHTML = '';
        return;
    }

    let items = '';

    items += `
        <li class="page-item ${activePage === 0 ? 'disabled' : ''}">
            <button class="page-link" onclick="loadLogs(${activePage - 1})">Previous</button>
        </li>
    `;

    for (let i = 0; i < totalPages; i++) {
        items += `
            <li class="page-item ${i === activePage ? 'active' : ''}">
                <button class="page-link" onclick="loadLogs(${i})">${i + 1}</button>
            </li>
        `;
    }

    items += `
        <li class="page-item ${activePage === totalPages - 1 ? 'disabled' : ''}">
            <button class="page-link" onclick="loadLogs(${activePage + 1})">Next</button>
        </li>
    `;

    pagination.innerHTML = items;
}

function deleteLog(id) {
    if (!confirm(`Are you sure you want to delete log #${id}?`)) return;

    fetch(`/api/activity-logs/${id}`, {
        method: 'DELETE',
        headers: getCsrfConfig()
    })
        .then(response => {
            if (response.ok) {
                loadLogs(currentPage);
            } else {
                alert('Failed to delete activity log');
            }
        })
        .catch(() => alert('Error connecting to server'));
}

function deleteOldLogs() {
    const input = document.getElementById('beforeDateInput');
    if (!input.value) {
        alert('Please select a date threshold first.');
        return;
    }

    if (!confirm(`Are you sure you want to delete all logs created before ${input.value}?`)) return;

    const isoDate = new Date(input.value).toISOString();
    fetch(`/api/activity-logs/old?beforeDate=${encodeURIComponent(isoDate)}`, {
        method: 'DELETE',
        headers: getCsrfConfig()
    })
        .then(response => {
            if (response.ok) {
                alert('Old logs cleared successfully');
                loadLogs(0);
            } else {
                alert('Failed to delete old logs');
            }
        })
        .catch(() => alert('Error connecting to server'));
}