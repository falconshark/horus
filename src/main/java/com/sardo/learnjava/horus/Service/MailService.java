package com.sardo.learnjava.horus.Service;

import java.util.Properties;

import java.io.UnsupportedEncodingException;
import javax.mail.MessagingException;

import javax.mail.Session;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Service
public class MailService {
    @Value("${email.smtp.server}")
    private String server;

    @Value("${email.smtp.port}")
    private String port;

    @Value("${email.smtp.username}")
    private String username;

    @Value("${email.smtp.password}")
    private String password;

    public void sendMail(String emailAddrss, String subject, String body) {
        Properties prop = new Properties();
        prop.setProperty("mail.smtp.host", server);
        prop.setProperty("mail.smtp.port", port);
        prop.setProperty("mail.smtp.auth", "true");
        
        // SMTPサーバへの認証とメールセッションの作成
        // ※メールセッション = メールの送信に関するパラメータや設定を保持
        String username = this.username;
        String password = this.password;
        Session session = Session.getInstance(prop, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        MimeMessage msg = new MimeMessage(session);
        try {
            String to = emailAddrss;
            String fromName = "Horus";
            String fromAddress = "horus@mail.sardo.work";

            msg.setRecipients(Message.RecipientType.TO, to);
            InternetAddress objFrm = new InternetAddress(fromAddress, fromName);
            msg.setFrom(objFrm);
            msg.setSubject(subject, "UTF-8");
            msg.setText(body, "UTF-8");
            Transport.send(msg);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
