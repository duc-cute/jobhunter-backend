package vn.hoidanit.jobhunter.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    private final MailSender mailSender;

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine springTemplateEngine;

    public EmailService(MailSender mailSender, JavaMailSender javaMailSender, SpringTemplateEngine springTemplateEngine) {
        this.mailSender = mailSender;
        this.javaMailSender = javaMailSender;
        this.springTemplateEngine = springTemplateEngine;
    }


    public  void sendEmail() {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo("20211841@eaut.edu.vn");
        msg.setSubject("Testing");
        msg.setText("Hello word from spring");
        this.mailSender.send(msg);

    }

    public void sendMailSync(String to,String subject,String content,boolean isMultipart,boolean isHtml) {
        MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage,isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content,isHtml);
            this.javaMailSender.send(mimeMessage);


        }  catch (MailException | MessagingException e) {
            System.out.println("ERROR SEND EMAIL" + e);
        }

    }

    public void sendMailFromTemplateSync(String to,String subject,String templateName) {
        Context context = new Context();
        String content = springTemplateEngine.process(templateName,context);
        this.sendMailSync(to,subject,content,false,true);

    }
}
