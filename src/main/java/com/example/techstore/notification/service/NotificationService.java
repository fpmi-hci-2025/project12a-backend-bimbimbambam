package com.example.techstore.notification.service;

import com.example.techstore.order.event.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final JavaMailSender mailSender;

    @KafkaListener(topics = "order-placed", groupId = "techStoreGroup")
    public void listen(OrderPlacedEvent event) {
        log.info("Received notification request for order: {}", event.getOrderId());

        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("noreply@techstore.com");
            messageHelper.setTo(event.getEmail().toString());
            messageHelper.setSubject(String.format("Заказ №%s успешно оформлен", event.getOrderId()));

            String content = String.format("""
                    Здравствуйте, %s!
                                        
                    Ваш заказ успешно принят в обработку.
                                        
                    📦 Номер заказа: %d
                    💰 Сумма: %s BYN
                    🛒 Количество товаров: %d
                                        
                    Спасибо, что выбрали нас!
                    Команда TechStore.
                    """,
                    event.getFirstName(),
                    event.getOrderId(),
                    event.getTotalPrice(),
                    event.getItemsCount()
            );

            messageHelper.setText(content);
        };

        try {
            mailSender.send(messagePreparator);
            log.info("Email notification sent to {}", event.getEmail());
        } catch (MailException e) {
            log.error("Failed to send email to {}", event.getEmail(), e);
        }
    }
}
