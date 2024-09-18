package com.microservice.order_service.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.order_service.dto.BaseResponse;
import com.microservice.order_service.dto.OrderDto;
import com.microservice.order_service.dto.Product;
import com.microservice.order_service.feignClient.InventoryClient;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InventoryFallbackFactory implements FallbackFactory<InventoryClient>
{

	@Override
	public InventoryClient create(Throwable cause) {

		log.info("Exception occurred", cause);  // Log the exception

		return new InventoryClient()
		{

			@Override
			public ResponseEntity<Product> getStock(String id) {

				// Provide a meaningful fallback response for getStock
				log.error("Error occurred while fetching stock for product {}: {}", id, cause.getMessage());
				return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
						.body(null); // Fallback with null product or a default value
			}

			@Override
			public ResponseEntity<BaseResponse<Boolean>> reserveProduct(OrderDto order) {
				if (cause instanceof FeignException.BadRequest) {
					String message = getMessageFromException(cause);
					if (message == null) {
						return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
								.body(new BaseResponse<>(false, "Something went wrong, please try again",
								                         HttpStatus.INTERNAL_SERVER_ERROR.value()));

					}
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body(new BaseResponse<>(false, message,
							                         HttpStatus.BAD_REQUEST.value()));
				} else if (cause instanceof FeignException.NotFound) {
					String message = getMessageFromException(cause);
					if (message == null) {
						return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
								.body(new BaseResponse<>(false, "Something went wrong, please try again",
								                         HttpStatus.INTERNAL_SERVER_ERROR.value()));

					}
					return ResponseEntity.status(HttpStatus.NOT_FOUND)
							.body(new BaseResponse<>(false, message,
							                         HttpStatus.NOT_FOUND.value()));



				} else {
					// Handle all other exceptions
					log.error("Error occurred: {}", cause.getMessage());
					return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
							.body(new BaseResponse<>(false, "Service Unavailable: " + cause.getMessage(),
							                         HttpStatus.SERVICE_UNAVAILABLE.value()));
				}
			}
		};
	}

	private String getMessageFromException(Throwable exception) {
		FeignException feignException = (FeignException) exception;
		String responseBody = feignException.contentUTF8();
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(responseBody);
			String message = jsonNode.get("message").asText();
			return message;

		} catch (JsonProcessingException e) {
			log.error("Error parsing response body", e);
			return null;
		}
	}
}
