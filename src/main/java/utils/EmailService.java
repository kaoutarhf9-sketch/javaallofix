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
public static void envoyerEmailRecovery(String destinataire, String nouveauMdp) throws Exception {
        
        // 1. Configuration SMTP (pour Gmail)
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // 2. Création de la session
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(MY_EMAIL,  MY_PASSWORD);
            }
        });

        // 3. Création du message
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(MY_EMAIL, "AlloFix Service"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinataire));
        message.setSubject("AlloFix - Réinitialisation de mot de passe");

        String htmlContent = "<h3>Bonjour,</h3>"
                + "<p>Une demande de réinitialisation a été effectuée pour votre compte AlloFix.</p>"
                + "<p>Votre nouveau mot de passe temporaire est : <b>" + nouveauMdp + "</b></p>"
                + "<p>Merci de le changer dès votre connexion.</p>"
                + "<br><p>L'équipe AlloFix.</p>";

        message.setContent(htmlContent, "text/html; charset=utf-8");

        // 4. Envoi
        Transport.send(message);
        System.out.println("📧 Email envoyé à " + destinataire);
    }

public static void envoyerNotificationFinReparation(String destinataire, String nomClient, String codeClient, String appareil, double avance, double reste) {
    
    // 1. Config SMTP
    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.port", "587");

    Session session = Session.getInstance(props, new Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(MY_EMAIL, MY_PASSWORD);
        }
    });

    try {
        // 2. Création du message
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(MY_EMAIL, "AlloFix Service"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinataire));
        message.setSubject("AlloFix - Votre appareil est prêt ! ");

        // 3. Contenu HTML Pro
        String htmlContent = "<div style='font-family: Arial, sans-serif; color: #333; max-width: 600px;'>"
                + "<h2 style='color: #10b981;'>Bonne nouvelle " + nomClient + " !</h2>"
                + "<p>La réparation de votre appareil <b>" + appareil + "</b> est terminée.</p>"
                + "<div style='background-color: #f3f4f6; padding: 15px; border-radius: 8px; margin: 20px 0;'>"
                + "<p><strong>Code Client :</strong> " + codeClient + "</p>"
                + "<p><strong>Avance versée :</strong> " + String.format("%.2f", avance) + " Dh</p>"
                + "<p style='font-size: 18px;'><strong>Reste à payer : <span style='color: #ef4444;'>" + String.format("%.2f", reste) + " Dh</span></strong></p>"
                + "</div>"
                + "<p>Vous pouvez passer récupérer votre appareil en boutique dès aujourd'hui.</p>"
                + "<br><p>Cordialement,<br>L'équipe <b>AlloFix</b></p>"
                + "</div>";

        message.setContent(htmlContent, "text/html; charset=utf-8");

        // 4. Envoi
        Transport.send(message);
        System.out.println("Email de fin envoyé à " + destinataire);

    } catch (Exception e) {
        e.printStackTrace();
        System.err.println(" Erreur d'envoi mail : " + e.getMessage());
    }
}
}