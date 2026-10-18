package br.com.app.lab.config;

import br.com.app.lab.evento.ListenerEventosHibernate;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateEventListenerConfig {

    @Bean
    HibernatePropertiesCustomizer hibernatePropertiesCustomizer(ListenerEventosHibernate listener) {
        return properties -> properties.put("hibernate.integrator_provider",
                (org.hibernate.jpa.boot.spi.IntegratorProvider) () -> java.util.List.of(
                        new org.hibernate.integrator.spi.Integrator() {
                    @Override
                    public void integrate(org.hibernate.boot.Metadata metadata,
                                          org.hibernate.engine.spi.SessionFactoryImplementor sessionFactory,
                                          org.hibernate.service.spi.SessionFactoryServiceRegistry serviceRegistry) {
                        EventListenerRegistry registry = serviceRegistry.getService(EventListenerRegistry.class);
                        registry.getEventListenerGroup(EventType.POST_INSERT).appendListener(listener);
                        registry.getEventListenerGroup(EventType.POST_UPDATE).appendListener(listener);
                        registry.getEventListenerGroup(EventType.POST_DELETE).appendListener(listener);
                    }

                    @Override
                    public void disintegrate(org.hibernate.engine.spi.SessionFactoryImplementor sessionFactory,
                                             org.hibernate.service.spi.SessionFactoryServiceRegistry serviceRegistry) {
                    }
                }));
    }
}
