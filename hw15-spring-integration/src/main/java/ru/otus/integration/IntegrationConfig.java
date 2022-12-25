package ru.otus.integration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.integration.scheduling.PollerMetadata;
import ru.otus.domain.Butterfly;
import ru.otus.domain.ButterflyEntity;
import ru.otus.domain.Chrysalis;
import ru.otus.service.BirdService;
import ru.otus.service.CaterpillarService;
import ru.otus.service.ChrysalisService;
import ru.otus.service.EggService;

import static java.util.concurrent.ThreadLocalRandom.current;

@Configuration
public class IntegrationConfig {

    private final EggService eggService;
    private final CaterpillarService caterpillarService;
    private final ChrysalisService chrysalisService;
    private final BirdService birdService;

    public IntegrationConfig(EggService eggService, CaterpillarService caterpillarService, ChrysalisService chrysalisService, BirdService birdService) {
        this.eggService = eggService;
        this.caterpillarService = caterpillarService;
        this.chrysalisService = chrysalisService;
        this.birdService = birdService;
    }

    @Bean
    public QueueChannel eggsChannel() {
        return MessageChannels.queue(10).get();
    }

    @Bean
    public PublishSubscribeChannel caterpillarChannel() {
        return MessageChannels.publishSubscribe().get();
    }

    @Bean
    public PublishSubscribeChannel chrysalisChannel() {
        return MessageChannels.publishSubscribe().get();
    }

    @Bean
    public PublishSubscribeChannel birdChannel() {
        return MessageChannels.publishSubscribe().get();
    }

    @Bean
    public PublishSubscribeChannel aliveButterfliesChannel() {
        return MessageChannels.publishSubscribe().get();
    }

    @Bean
    public PublishSubscribeChannel deadButterfliesChannel() {
        return MessageChannels.publishSubscribe().get();
    }

    @Bean
    public PublishSubscribeChannel readyButterflyChannel() {
        return MessageChannels.publishSubscribe().get();
    }

    @Bean
    public PublishSubscribeChannel butterflyChannel() {
        return MessageChannels.publishSubscribe().get();
    }

    @ServiceActivator(inputChannel = "birdChannel")
    public void birdEat(ButterflyEntity butterflyEntity) {
        birdService.eat(butterflyEntity);
    }

    @ServiceActivator(inputChannel = "deadButterfliesChannel")
    public void butterfliesDie(ButterflyEntity butterfly) {
        System.out.printf(">>>>>>>> Butterfly dies %s\n", butterfly);
    }

    @Bean(name = PollerMetadata.DEFAULT_POLLER)
    public PollerMetadata poller() {
        return Pollers.fixedRate(100).maxMessagesPerPoll(20).get();
    }

    @Bean
    public IntegrationFlow eggToCaterpillarFlow() {
        return IntegrationFlows.from(eggsChannel())
            .split()
            .handle(eggService, "hatch")
            .channel(caterpillarChannel())
            .get();
    }

    @Bean
    public IntegrationFlow caterpillarToChrysalisFlow() {
        return IntegrationFlows.from(caterpillarChannel())
            .handle(caterpillarService, "pupate")
            .<Chrysalis, Boolean>route(
                c -> current().nextBoolean(),
                mapping -> mapping
                    .subFlowMapping(true, sf -> sf.channel(chrysalisChannel()))
                    .subFlowMapping(false, sf -> sf.channel(birdChannel()).channel(deadButterfliesChannel()))
            )
            .get();
    }

    @Bean
    public IntegrationFlow chrysalisToButterflyFlow() {
        return IntegrationFlows.from(chrysalisChannel())
            .handle(chrysalisService, "transform")
            .<Butterfly>filter(b -> b.getSize() > 0, e -> e.discardChannel(deadButterfliesChannel()))
            .channel(aliveButterfliesChannel())
            .get();
    }

    @Bean
    public IntegrationFlow aliveButterfliesFlow() {
        return IntegrationFlows.from(aliveButterfliesChannel())
            .handle((GenericHandler<Butterfly>) (butterfly, headers) -> {
                System.out.printf(">>>>>>>> Butterfly is alive %s\n", butterfly);
                return butterfly;
            })
            .channel(readyButterflyChannel())
            .get();
    }

    @Bean
    public IntegrationFlow deadButterfliesFlow() {
        return IntegrationFlows.from(deadButterfliesChannel())
            .handle((GenericHandler<ButterflyEntity>) (butterfly, headers) -> {
                butterfly.setAlive(false);
                System.out.printf(">>>>>>>> Butterfly is dead %s\n", butterfly);
                return butterfly;
            })
            .channel(readyButterflyChannel())
            .get();
    }

    @Bean
    public IntegrationFlow butterfliesFlow() {
        return IntegrationFlows.from(readyButterflyChannel())
            .aggregate()
            .channel(butterflyChannel())
            .get();
    }
}
