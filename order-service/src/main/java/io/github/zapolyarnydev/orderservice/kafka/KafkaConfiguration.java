package io.github.zapolyarnydev.orderservice.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfiguration {

    @Bean
    public NewTopic ordersPlacedTopic() {
        return TopicBuilder.name("orders.placed")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic ordersCancelTopic() {
        return TopicBuilder.name("orders.cancelled")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
