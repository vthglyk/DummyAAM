package eu.h2020.symbiote.dummyAAM;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author RuggenthalerC
 *
 *         Main entry point to start spring boot application.
 */
@EnableAutoConfiguration
@SpringBootApplication
public class DummyAAM {

	@Value("${rabbit.host}")
	private String rabbitHost;

	@Value("${rabbit.username}")
	private String rabbitUsername;

	@Value("${rabbit.password}")
	private String rabbitPassword;

	public static void main(String[] args) {
		SpringApplication.run(DummyAAM.class, args);
    }

	@Bean
	public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
//        factory.setConcurrentConsumers(3);
//        factory.setMaxConcurrentConsumers(10);
		factory.setMessageConverter(jackson2JsonMessageConverter());
		return factory;
	}

	@Bean
	Jackson2JsonMessageConverter jackson2JsonMessageConverter() {

		Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();

		/**
		 * It is necessary to register the GeoJsonModule, otherwise the GeoJsonPoint cannot
		 * be deserialized by Jackson2JsonMessageConverter.
		 */
		// ObjectMapper mapper = new ObjectMapper();
		// mapper.registerModule(new GeoJsonModule());
		// converter.setJsonObjectMapper(mapper);
		return converter;
	}


	@Bean
	public ConnectionFactory connectionFactory() throws Exception {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rabbitHost);
		// connectionFactory.setPublisherConfirms(true);
		// connectionFactory.setPublisherReturns(true);
		connectionFactory.setUsername(rabbitUsername);
		connectionFactory.setPassword(rabbitPassword);
		return connectionFactory;
	}

	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter jackson2JsonMessageConverter) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter);
		return rabbitTemplate;
	}

}
