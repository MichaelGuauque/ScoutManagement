package com.scoutmanagement.TestEmail;

import com.scoutmanagement.service.implementation.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import static org.mockito.Mockito.*;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;



@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

    private EmailService emailService;

    @BeforeEach
    void setUp() {
        emailService = new EmailService();

        ReflectionTestUtils.setField(emailService, "mailSender", mailSender);
    }

    @Test
    void testEnviarPasswordTemporal_mockeado() throws MessagingException {
        // Creamos un spy para interceptar solo el método que genera HTML
        EmailService spyEmailService = spy(emailService);

        // No queremos ejecutar thymeleaf real, devolvemos HTML falso
        doReturn("<html>Mock HTML</html>")
                .when(spyEmailService)
                .generarHtmlConThymeleaf("abc123");

        MimeMessage mensaje = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mensaje);

        // Ejecutamos el método que envía correo
        spyEmailService.enviarPasswordTemporal("user@correo.com", "abc123");

        // Verificamos que se envió
        verify(mailSender).send(mensaje);
    }

}


