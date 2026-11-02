/**
 * CAR RENTAL MANAGEMENT SYSTEM - GLOBAL JS MODULE
 */

const API_BASE = '/api';

// Current session state
let currentUser = null;

// Helper: Show Bootstrap toast / alert
function showAlert(message, type = 'danger', containerId = 'alert-container') {
    const container = document.getElementById(containerId);
    if (!container) return;

    const alertHtml = `
        <div class="custom-alert custom-alert-${type === 'success' ? 'success' : 'danger'} alert-dismissible fade show" role="alert">
            <i class="bi ${type === 'success' ? 'bi-check-circle-fill' : 'bi-exclamation-triangle-fill'}"></i>
            <div>${message}</div>
            <button type="button" class="btn-close ms-auto" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    `;
    container.innerHTML = alertHtml;

    // Auto dismiss after 6 seconds
    setTimeout(() => {
        if (container.firstElementChild) {
            container.innerHTML = '';
        }
    }, 6000);
}

// Fetch currently logged-in user
async function checkAuthStatus() {
    try {
        const response = await fetch(`${API_BASE}/auth/me`);
        const result = await response.json();
        if (result.success && result.data) {
            currentUser = result.data;
            updateNavbarForUser(currentUser);
            return currentUser;
        } else {
            currentUser = null;
            updateNavbarForUser(null);
            return null;
        }
    } catch (err) {
        console.error('Error checking auth:', err);
        currentUser = null;
        updateNavbarForUser(null);
        return null;
    }
}

// Update navbar links dynamically based on login state
function updateNavbarForUser(user) {
    const navAuth = document.getElementById('navbar-auth-links');
    if (!navAuth) return;

    if (user) {
        const isAdmin = user.role === 'ROLE_ADMIN';
        navAuth.innerHTML = `
            <li class="nav-item">
                <a class="nav-link" href="dashboard.html"><i class="bi bi-speedometer2 me-1"></i>Dashboard</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="cars.html"><i class="bi bi-car-front me-1"></i>Available Cars</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="bookings.html"><i class="bi bi-calendar-check me-1"></i>My Bookings</a>
            </li>
            ${isAdmin ? `
            <li class="nav-item">
                <a class="nav-link text-warning" href="admin.html"><i class="bi bi-shield-lock-fill me-1"></i>Admin Panel</a>
            </li>
            ` : ''}
            <li class="nav-item dropdown ms-lg-2">
                <a class="nav-link dropdown-toggle btn-outline-custom text-white py-1 px-3" href="#" role="button" data-bs-toggle="dropdown">
                    <i class="bi bi-person-circle me-1 text-danger"></i>${user.name.split(' ')[0]}
                </a>
                <ul class="dropdown-menu dropdown-menu-dark dropdown-menu-end shadow">
                    <li><h6 class="dropdown-header text-muted">${user.displayRole || user.email}</h6></li>
                    <li><a class="dropdown-item" href="dashboard.html"><i class="bi bi-grid me-2"></i>User Dashboard</a></li>
                    <li><a class="dropdown-item" href="bookings.html"><i class="bi bi-receipt me-2"></i>My Bookings</a></li>
                    <li><hr class="dropdown-divider"></li>
                    <li><a class="dropdown-item text-danger" href="#" onclick="handleLogout(event)"><i class="bi bi-box-arrow-right me-2"></i>Logout</a></li>
                </ul>
            </li>
        `;
    } else {
        navAuth.innerHTML = `
            <li class="nav-item">
                <a class="nav-link" href="cars.html"><i class="bi bi-car-front me-1"></i>Browse Cars</a>
            </li>
            <li class="nav-item ms-lg-2">
                <a class="nav-link btn-outline-custom" href="login.html"><i class="bi bi-box-arrow-in-right me-1"></i>Login</a>
            </li>
            <li class="nav-item ms-lg-2">
                <a class="btn-red" href="register.html"><i class="bi bi-person-plus me-1"></i>Register</a>
            </li>
        `;
    }
}

// Global logout handler
async function handleLogout(e) {
    if (e) e.preventDefault();
    try {
        await fetch(`${API_BASE}/auth/logout`, { method: 'POST' });
        currentUser = null;
        window.location.href = 'login.html?logout=true';
    } catch (err) {
        console.error('Logout error:', err);
        window.location.href = 'login.html';
    }
}

// Require authentication guard for protected pages
async function requireAuth(targetPage = 'login.html') {
    const user = await checkAuthStatus();
    if (!user) {
        window.location.href = `${targetPage}?redirect=${encodeURIComponent(window.location.pathname)}`;
        return null;
    }
    return user;
}

// Require admin guard
async function requireAdmin() {
    const user = await requireAuth();
    if (!user) return null;
    if (user.role !== 'ROLE_ADMIN') {
        alert('Access denied. Administrator privileges required.');
        window.location.href = 'dashboard.html';
        return null;
    }
    return user;
}

// Format currency
function formatCurrency(amount) {
    return '$' + Number(amount).toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

// Format date
function formatDate(dateStr) {
    if (!dateStr) return 'N/A';
    const date = new Date(dateStr);
    return date.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
}
