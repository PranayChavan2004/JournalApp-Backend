package com.projectbyPranayChavan.JournalApp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    public void sendEmail(String to, String subject, String body) {

        try {
            // to send the mail to particular mail id
            SimpleMailMessage mail = new SimpleMailMessage();

            // This are the properties required to set to send mail like sending this mail to this id, subject and body of mail
            mail.setTo(to);
            mail.setSubject(subject);
            mail.setText(body);

            javaMailSender.send(mail);

            log.info("Email sent successfully to: {}", to);

        } catch (Exception e) {
            log.error("Exception occurred while sending email to: {}", to, e);
        }
    }
}