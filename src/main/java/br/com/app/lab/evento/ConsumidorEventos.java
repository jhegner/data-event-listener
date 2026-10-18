package br.com.app.lab.evento;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ConsumidorEventos {

    private final MensagemRecebidaRepository mensagemRepository;

    public ConsumidorEventos(MensagemRecebidaRepository mensagemRepository) {
        this.mensagemRepository = mensagemRepository;
    }

    @RabbitListener(queues = "${lab.rabbit.queue}")
    public void receber(EventoDml evento) {
        mensagemRepository.save(new MensagemRecebida(evento.operacao(), evento.entidade(),
                evento.entidadeId(), evento.dados(), evento.ocorridoEm(), Instant.now()));
    }
}
