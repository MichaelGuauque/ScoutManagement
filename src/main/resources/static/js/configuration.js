document.addEventListener('DOMContentLoaded', function() {
    // Elementos del DOM
    const passwordForm = document.getElementById('passwordForm');
    const currentPasswordInput = document.getElementById('currentPassword');
    const newPasswordInput = document.getElementById('newPassword');
    const confirmPasswordInput = document.getElementById('confirmPassword');
    const strengthBar = document.getElementById('strengthBar');
    const strengthText = document.getElementById('strengthText');
    const matchError = document.getElementById('matchError');
    const reqLength = document.getElementById('reqLength');
    const reqUppercase = document.getElementById('reqUppercase');

    // Estado
    let passwordData = {
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
    };

    // Función para actualizar la fortaleza de la contraseña
    function updatePasswordStrength() {
        const password = passwordData.newPassword;

        if (!password) {
            strengthBar.style.width = '0';
            strengthBar.style.backgroundColor = '';
            strengthText.textContent = 'Ingresa tu nueva contraseña';
            return;
        }

        // Verificar requisitos
        const hasMinLength = password.length >= 8;
        const hasUppercase = /[A-Z]/.test(password);

        // Actualizar indicadores de requisitos
        if (hasMinLength) {
            reqLength.classList.add('valid');
        } else {
            reqLength.classList.remove('valid');
        }

        if (hasUppercase) {
            reqUppercase.classList.add('valid');
        } else {
            reqUppercase.classList.remove('valid');
        }

        // Determinar fortaleza
        let strength = 0;
        let strengthColor = '';
        let strengthMessage = '';

        if (password.length < 6) {
            strength = 25;
            strengthColor = '#ef4444'; // rojo
            strengthMessage = 'Contraseña débil';
        } else if (password.length < 10) {
            strength = 50;
            strengthColor = '#f59e0b'; // amarillo
            strengthMessage = 'Contraseña media';
        } else {
            strength = 100;
            strengthColor = '#10b981'; // verde
            strengthMessage = 'Contraseña fuerte';
        }

        // Actualizar UI
        strengthBar.style.width = `${strength}%`;
        strengthBar.style.backgroundColor = strengthColor;
        strengthText.textContent = strengthMessage;
    }

    // Función para verificar si las contraseñas coinciden
    function checkPasswordsMatch() {
        const { newPassword, confirmPassword } = passwordData;

        if (newPassword && confirmPassword) {
            if (newPassword !== confirmPassword) {
                matchError.style.display = 'block';
                confirmPasswordInput.style.borderColor = '#ef4444';
            } else {
                matchError.style.display = 'none';
                confirmPasswordInput.style.borderColor = '';
            }
        } else {
            matchError.style.display = 'none';
            confirmPasswordInput.style.borderColor = '';
        }
    }

    // Event listeners para los inputs
    currentPasswordInput.addEventListener('input', function(e) {
        passwordData.currentPassword = e.target.value;
    });

    newPasswordInput.addEventListener('input', function(e) {
        passwordData.newPassword = e.target.value;
        updatePasswordStrength();
        checkPasswordsMatch();
    });

    confirmPasswordInput.addEventListener('input', function(e) {
        passwordData.confirmPassword = e.target.value;
        checkPasswordsMatch();
    });

    // Event listener para el formulario - CORREGIDO
    passwordForm.addEventListener('submit', function(e) {
        // Validaciones antes del envío
        let isValid = true;

        // Validar que todos los campos estén llenos
        if (!passwordData.currentPassword.trim()) {
            isValid = false;
        }

        if (!passwordData.newPassword.trim()) {
            isValid = false;
        }

        if (!passwordData.confirmPassword.trim()) {
            isValid = false;
        }

        // Validar que las contraseñas coincidan
        if (passwordData.newPassword !== passwordData.confirmPassword) {
            matchError.style.display = 'block';
            confirmPasswordInput.style.borderColor = '#ef4444';
            isValid = false;
        }

        // Validar longitud mínima
        if (passwordData.newPassword.length < 6) {
            isValid = false;
        }

        // Si hay errores, prevenir el envío
        if (!isValid) {
            e.preventDefault();
            return false;
        }

        // Si llegamos aquí, el formulario es válido
        // NO llamamos e.preventDefault() para permitir el envío normal al servidor
        console.log('Formulario válido, enviando al servidor...');

        // El formulario se enviará automáticamente al controlador Spring Boot
        // No necesitamos hacer nada más aquí
    });
});

