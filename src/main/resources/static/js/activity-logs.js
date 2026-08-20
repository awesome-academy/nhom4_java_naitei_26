function getCsrfConfig() {
    const token = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
    return (token && header) ? { [header]: token } : {};
}

function deleteLog(id) {
    if (!confirm(`Are you sure you want to delete log #${id}?`)) return;

    fetch(`/api/activity-logs/${id}`, {
        method: 'DELETE',
        headers: getCsrfConfig()
    })
        .then(response => {
            if (response.ok) {
                // Tải lại trang để Thymeleaf render lại danh sách mới nhất
                window.location.reload();
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
                // Chuyển hướng về trang đầu tiên sau khi dọn dẹp
                window.location.href = '/admin/activity-logs?page=0';
            } else {
                alert('Failed to delete old logs');
            }
        })
        .catch(() => alert('Error connecting to server'));
}