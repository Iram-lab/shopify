package com.ecommerce.order.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean public NewTopic reserveStockTopic()     { return TopicBuilder.name("saga.reserve-stock").partitions(1).replicas(1).build(); }
    @Bean public NewTopic stockReplyTopic()        { return TopicBuilder.name("saga.stock-reply").partitions(1).replicas(1).build(); }
    @Bean public NewTopic processPaymentTopic()    { return TopicBuilder.name("saga.process-payment").partitions(1).replicas(1).build(); }
    @Bean public NewTopic paymentReplyTopic()      { return TopicBuilder.name("saga.payment-reply").partitions(1).replicas(1).build(); }
    @Bean public NewTopic sendNotificationTopic()  { return TopicBuilder.name("saga.send-notification").partitions(1).replicas(1).build(); }
}
