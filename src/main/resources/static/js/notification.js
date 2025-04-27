// notifications.js
function showNotification(message, type) {
    if (!message) return;

    const backgroundColor =
        type === "success" ? "#4CAF50" :
            type === "error" ? "#f44336" :
                type === "warning" ? "#ff9800" :
                    "#2196F3"; // default blue

    Toastify({
        text: message,
        duration: 3000,
        close: true,
        gravity: "top",
        position: "right",
        backgroundColor: backgroundColor,
    }).showToast();
}


// notifications.js
function showCustomNotification(message, type) {
    if (!message) return;

    // Definir título según el tipo
    let title = "Info!";
    if (type === "success") title = "Success!";
    if (type === "warning") title = "Warning!";
    if (type === "error") title = "Error!";

    // Crear elementos
    const notification = document.createElement('div');
    notification.className = `custom-notification ${type}`;

    // Crear estructura HTML
    notification.innerHTML = `
        <div class="custom-notification-border"></div>
        <div class="custom-notification-content">
            <div class="custom-notification-title">${title}</div>
            <div class="custom-notification-message">${message}</div>
        </div>
        <button class="custom-notification-close">×</button>
    `;

    // Añadir al DOM
    document.body.appendChild(notification);

    // Animar entrada
    setTimeout(() => {
        notification.style.opacity = '1';
    }, 10);

    // Configurar botón de cierre
    const closeButton = notification.querySelector('.custom-notification-close');
    closeButton.addEventListener('click', () => {
        closeNotification(notification);
    });

    // Cerrar automáticamente después de 5 segundos
    setTimeout(() => {
        closeNotification(notification);
    }, 5000);
}

function closeNotification(notification) {
    notification.style.animation = 'slideOut 0.3s ease-out forwards';
    setTimeout(() => {
        if (notification.parentNode) {
            notification.parentNode.removeChild(notification);
        }
    }, 300);
}

// simpleNotifications.js
function showSimpleNotification(message, type = "success") {
    if (!message) return;

    // Crear elementos
    const notification = document.createElement('div');
    notification.className = 'simple-notification';

    // Determinar el icono según el tipo
    let iconPlaceholder = ""; // Aquí puedes poner tu icono

    if (type === "success") {
        // Placeholder para el icono de éxito (un círculo con check)
        iconPlaceholder = '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-check-circle-fill mi-icono-check notification-icon" viewBox="0 0 16 16">\n' +
            '  <path d="M16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0m-3.97-3.03a.75.75 0 0 0-1.08.022L7.477 9.417 5.384 7.323a.75.75 0 0 0-1.06 1.06L6.97 11.03a.75.75 0 0 0 1.079-.02l3.992-4.99a.75.75 0 0 0-.01-1.05z"/>\n' +
            '</svg>';
    } else if (type === "error") {
        // Placeholder para el icono de error
        iconPlaceholder = '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-x-circle-fill mi-icono-error notification-icon" viewBox="0 0 16 16">\n' +
            '  <path d="M16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0M5.354 4.646a.5.5 0 1 0-.708.708L7.293 8l-2.647 2.646a.5.5 0 0 0 .708.708L8 8.707l2.646 2.647a.5.5 0 0 0 .708-.708L8.707 8l2.647-2.646a.5.5 0 0 0-.708-.708L8 7.293z"/>\n' +
            '</svg>';
    } else if (type === "warning") {
        // Placeholder para el icono de advertencia
        iconPlaceholder = '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-exclamation-triangle-fill mi-icono-warning notification-icon" viewBox="0 0 16 16">\n' +
            '  <path d="M8.982 1.566a1.13 1.13 0 0 0-1.96 0L.165 13.233c-.457.778.091 1.767.98 1.767h13.713c.889 0 1.438-.99.98-1.767zM8 5c.535 0 .954.462.9.995l-.35 3.507a.552.552 0 0 1-1.1 0L7.1 5.995A.905.905 0 0 1 8 5m.002 6a1 1 0 1 1 0 2 1 1 0 0 1 0-2"/>\n' +
            '</svg>';
    } else {
        // Placeholder para el icono de información
        iconPlaceholder = '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-info-circle-fill mi-icono-info notification-icon" viewBox="0 0 16 16">\n' +
            '  <path d="M8 16A8 8 0 1 0 8 0a8 8 0 0 0 0 16m.93-9.412-1 4.705c-.07.34.029.533.304.533.194 0 .487-.07.686-.246l-.088.416c-.287.346-.92.598-1.465.598-.703 0-1.002-.422-.808-1.319l.738-3.468c.064-.293.006-.399-.287-.47l-.451-.081.082-.381 2.29-.287zM8 5.5a1 1 0 1 1 0-2 1 1 0 0 1 0 2"/>\n' +
            '</svg>';
    }

    // Crear estructura HTML
    notification.innerHTML = `
        ${iconPlaceholder}
        <div class="notification-message">${message}</div>
    `;

    // Añadir al DOM
    document.body.appendChild(notification);

    // Cerrar automáticamente después de 3 segundos
    setTimeout(() => {
        notification.style.animation = 'fadeOut 0.3s ease-out forwards';
        setTimeout(() => {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
        }, 300);
    },3000);
}