package com.HerWell.example.Service;

import com.HerWell.example.Data.UserQuery;
import com.HerWell.example.Reposistry.UserQueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class UserQueryService {

    @Autowired
    private UserQueryRepository queryRepository;

    @Autowired
    private JavaMailSender mailSender;

    // Save query + send email notification
    public UserQuery submitQuery(UserQuery userQuery) {
        // 1. Save in DB
        UserQuery savedQuery = queryRepository.save(userQuery);

        // 2. Send email
        sendEmailNotification(savedQuery);

        return savedQuery;
    }

    private void sendEmailNotification(UserQuery query) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("youremail@example.com"); // replace with your admin email
        message.setSubject("New User Query Submitted");
        message.setText("A new query has been submitted by user: " + query.getEmail() +
                "\n\nQuery:\n" + query.getQueryText() +
                "\n\nSubmitted at: " + query.getCreatedAt());

        mailSender.send(message);
    }
}
