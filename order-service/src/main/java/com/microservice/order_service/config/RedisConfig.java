package com.microservice.order_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig
{

	@Bean
	public LettuceConnectionFactory redisConnectionFactory() {

		return new LettuceConnectionFactory(new RedisStandaloneConfiguration("localhost", 6379));
	}
	@Bean
	RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {

		RedisTemplate<String, Object> template = new RedisTemplate<>();
		// Use String serializer for keys
		template.setKeySerializer(new StringRedisSerializer());
		// Use JSON serializer for values
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

		template.setConnectionFactory(connectionFactory);
		return template;
	}

}
