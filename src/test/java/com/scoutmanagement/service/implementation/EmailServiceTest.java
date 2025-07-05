package com.scoutmanagement.service.implementation;

import com.scoutmanagement.util.exception.ServiceException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;

    @Test
    @DisplayName("Debería enviar correo HTML exitosamente")
    void deberiaEnviarCorreoHTMLExitosamente() throws MessagingException {
        String destinatario = "test@example.com";
        String asunto = "Test Subject";
        String cuerpoHTML = "<h1>Test HTML Content</h1>";

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.enviarCorreoHTML(destinatario, asunto, cuerpoHTML);

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);

        ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(messageCaptor.capture());
        assertThat(messageCaptor.getValue()).isEqualTo(mimeMessage);
    }

    @Test
    @DisplayName("Debería lanzar ServiceException cuando ocurre MessagingException al enviar")
    void deberiaLanzarServiceExceptionCuandoOcurreMessagingExceptionAlEnviar() throws MessagingException {
        String destinatario = "test@example.com";
        String asunto = "Test Subject";
        String cuerpoHTML = "<h1>Test HTML Content</h1>";

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException(new MessagingException("Error de conexión SMTP")))
                .when(mailSender).send(any(MimeMessage.class));

        assertThatThrownBy(() -> emailService.enviarCorreoHTML(destinatario, asunto, cuerpoHTML))
                .isInstanceOf(ServiceException.class)
                .hasMessage("Error al enviar el correo HTML");

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Debería manejar parámetros con caracteres especiales")
    void deberiaManejarParametrosConCaracteresEspeciales() throws MessagingException {
        String destinatario = "usuario@test.com";
        String asunto = "Asunto de prueba con acentos";
        String cuerpoHTML = "<html><body><h1>Contenido HTML</h1></body></html>";

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        assertThatCode(() -> emailService.enviarCorreoHTML(destinatario, asunto, cuerpoHTML))
                .doesNotThrowAnyException();

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Debería lanzar ServiceException cuando createMimeMessage falla")
    void deberiaLanzarServiceExceptionCuandoCreateMimeMessageFalla() {
        String destinatario = "test@example.com";
        String asunto = "Test";
        String cuerpoHTML = "<p>Test</p>";

        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Error al crear mensaje"));

        assertThatThrownBy(() -> emailService.enviarCorreoHTML(destinatario, asunto, cuerpoHTML))
                .isInstanceOf(ServiceException.class)
                .hasMessage("Error al enviar el correo HTML");

        verify(mailSender).createMimeMessage();
        verify(mailSender, never()).send((MimeMessage) any());
    }

    @Test
    @DisplayName("Debería lanzar ServiceException cuando ocurre cualquier RuntimeException")
    void deberiaLanzarServiceExceptionCuandoOcurreRuntimeException() {
        String destinatario = "test@example.com";
        String asunto = "Test";
        String cuerpoHTML = "<p>Test</p>";

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("Error genérico"))
                .when(mailSender).send(any(MimeMessage.class));

        assertThatThrownBy(() -> emailService.enviarCorreoHTML(destinatario, asunto, cuerpoHTML))
                .isInstanceOf(ServiceException.class)
                .hasMessage("Error al enviar el correo HTML");

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }
}