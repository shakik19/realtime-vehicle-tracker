package bd.shakik.realtime.tracking.Config.Kafka;

import bd.shakik.avro.schemas.BusPosition;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class Producer {
	
	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServer;
	@Value("${spring.kafka.producer.properties.schema.registry.url}")
	private String schemaRegistryUrl;
	
	@Bean
	public Map<String, Object> avroProducerConfigs(){
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
		props.put("schema.registry.url", schemaRegistryUrl);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
		return props;
	}
	
	@Bean("busPositionKafkaTemplate")
	public KafkaTemplate<String, BusPosition> busPositionKafkaTemplate(){
		return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(avroProducerConfigs()));
	}
}
