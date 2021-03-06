package com.ultimatesoftware.workflow.messaging.kafka;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.Lifecycle;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;

/** Credit to Bikas Katwal
 * https://medium.com/@bikas.katwal10/start-stop-kafka-consumers-or-subscribe-to-new-topic-programmatically-using-spring-kafka-2d4fb77c9117
 * https://github.com/bkatwal/kafka-util/blob/master/src/main/java/com/bkatwal/kafka/util/KafkaConsumerUtil.java)

 * Potentially interesting: https://howtoprogram.xyz/2016/09/25/spring-kafka-multi-threaded-message-consumption/
 */
public class KafkaTopicContainerManager<K, V> implements TopicContainerManager {

    private final Logger LOGGER = LoggerFactory.getLogger(KafkaTopicContainerManager.class.getName());

    private final ConsumerFactory<K, V> factory;

    private final Map<String, ConcurrentMessageListenerContainer<K, V>> consumersMap =
            new HashMap<>();

    public KafkaTopicContainerManager(ConsumerFactory<K, V> factory) {
        this.factory = factory;
    }

    @Override
    public void createOrStartConsumers(Iterable<String> topics, Object listener) {
        topics.forEach(t -> createOrStartConsumer(t, listener, factory.getConfigurationProperties()));
    }

    @Override
    public void createOrStartConsumer(String topic, Object listener) {
        createOrStartConsumer(topic, listener, factory.getConfigurationProperties());
    }

    @Override
    public void stopConsumers(Iterable<String> topics) {
        topics.forEach(t -> stopConsumer(t));
    }

    @Override
    public void stopConsumer(String topic) {
        LOGGER.debug("stopping consumer for topic \"{}\"", topic);
        ConcurrentMessageListenerContainer<K, V> container = consumersMap.get(topic);
        if (container == null) {
            return;
        }
        container.stop();
        LOGGER.debug("consumer for topic \"{}\" stopped!!", topic);
    }

    @Override
    public Lifecycle getConsumer(String topic) {
        return consumersMap.get(topic);
    }

    private void createOrStartConsumer(String topic, Object messageListener, Map<String, Object> consumerConfig) {
        LOGGER.debug("creating kafka consumer for topic \"{}\"", topic);

        ConcurrentMessageListenerContainer<K, V> container = consumersMap.get(topic);

        if (container != null) {
            startConsumer(container, topic);
            return;
        }

        container = createConsumer(topic, messageListener, consumerConfig);

        try {
            container.start();
        } catch (Exception e) {
            LOGGER.warn("There was a problem starting a consumer for topic: \"{}\"", topic, e);
            return;
        }

        consumersMap.put(topic, container);

        LOGGER.debug("Created and started kafka consumer for topic \"{}\"", topic);
    }

    private void startConsumer(ConcurrentMessageListenerContainer<K, V> container, String topic) {
        if (container.isRunning()) {
            LOGGER.debug("Consumer for topic \"{}\" is already running.", topic);
            return;
        }

        LOGGER.debug("Consumer already created for topic \"{}\" starting consumer!!", topic);
        container.start();
        LOGGER.debug("Consumer for topic \"{}\" started!!!!", topic);
    }

    private ConcurrentMessageListenerContainer<K, V> createConsumer(String topic, Object messageListener, Map<String, Object> consumerConfig) {
        ConcurrentMessageListenerContainer<K, V> container;
        ContainerProperties containerProps = new ContainerProperties(topic);

        container = new ConcurrentMessageListenerContainer<>(factory, containerProps);
        container.setupMessageListener(messageListener);

        return container;
    }
}
