// Esperar a que el DOM esté completamente cargado
document.addEventListener('DOMContentLoaded', function() {
    let passwordValidation = {
        length: false,
        uppercase: false,
        number: false,
        match: false
    };

    function validatePassword(password) {
        const newPasswordInput = document.getElementById('newPassword');

        // Validar longitud (6-15 caracteres)
        const lengthValid = password.length >= 6 && password.length <= 15;
        passwordValidation.length = lengthValid;
        updateRequirement('length', lengthValid);

        // Validar mayúscula
        const uppercaseValid = /[A-Z]/.test(password);
        passwordValidation.uppercase = uppercaseValid;
        updateRequirement('uppercase', uppercaseValid);

        // Validar número
        const numberValid = /\d/.test(password);
        passwordValidation.number = numberValid;
        updateRequirement('number', numberValid);

        // Actualizar barra de progreso
        updatePasswordStrengthBar();

        // Actualizar clase del input
        const allValid = lengthValid && uppercaseValid && numberValid;
        if (password.length > 0) {
            if (allValid) {
                newPasswordInput.classList.remove('invalid');
                newPasswordInput.classList.add('valid');
            } else {
                newPasswordInput.classList.remove('valid');
                newPasswordInput.classList.add('invalid');
            }
        } else {
            newPasswordInput.classList.remove('valid', 'invalid');
        }

        // Validar coincidencia de contraseñas si ya hay algo en confirmar
        validatePasswordMatch();

        // Actualizar estado del botón
        updateSubmitButton();
    }

    function updatePasswordStrengthBar() {
        const progressBar = document.querySelector('.password-strength-progress');
        if (!progressBar) return;

        const validRequirements = [passwordValidation.length, passwordValidation.uppercase, passwordValidation.number].filter(Boolean).length;
        const totalRequirements = 3; // length, uppercase, number

        const percentage = (validRequirements / totalRequirements) * 100;
        progressBar.style.width = `${percentage}%`;

        // Cambiar color según el progreso
        if (percentage === 100) {
            progressBar.style.backgroundColor = '#10b981'; // Verde
        } else if (percentage >= 66) {
            progressBar.style.backgroundColor = '#f59e0b'; // Amarillo
        } else if (percentage >= 33) {
            progressBar.style.backgroundColor = '#ef4444'; // Rojo
        } else {
            progressBar.style.backgroundColor = '#d1d5db'; // Gris
        }
    }

    function updateRequirement(requirement, isValid) {
        const footerElement = document.querySelector(`[data-requirement="${requirement}-footer"]`);

        if (footerElement) {
            footerElement.classList.remove('valid', 'invalid');
            footerElement.classList.add(isValid ? 'valid' : 'invalid');
        }
    }

    function validatePasswordMatch() {
        const newPassword = document.getElementById('newPassword').value;
        const confirmPassword = document.getElementById('confirmPassword').value;
        const confirmPasswordInput = document.getElementById('confirmPassword');
        const matchMessage = document.getElementById('passwordMatchMessage');

        // Solo validar si ambos campos tienen contenido
        if (confirmPassword.length > 0 && newPassword.length > 0) {
            const passwordsMatch = newPassword === confirmPassword;
            passwordValidation.match = passwordsMatch;

            if (passwordsMatch) {
                confirmPasswordInput.classList.remove('invalid');
                confirmPasswordInput.classList.add('valid');
                if (matchMessage) {
                    matchMessage.classList.remove('show', 'invalid');
                    matchMessage.classList.add('valid');
                    matchMessage.textContent = 'Las contraseñas coinciden';
                    matchMessage.classList.add('show');
                }
            } else {
                confirmPasswordInput.classList.remove('valid');
                confirmPasswordInput.classList.add('invalid');
                if (matchMessage) {
                    matchMessage.classList.remove('valid');
                    matchMessage.classList.add('invalid', 'show');
                    matchMessage.textContent = 'Las contraseñas no coinciden';
                }
            }
        } else {
            // Si no hay contenido en confirmPassword, no mostrar error pero no marcar como válido
            passwordValidation.match = (confirmPassword.length === 0) ? false : passwordValidation.match;
            confirmPasswordInput.classList.remove('valid', 'invalid');
            if (matchMessage) {
                matchMessage.classList.remove('show');
            }
        }

        updateSubmitButton();
    }

    function updateSubmitButton() {
        const submitBtn = document.getElementById('submitBtn');
        if (!submitBtn) return;

        const currentPassword = document.getElementById('currentPassword').value;
        const newPassword = document.getElementById('newPassword').value;
        const confirmPassword = document.getElementById('confirmPassword').value;

        // Verificar que todas las validaciones pasen
        const allValidationsPass = passwordValidation.length &&
            passwordValidation.uppercase &&
            passwordValidation.number &&
            passwordValidation.match;

        // Verificar que todos los campos estén llenos
        const allFieldsFilled = currentPassword.length > 0 &&
            newPassword.length > 0 &&
            confirmPassword.length > 0;

        // Verificar que las contraseñas coincidan exactamente
        const passwordsMatch = newPassword === confirmPassword && newPassword.length > 0;

        // El botón se habilita solo si todo está correcto
        if (allValidationsPass && allFieldsFilled && passwordsMatch) {
            submitBtn.disabled = false;
            submitBtn.style.opacity = '1';
            submitBtn.style.cursor = 'pointer';
        } else {
            submitBtn.disabled = true;
            submitBtn.style.opacity = '0.6';
            submitBtn.style.cursor = 'not-allowed';
        }
    }

    function resetPasswordForm() {
        const form = document.querySelector('.password-form');
        if (form) {
            form.reset();
        }

        // Limpiar clases de validación
        document.querySelectorAll('.inputContraseña').forEach(input => {
            input.classList.remove('valid', 'invalid');
        });

        // Limpiar requisitos
        document.querySelectorAll('.requirement').forEach(req => {
            req.classList.remove('valid', 'invalid');
        });

        // Ocultar mensaje de coincidencia
        const matchMessage = document.getElementById('passwordMatchMessage');
        if (matchMessage) {
            matchMessage.classList.remove('show', 'valid', 'invalid');
        }

        // Resetear barra de progreso
        const progressBar = document.querySelector('.password-strength-progress');
        if (progressBar) {
            progressBar.style.width = '0%';
            progressBar.style.backgroundColor = '#d1d5db';
        }

        // Resetear validaciones
        passwordValidation = {
            length: false,
            uppercase: false,
            number: false,
            match: false
        };

        // Deshabilitar botón
        const submitBtn = document.getElementById('submitBtn');
        if (submitBtn) {
            submitBtn.disabled = true;
            submitBtn.style.opacity = '0.6';
            submitBtn.style.cursor = 'not-allowed';
        }
    }

    // FUNCIÓN CORREGIDA: No prevenir el envío del formulario
    function handleSubmit(event) {
        // NO usar preventDefault() aquí
        const currentPassword = document.getElementById('currentPassword').value;
        const newPassword = document.getElementById('newPassword').value;
        const confirmPassword = document.getElementById('confirmPassword').value;

        // Validación final antes de enviar
        if (newPassword !== confirmPassword) {
            event.preventDefault(); // Solo prevenir si hay error
            alert('Las contraseñas no coinciden');
            return false;
        }

        // Validar que todas las validaciones pasen
        const allValidationsPass = passwordValidation.length &&
            passwordValidation.uppercase &&
            passwordValidation.number &&
            passwordValidation.match;

        if (!allValidationsPass) {
            event.preventDefault(); // Solo prevenir si hay error
            alert('La contraseña no cumple con todos los requisitos');
            return false;
        }

        // Si todo está correcto, permitir que el formulario se envíe normalmente
        console.log('Formulario enviándose al servidor...');
        return true;
    }

    // Agregar eventos para validar en tiempo real
    const currentPasswordInput = document.getElementById('currentPassword');
    const newPasswordInput = document.getElementById('newPassword');
    const confirmPasswordInput = document.getElementById('confirmPassword');
    const passwordForm = document.querySelector('.password-form');

    if (currentPasswordInput) {
        currentPasswordInput.addEventListener('input', function() {
            updateSubmitButton();
        });
    }

    if (newPasswordInput) {
        newPasswordInput.addEventListener('input', function() {
            validatePassword(this.value);
        });
    }

    if (confirmPasswordInput) {
        confirmPasswordInput.addEventListener('input', function() {
            validatePasswordMatch();
        });
    }

    // EVENTO SUBMIT CORREGIDO
    if (passwordForm) {
        passwordForm.addEventListener('submit', handleSubmit);
    }

    // Hacer las funciones globales para que puedan ser llamadas desde HTML
    window.resetPasswordForm = resetPasswordForm;
    window.handleSubmit = handleSubmit;
    window.validatePassword = validatePassword;
    window.validatePasswordMatch = validatePasswordMatch;
});

