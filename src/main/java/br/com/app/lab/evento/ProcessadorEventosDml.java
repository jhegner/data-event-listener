package br.com.app.lab.evento;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class ProcessadorEventosDml {

    private static final Logger logger = LoggerFactory.getLogger(ProcessadorEventosDml.class);

    private final EventoPersistenciaRepository eventoRepository;
    private final RabbitTemplate rabbitTemplate;
    private final TransactionTemplate transactionTemplate;
    private final String exchange;
    private final String routingKey;

    public ProcessadorEventosDml(EventoPersistenciaRepository eventoRepository,
                                 RabbitTemplate rabbitTemplate,
                                 PlatformTransactionManager transactionManager,
                                 @Value("${lab.rabbit.exchange}") String exchange,
                                 @Value("${lab.rabbit.routing-key}") String routingKey) {
        this.eventoRepository = eventoRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void processarAposCommit(EventoDml evento) {
        transactionTemplate.executeWithoutResult(status -> eventoRepository.save(
                new EventoPersistencia(evento.operacao(), evento.entidade(),
                        evento.entidadeId(), evento.dados(), evento.ocorridoEm())));
        rabbitTemplate.convertAndSend(exchange, routingKey, evento);
        logger.info("Evento {} de {} {} publicado em {}:{}",
                evento.operacao(), evento.entidade(), evento.entidadeId(), exchange, routingKey);
    }
}
