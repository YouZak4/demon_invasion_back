package com.demon_invasion.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;

    public void envoyerCodeVerification(String email, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject("Demon invasion - votre code de vérification");
        message.setText("Votre code de vérification est : " + code
                + "\nIl expire dans 15 minutes.");
        mailSender.send(message);
    }

    public void envoyerAlerteCompteExistant(String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject("Demon invasion - tentative d'inscription");
        message.setText("Quelqu'un a tenté de créer un compte Demon invasion avec votre adresse e-mail,"
                + " mais un compte existe déjà pour cette adresse."
                + "\nSi c'est vous, connectez-vous simplement avec votre identifiant habituel."
                + "\nSi ce n'est pas vous, vous pouvez ignorer ce message : aucun compte n'a été créé.");
        mailSender.send(message);
    }
}
