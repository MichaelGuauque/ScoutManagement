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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;


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

        EmailService spyEmailService = spy(emailService);


        doReturn("<html>Mock HTML</html>")
                .when(spyEmailService)
                .generarHtmlConThymeleaf("abc123");

        MimeMessage mensaje = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mensaje);


        spyEmailService.enviarPasswordTemporal("user@correo.com", "abc123");


        verify(mailSender).send(mensaje);
    }

    @Test
    void testGenerarHtmlConThymeleaf_conMotorReal() {

        TemplateEngine realEngine = new TemplateEngine();
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setCharacterEncoding("UTF-8");

        realEngine.setTemplateResolver(resolver);
        ReflectionTestUtils.setField(emailService, "templateEngine", realEngine);


        String password = "clavePrueba123";
        String html = emailService.generarHtmlConThymeleaf(password);

        assertNotNull(html);
        assertTrue(html.contains(password));
        assertTrue(html.contains("http://localhost:8080"));
    }

}


