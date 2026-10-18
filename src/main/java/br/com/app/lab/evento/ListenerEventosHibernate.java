package br.com.app.lab.evento;

import br.com.app.lab.produto.Produto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ListenerEventosHibernate implements PostInsertEventListener, PostUpdateEventListener,
        PostDeleteEventListener {

    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;

    public ListenerEventosHibernate(ObjectMapper objectMapper,
                                    ApplicationEventPublisher eventPublisher) {
        this.objectMapper = objectMapper;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        capturar("INSERT", event.getEntity(), event.getId());
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        capturar("UPDATE", event.getEntity(), event.getId());
    }

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        capturar("DELETE", event.getEntity(), event.getId());
    }

    @Override
    public boolean requiresPostCommitHandling(EntityPersister persister) {
        return false;
    }

    private void capturar(String operacao, Object entity, Object id) {
        if (!(entity instanceof Produto produto)) {
            return;
        }

        Map<String, Object> dados = new LinkedHashMap<>();
        dados.put("id", id);
        dados.put("nome", produto.getNome());
        dados.put("preco", produto.getPreco());

        EventoDml mensagem = new EventoDml(operacao, Produto.class.getSimpleName(),
                String.valueOf(id), serializar(dados), Instant.now());
        eventPublisher.publishEvent(mensagem);
    }

    private String serializar(Map<String, Object> dados) {
        try {
            return objectMapper.writeValueAsString(dados);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Nao foi possivel serializar os dados do evento Hibernate", exception);
        }
    }
}
