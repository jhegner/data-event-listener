package br.com.app.lab.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitConfiguration {

    @Bean
    TopicExchange dmlExchange(@Value("${lab.rabbit.exchange}") String exchange) {
        return new TopicExchange(exchange, true, false);
    }

    @Bean
    Queue dmlQueue(@Value("${lab.rabbit.queue}") String queue) {
        return new Queue(queue, true);
    }

    @Bean
    Binding dmlBinding(Queue dmlQueue, TopicExchange dmlExchange,
                       @Value("${lab.rabbit.routing-key}") String routingKey) {
        return BindingBuilder.bind(dmlQueue).to(dmlExchange).with(routingKey);
    }

    @Bean
    MessageConverter rabbitMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
