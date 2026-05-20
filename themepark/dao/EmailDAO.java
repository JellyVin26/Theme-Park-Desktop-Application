package dao;

import java.io.InputStream;
import java.util.Properties;

public class EmailDAO {
    private final Properties props = new Properties();

    public EmailDAO() {
        try ( InputStream in = getClass()
                .getClassLoader()
                .getResourceAsStream("mail.properties") ) {
            if (in == null) {
                System.out.println("Sorry, unable to find email/mail.properties");
                return;
            }
            props.load(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean sendConfirmationEmail(String toEmail,
                                         String subject,
                                         String body,
                                         String replyToEmail) {
        // 1) create a mail Session with authentication
        javax.mail.Session session = javax.mail.Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new javax.mail.PasswordAuthentication(
                        props.getProperty("mail.username"),
                        props.getProperty("mail.password")
                );
            }
        });

        try {
            // 2) compose the message
            javax.mail.Message message = new javax.mail.internet.MimeMessage(session);
            message.setFrom(new javax.mail.internet.InternetAddress(props.getProperty("mail.from")));
            message.setRecipients(
                    javax.mail.Message.RecipientType.TO,
                    javax.mail.internet.InternetAddress.parse(toEmail)
            );
            message.setSubject(subject);
            message.setText(body);

            // 3) set Reply-To if provided
            if (replyToEmail != null && !replyToEmail.isBlank()) {
                message.setReplyTo(
                        javax.mail.internet.InternetAddress.parse(replyToEmail)
                );
            }

            // 4) send it
            javax.mail.Transport.send(message);
            System.out.println("Email sent successfully to " + toEmail);
            return true;

        } catch (javax.mail.MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }
}
