package com.scoutmanagement.service.implementation;

import com.scoutmanagement.util.exception.ServiceException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async("mailTaskExecutor")
    public void enviarCorreo(String destinatario, String asunto, String cuerpo) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destinatario);
        mensaje.setSubject(asunto);
        mensaje.setText(cuerpo);

        mailSender.send(mensaje);
    }

    @Async("mailTaskExecutor")
    public void enviarCorreoHTML(String destinatario, String asunto, String cuerpoHTML) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(cuerpoHTML, true);

            mailSender.send(mensaje);
        } catch (MessagingException e) {
            throw new ServiceException("Error al enviar el correo HTML", e);
        }
    }


    public String generarTemplateRecuperacionPassword(String nuevaPassword, String nombreUsuario) {
        return """
    <!DOCTYPE html>
    <html lang="es">
    <head>
      <meta charset="UTF-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
      <link href="https://fonts.googleapis.com/css2?family=Figtree:wght@300;400;500;600;700&display=swap" rel="stylesheet">
      <title>Recuperar Contraseña</title>
      <style>
        /* Estilos CSS integrados para mejor compatibilidad */
        body {
          margin: 0;
          padding: 0;
          font-family: Figtree, Arial, sans-serif;
          color: #333333 !important;
          background-color: #f7f7f7;
        }
    
        .email-container {
          background-color: #ffffff;
          margin: 0 auto;
          border-radius: 8px;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
          max-width: 600px;
        }
    
        .header {
          background-color: #622599;
          padding: 30px;
          text-align: center;
          border-radius: 8px 8px 0 0;
        }
    
        .header h1 {
          color: #ffffff !important;
          margin: 0;
          font-size: 24px;
          font-weight: 600;
        }
    
        .content {
          padding: 30px;
        }
    
        .content p {
          margin-top: 0;
          font-size: 16px;
          line-height: 1.5;
          color: #333333 !important;
        }
    
        .temp-password-box {
          background-color: #f5f5f5;
          border-left: 4px solid #622599;
          padding: 15px;
          margin: 25px 0;
        }
    
        .temp-password-box p {
          margin: 0;
          color: #333333 !important;
        }
    
        .temp-password-title {
          font-size: 16px;
          font-weight: bold;
          color: #333333 !important;
        }
    
        .temp-password-value {
          margin: 10px 0 0;
          font-family: 'Courier New', Courier, monospace;
          font-size: 18px;
          font-weight: bold;
          color: #622599 !important;
        }
    
        .instructions {
          font-size: 16px;
          line-height: 1.5;
          margin-bottom: 25px;
          color: #333333 !important;
        }
    
        .instructions li {
          color: #333333 !important;
          margin-bottom: 5px;
        }
    
        .button-container {
          margin: 30px 0;
          text-align: center;
        }
    
        .reset-button {
          background-color: #424243;
          color: #ffffff !important;
          padding: 12px 30px;
          text-decoration: none;
          border-radius: 4px;
          font-weight: bold;
          display: inline-block;
        }
    
        .reset-button:hover {
          background-color: #333334;
          color: #ffffff !important;
        }
    
        .footer {
          background-color: #f5f5f5;
          padding: 20px;
          text-align: center;
          font-size: 14px;
          color: #666666 !important;
          border-radius: 0 0 8px 8px;
        }
    
        .footer p {
          margin: 0;
          color: #666666 !important;
        }
    
        /* Forzar colores específicos */
        * {
          color: inherit;
        }
    
        a {
          color: #622599 !important;
        }
    
        strong {
          color: #333333 !important;
        }
      </style>
    </head>
    <body>
      <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" border="0">
        <tr>
          <td style="padding: 20px 0;">
            <table align="center" role="presentation" width="600" cellspacing="0" cellpadding="0" border="0" class="email-container">
              <!-- Header -->
              <tr>
                <td class="header">
                  <h1>Recuperar Contraseña - Scout Management</h1>
                </td>
              </tr>
    
              <!-- Content -->
              <tr>
                <td class="content">
                  <p style="color: #333333 !important;">Hola %s,</p>
    
                  <p style="color: #333333 !important;">Hemos recibido una solicitud para restablecer la contraseña de tu cuenta en Scout Management. Si no solicitaste este cambio, puedes ignorar este correo de forma segura.</p>
    
                  <div class="temp-password-box">
                    <p class="temp-password-title">Tu Contraseña Temporal</p>
                    <p class="temp-password-value">%s</p>
                  </div>
    
                  <p style="color: #333333 !important;">Para restablecer tu contraseña, sigue estos pasos:</p>
    
                  <ol class="instructions">
                    <li style="color: #333333 !important;">Ingresa a nuestro sitio web</li>
                    <li style="color: #333333 !important;">Ingresa tu dirección de correo electrónico</li>
                    <li style="color: #333333 !important;">Ingresa la contraseña temporal proporcionada anteriormente</li>
                    <li style="color: #333333 !important;">Sigue las instrucciones para establecer una contraseña nueva y segura.</li>
                  </ol>
    
                  <p style="color: #333333 !important;"><strong style="color: #333333 !important;">Importante:</strong> Por tu seguridad, te recomendamos mucho que cambies tu contraseña después de iniciar sesión por primera vez.</p>
    
                  <div class="button-container">
                    <a href="#" class="reset-button">Restablecer Contraseña</a>
                  </div>
    
                  <p style="color: #333333 !important; margin-bottom: 0;">Atentamente,<br>El Equipo Scout Jaguares 605</p>
                </td>
              </tr>
    
              <!-- Footer -->
              <tr>
                <td class="footer">
                  <p>&copy; 2025 Jaguares 605 - Todos los derechos reservados.</p>
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </body>
    </html>
    """.formatted(nombreUsuario, nuevaPassword);
    }


}