// CÓDIGO PARA EL FORMULARIO DE IMAGEN DE PERFIL
// Elementos del DOM para la imagen
const uploadArea = document.getElementById('uploadArea');
const uploadPlaceholder = document.getElementById('uploadPlaceholder');
const fileInput = document.getElementById('fileInput');
const imagePreview = document.getElementById('imagePreview');
const uploadStatus = document.getElementById('uploadStatus');
const statusText = document.getElementById('statusText');

// Seleccionar botones por clase ya que no tienen ID
const cancelBtn = document.querySelector('.btn-secondary');
const uploadBtn = document.querySelector('.btn-primary');

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

        // Habilitar botón de actualizar
        if (uploadBtn) {
            uploadBtn.disabled = false;
            uploadBtn.style.opacity = '1';
            uploadBtn.style.cursor = 'pointer';
        }
    } else {
        alert('Por favor, selecciona un archivo de imagen válido (JPG o PNG).');
    }
}

// Función para resetear el formulario
function resetImageForm() {
    // Resetear input de archivo
    if (fileInput) {
        fileInput.value = '';
    }

    // Ocultar vista previa y mostrar placeholder
    if (imagePreview) {
        imagePreview.style.display = 'none';
        imagePreview.src = '';
    }

    if (uploadPlaceholder) {
        uploadPlaceholder.style.display = 'flex';
    }

    // Resetear el estado del upload
    if (uploadStatus) {
        uploadStatus.innerHTML = `
              <svg class="status-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <circle cx="12" cy="12" r="10"></circle>
                  <line x1="12" y1="8" x2="12" y2="16"></line>
                  <line x1="8" y1="12" x2="16" y2="12"></line>
              </svg>
              <div class="status-text">No has seleccionado ninguna imagen</div>
          `;
    }

    // Deshabilitar botón de actualizar
    if (uploadBtn) {
        uploadBtn.disabled = true;
        uploadBtn.style.opacity = '0.5';
        uploadBtn.style.cursor = 'not-allowed';
    }
}

// Event listeners solo si los elementos existen
if (uploadArea) {
    // Abrir selector de archivos al hacer clic en el área
    uploadArea.addEventListener('click', () => {
        if (fileInput) fileInput.click();
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
}

// Procesar archivo seleccionado
if (fileInput) {
    fileInput.addEventListener('change', (e) => {
        const file = e.target.files[0];
        if (file) processFile(file);
    });
}

// BOTÓN DE CANCELAR - Funcionalidad principal
if (cancelBtn) {
    cancelBtn.addEventListener('click', (e) => {
        e.preventDefault(); // Prevenir cualquier comportamiento por defecto
        resetImageForm();
    });
}

// Botón de actualizar/guardar
if (uploadBtn) {
    // Inicialmente deshabilitar el botón
    uploadBtn.disabled = true;
    uploadBtn.style.opacity = '0.5';
    uploadBtn.style.cursor = 'not-allowed';

    // No necesita event listener ya que el formulario se enviará al controlador
    // El botón solo se habilita/deshabilita según si hay imagen seleccionada
}