// Función para resetear el formulario (llamada desde el botón Cancelar)
function resetPasswordForm() {
    const currentPasswordInput = document.getElementById('currentPassword');
    const newPasswordInput = document.getElementById('newPassword');
    const confirmPasswordInput = document.getElementById('confirmPassword');
    const strengthBar = document.getElementById('strengthBar');
    const strengthText = document.getElementById('strengthText');
    const matchError = document.getElementById('matchError');
    const reqLength = document.getElementById('reqLength');
    const reqUppercase = document.getElementById('reqUppercase');

    // Resetear campos
    currentPasswordInput.value = '';
    newPasswordInput.value = '';
    confirmPasswordInput.value = '';

    // Resetear estado
    let passwordData = {
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
    };

    // Resetear UI
    strengthBar.style.width = '0';
    strengthBar.style.backgroundColor = '';
    strengthText.textContent = 'Ingresa tu nueva contraseña';
    matchError.style.display = 'none';
    confirmPasswordInput.style.borderColor = '';

    // Resetear indicadores de requisitos
    reqLength.classList.remove('valid');
    reqUppercase.classList.remove('valid');
}

// ===== FUNCIONALIDAD DE SUBIR IMÁGENES (SIN CAMBIOS) =====

// Elementos del DOM
const uploadArea = document.getElementById('uploadArea');
const uploadPlaceholder = document.getElementById('uploadPlaceholder');
const fileInput = document.getElementById('fileInput');
const imagePreview = document.getElementById('imagePreview');
const uploadStatus = document.getElementById('uploadStatus');
const statusText = document.getElementById('statusText');
const uploadBtn = document.getElementById('uploadBtn');
const cancelBtn = document.getElementById('cancelBtn');

// Función para procesar el archivo seleccionado
function processFile(file) {
    if (file && file.type.startsWith('image/')) {
        // Verificar tamaño (5MB máximo)
        if (file.size > 5 * 1024 * 1024) {
            alert('El archivo es demasiado grande. El tamaño máximo es 5 MB.');
            return;
        }

        // Actualizar el estado
        statusText.textContent = `Imagen seleccionada: ${file.name}`;
        uploadStatus.innerHTML = `
            <svg class="status-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                <polyline points="22 4 12 14.01 9 11.01"></polyline>
            </svg>
            <div class="status-text">Imagen seleccionada: ${file.name}</div>
        `;

        // Crear un objeto URL para la vista previa
        const imageUrl = URL.createObjectURL(file);

        // Actualizar la vista previa
        imagePreview.src = imageUrl;
        imagePreview.style.display = 'block';
        uploadPlaceholder.style.display = 'none';

        // Habilitar botones
        uploadBtn.disabled = false;
    } else {
        alert('Por favor, selecciona un archivo de imagen válido (JPG o PNG).');
    }
}

// Abrir selector de archivos al hacer clic en el área
uploadArea.addEventListener('click', () => fileInput.click());

// Procesar archivo seleccionado
fileInput.addEventListener('change', (e) => {
    const file = e.target.files[0];
    if (file) processFile(file);
});

// Eventos para arrastrar y soltar
uploadArea.addEventListener('dragover', (e) => {
    e.preventDefault();
    e.stopPropagation();
    uploadArea.classList.add('drag-over');
});

uploadArea.addEventListener('dragenter', (e) => {
    e.preventDefault();
    e.stopPropagation();
    uploadArea.classList.add('drag-over');
});

uploadArea.addEventListener('dragleave', (e) => {
    e.preventDefault();
    e.stopPropagation();
    uploadArea.classList.remove('drag-over');
});

uploadArea.addEventListener('drop', (e) => {
    e.preventDefault();
    e.stopPropagation();
    uploadArea.classList.remove('drag-over');

    const file = e.dataTransfer.files[0];
    if (file) processFile(file);
});

// Cancelar selección
cancelBtn.addEventListener('click', () => {
    // Resetear
    fileInput.value = '';
    imagePreview.style.display = 'none';
    uploadPlaceholder.style.display = 'flex';

    // Actualizar el estado
    uploadStatus.innerHTML = `
        <svg class="status-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="10"></circle>
            <line x1="12" y1="8" x2="12" y2="16"></line>
            <line x1="8" y1="12" x2="16" y2="12"></line>
        </svg>
        <div class="status-text">No has seleccionado ninguna imagen</div>
    `;

    // Deshabilitar botones
    uploadBtn.disabled = true;
});

// Simular subida
uploadBtn.addEventListener('click', () => {
    alert('Imagen subida correctamente.');

    // En un caso real, aquí harías una petición fetch
    // const formData = new FormData();
    // formData.append('profileImage', fileInput.files[0]);
    // fetch('/api/upload-profile-image', {
    //     method: 'POST',
    //     body: formData
    // });
});