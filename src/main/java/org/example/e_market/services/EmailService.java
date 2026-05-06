package org.example.e_market.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Async
    public void sendMail(String to, String subject, String template, Map<String, Object> templateModel) throws MessagingException {

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);

        messageHelper.setTo(to);
        messageHelper.setSubject(subject);

        Context context = new Context();
        context.setVariables(templateModel);

        String htmlContent = templateEngine.process(template, context);

        messageHelper.setText(htmlContent, true);

        System.out.println("===============sending mail...===========");
        mailSender.send(mimeMessage);
        System.out.println("===============mail sent==============");


    }
}
