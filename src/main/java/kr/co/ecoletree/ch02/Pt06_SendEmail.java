/*****************************************************************
 * Copyright (c) 2017 EcoleTree. All Rights Reserved.
 *
 * Author : HyungSeok Kim
 * Create Date : 2022. 02. 16.
 * File Name : Pt06_SendEmail.java
 * DESC : Send E-mail with SimpleJavaMail Lib.
 *****************************************************************/
package kr.co.ecoletree.ch02;

import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.AsyncResponse;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

import java.util.List;

import lombok.Data;

public class Pt06_SendEmail {

    private static AsyncResponse sendMail(final EmailInfo email, final EmailAuth auth) {
        final String from = email.getSender();
        final String to = email.getReceiver();
        final String cc = email.getCc().stream().findFirst().orElse(null);
        final String title = email.getSubject();
        final String content = email.getContent();

        final String from_name = email.getSenderName();
        final String to_name = email.getReceiverName();

        final String sender = auth.getAccount();
        final String password = auth.getPassword();
        final String host = auth.getHost();
        final int port = auth.getPort();

        Email mail = EmailBuilder.startingBlank()
                .from(from_name, from)
                .to(to_name, to)
                .ccMultiple(cc) // 다중 참조
                .withSubject(title)
                .withPlainText(content)
                .buildEmail();

        Mailer mailer = MailerBuilder
                .withSMTPServer(host, port, sender, password)
                .withTransportStrategy(TransportStrategy.SMTP)
                .async()
                .buildMailer();

        return mailer.sendMail(mail, true);
    }

    @Data
    static class EmailInfo {
        private String receiver;
        private String receiverName;
        private String sender;
        private String senderName;
        private List<String> cc;
        private String subject;
        private String content;
    }

    @Data
    static class EmailAuth {
        private String host;
        private int port;
        private String account;
        private String password;
    }
}
