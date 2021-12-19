import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.Random;


// kolla på hur vi gjorde inan måns har din kod!
public class EmailService {
    private static final int MAX_REQUEST_AGE = 300000;
    private static final String HOST = "pop.gmail.com";
    private static final String SERVER_EMAIL = "jtestemu@gmail.com";
    private static final String SERVER_EMAIL_PW = "azsx1234";
    DBHandler dbHandler = new DBHandler();



    public void forgotPassword(UserDB userDB) {
        //Generate token
        String newPassword = generateRandomString();
        //Set password to user in db
        userDB.setPassword(newPassword);
        dbHandler.updateUser(userDB);

        //Send email
        sendPassword(userDB.getUsername(), newPassword);
    }

    //From https://www.baeldung.com/java-random-string
    public String generateRandomString() {
        int leftLimit = 97; // letter 'a' // kolla på hur du kan generera lösenord
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        System.out.println("[EmailService] Generated new password: " + generatedString);

        return generatedString;
    }

    private void sendPassword(String user, String newPassword) {
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "25");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.socketFactory.port", "25");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(SERVER_EMAIL, SERVER_EMAIL_PW);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SERVER_EMAIL));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(user)
            );
            message.setSubject("SmartHouse: New password");
            message.setText("Hello!\n" +
                    "Here is your new password: " + newPassword);
            Transport.send(message);

            System.out.println("[EmailService] Sent password to " + user);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
