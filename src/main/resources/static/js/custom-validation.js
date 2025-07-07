/**
 * custom-validation.js
 * Versión con mensajes personalizados más cortos
 */

(function() {
    // Estilos CSS para los mensajes de error
    const styles = `
    .form-group {
      position: relative;
    }
    .error-message {
      color: #dc2626;
      font-size: 0.75rem;
      display: none;
      margin-top: 0.25rem; /* Espacio entre el campo y el mensaje de error */
    }
    
    .error-message.show {
      display: block;
    }
    
    input.error-highlight, select.error-highlight, textarea.error-highlight {
      border-color: #dc2626 !important;
      border-width: 1px !important;
    }
  `;

    // Mensajes personalizados por tipo de validación
    const customMessages = {
        valueMissing: "Campo obligatorio",
        typeMismatch: {
            email: "Por favor ingrese un correo válido",
            url: "URL inválida",
            tel: "Teléfono inválido",
            default: "Formato incorrecto"
        },
        patternMismatch: "Formato incorrecto",
        tooLong: "Texto demasiado largo",
        tooShort: "Texto demasiado corto",
        rangeUnderflow: "El documento debe tener entre 7 y 11 dígitos.",
        rangeOverflow: "El documento debe tener entre 7 y 11 dígitos.",
        stepMismatch: "Valor no permitido",
        badInput: "Valor inválido",
        default: "Campo inválido"
    };

    // Obtener mensaje personalizado según el tipo de error
    function getCustomMessage(element) {
        const validity = element.validity;

        // Verificar cada tipo de error
        if (validity.valueMissing) {
            return customMessages.valueMissing;
        }

        if (validity.typeMismatch) {
            const type = element.getAttribute('type');
            return customMessages.typeMismatch[type] || customMessages.typeMismatch.default;
        }

        if (validity.patternMismatch) {
            // Si hay un mensaje personalizado en el atributo data-error-pattern, usarlo
            return element.getAttribute('data-error-pattern') || customMessages.patternMismatch;
        }

        if (validity.tooLong) {
            return customMessages.tooLong;
        }

        if (validity.tooShort) {
            return customMessages.tooShort;
        }

        if (validity.rangeUnderflow) {
            return customMessages.rangeUnderflow;
        }

        if (validity.rangeOverflow) {
            return customMessages.rangeOverflow;
        }

        if (validity.stepMismatch) {
            return customMessages.stepMismatch;
        }

        if (validity.badInput) {
            return customMessages.badInput;
        }

        // Mensaje por defecto
        return customMessages.default;
    }

    // Inyectar estilos en el documento
    function injectStyles() {
        const styleElement = document.createElement('style');
        styleElement.textContent = styles;
        document.head.appendChild(styleElement);
    }

    // Crear o encontrar un elemento de mensaje de error para un campo
    function createErrorMessage(element) {
        const formGroup = element.closest('.form-group'); // Buscar el .form-group más cercano
        if (!formGroup) {
            console.error('No se encontró un .form-group para el elemento:', element);
            return null; // O manejar el error de otra manera
        }

        let errorElement = formGroup.querySelector('.error-message');

        if (!errorElement) {
            errorElement = document.createElement('div');
            errorElement.className = 'error-message';

            const inputContainer = element.closest('.input-container');
            if (inputContainer) {
                // Si hay un input-container, insertar después de él
                inputContainer.after(errorElement);
            } else {
                // Si no, insertar después del propio elemento (input/select)
                element.after(errorElement);
            }
        }
        return errorElement;
    }

    // Mostrar mensaje de error
    function showErrorMessage(element) {
        const errorElement = createErrorMessage(element);
        if (!errorElement) return; // Salir si no se pudo crear el elemento de error

        const message = getCustomMessage(element);
        errorElement.textContent = message;
        errorElement.classList.add('show');
        element.classList.add('error-highlight');

        // Handler para ocultar el error al escribir
        element._hideErrorHandler = function() {
            hideErrorMessage(element);
            element.removeEventListener('input', element._hideErrorHandler);
        };
        element.addEventListener('input', element._hideErrorHandler);
    }

    // Ocultar mensaje de error
    function hideErrorMessage(element) {
        const formGroup = element.closest('.form-group');
        if (!formGroup) return; // Salir si no se encuentra el form-group

        const errorElement = formGroup.querySelector('.error-message');
        if (errorElement) {
            errorElement.classList.remove('show');
            element.classList.remove('error-highlight');
        }
    }

    // Interceptar la validación nativa del navegador
    function setupFormValidationInterception() {
        // Sobrescribir el método de validación nativo
        HTMLFormElement.prototype.reportValidity = (function(originalFunction) {
            return function() {
                // Obtener todos los elementos inválidos
                const invalidElements = Array.from(this.elements).filter(el => !el.validity.valid);

                // Si hay elementos inválidos, mostrar mensaje para el primero
                if (invalidElements.length > 0) {
                    const firstInvalid = invalidElements[0];
                    showErrorMessage(firstInvalid);
                    firstInvalid.focus();
                    return false;
                }

                // Si todo es válido, continuar con el comportamiento normal
                return originalFunction.apply(this, arguments);
            };
        })(HTMLFormElement.prototype.reportValidity);

        // Sobrescribir el método setCustomValidity
        const originalSetCustomValidity = HTMLInputElement.prototype.setCustomValidity;
        HTMLInputElement.prototype.setCustomValidity = function(message) {
            originalSetCustomValidity.call(this, message);
            if (message) {
                // Si hay un mensaje personalizado, usarlo directamente
                const errorElement = createErrorMessage(this);
                errorElement.textContent = message;
                errorElement.classList.add('show');
                this.classList.add('error-highlight');
            } else {
                hideErrorMessage(this);
            }
        };

        // Interceptar el evento invalid
        document.addEventListener('invalid', function(e) {
            e.preventDefault();
            showErrorMessage(e.target);
        }, true);
    }

    // Inicializar
    function init() {
        injectStyles();
        setupFormValidationInterception();
        console.log('Custom validation messages initialized');
    }

    // Ejecutar cuando el DOM esté listo
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();