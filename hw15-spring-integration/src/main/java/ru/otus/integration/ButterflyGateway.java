package ru.otus.integration;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import ru.otus.domain.ButterflyEntity;
import ru.otus.domain.Egg;

import java.util.List;

@MessagingGateway
public interface ButterflyGateway {

    @Gateway(requestChannel = "eggsChannel", replyChannel = "butterflyChannel")
    List<ButterflyEntity> transform(List<Egg> eggs);
}
