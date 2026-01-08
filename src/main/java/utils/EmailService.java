package utils;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailService {
    private static final String MY_EMAIL = "wiamhanafi21@gmail.com";
    private static final String MY_PASSWORD = "xvaqtjxdxvgyyymy"; 

    public static void envoyerEmailReparateur(String emailDestinataire, String nom, String motDePasseGenere) {
        
        // Configuration du serveur SMTP (Exemple pour Gmail)
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Authentification
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(MY_EMAIL, MY_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(MY_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailDestinataire));
            message.setSubject("Bienvenue sur AlloFix - Vos accès");

            // Contenu du mail
            String htmlContent = "<h1>Bonjour " + nom + " !</h1>"
                    + "<p>Votre compte réparateur a été créé.</p>"
                    + "<p>Voici votre mot de passe provisoire : <b>" + motDePasseGenere + "</b></p>"
                    + "<p>Cordialement,<br>L'équipe AlloFix.</p>";

            message.setContent(htmlContent, "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("Email envoyé à " + emailDestinataire);

        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Erreur d'envoi d'email : " + e.getMessage());
        }
    }
}