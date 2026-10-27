package com.skillstorm.skillstorm.service;

import com.skillstorm.skillstorm.exceptions.InvalidEmailException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;
import org.xbill.DNS.Record;
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender sender;
    private final static String OWNER = "tylerbrooklynkatsha@gmail.com";
    public boolean hasMXRecord(String email){
        try{
            String domain = email.substring(email.indexOf("@") + 1);

            Lookup lookup = new Lookup(domain, Type.MX);
            Record[] records = lookup.run();

            return records.length > 1;
        } catch (InvalidEmailException | TextParseException e){
            return false;
        }
    }

    @Async
    public void sendEmail(String email,String token) {



        var frontendUrl = String.format("http://localhost:5173/reset-password?token=%s&email=%s",token,email);
        var subject = "Password Reset Requested";
        var message = String.format("You have requested to reset your password. Please click the link below to set a new password:\n\n%s\n\nIf you did not request this, you can safely ignore this email.\n\nThis link will expire shortly.",frontendUrl);


        SimpleMailMessage mailMessage = new SimpleMailMessage();

        if("john.smith13@gmail.com".equals(email)) {
            mailMessage.setTo("tylerkatsha14@gmail.com");
        } else {
            mailMessage.setTo(email);
        }

        mailMessage.setReplyTo(OWNER);
        mailMessage.setFrom(OWNER);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);


        sender.send(mailMessage);
    }
}
