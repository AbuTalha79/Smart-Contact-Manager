package com.smart.smartcontactmanager.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
    public boolean sendEmail(String subject, String message,String to){
        //rest of the code


        boolean f= false;

        String from = "mohdtalha@mail.com" ;

        //variable for email
        String host = "smtp.gmail.com" ;

        //get the system properties
        Properties properties = System.getProperties();
        System.out.println("PROPERTIES " +properties);


        //setting important information to properties object

        //host set
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");


        // stet1: to get the session object...
        Session session = Session.getInstance(properties, new Authenticator() {
            
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("abutalha2079@gmail.com", "A@talha79");
            }
        });

        session.setDebug(true);


        //Step 2 : compose the message [text , multi media]
        

        try {
            MimeMessage m = new MimeMessage(session);
            // from mail
            m.setFrom(from);

            // adding recipient to message
            m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // adding subject to message
            m.setText(message);

            //send

            //step 3 : send the message using Transport class
            Transport.send(m);

            System.out.println("Sent Success.....");
            f=true;




        } catch (Exception e) {
            e.printStackTrace();
        }
        return f;
    }
}
