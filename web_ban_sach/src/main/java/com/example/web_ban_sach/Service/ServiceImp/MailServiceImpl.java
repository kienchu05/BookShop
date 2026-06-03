package com.example.web_ban_sach.Service.ServiceImp;

import com.example.web_ban_sach.Service.IService.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {
    private JavaMailSender mailSender;
    public MailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendMail(String from ,String to, String subject, String content) {
        //MimeMailMessage => co dinh kem media
        //SimpleMailMessage => noi dung thong thuong
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        //Gui email
        mailSender.send(message);
    }
}
