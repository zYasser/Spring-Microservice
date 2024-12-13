package com.microservice.inventory_service.grpc;

import com.microservice.grpc.CheckQuantityRequest;
import com.microservice.grpc.CheckQuantityResponse;
import com.microservice.grpc.InventoryServiceGrpc;
import com.microservice.inventory_service.entity.Product;
import com.microservice.inventory_service.repository.ProductRepository;
import com.netflix.discovery.EurekaClient;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@GrpcService
@Slf4j
public class GrpcProductService extends InventoryServiceGrpc.InventoryServiceImplBase
{
	private final ProductRepository productRepository;

	public GrpcProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	@Override
	public void checkQuantity(CheckQuantityRequest request, StreamObserver<CheckQuantityResponse> responseObserver) {
		log.info("Received Request");
		Optional<Product> result = productRepository.findById(request.getProductId());
		if (result.isEmpty()) {
			responseObserver.onError(
					Status.NOT_FOUND.withDescription("Product with this id does not exist").asRuntimeException());
		}
		if (result.get().getQuantity() >= request.getQuantity()) {
			responseObserver.onNext(CheckQuantityResponse.newBuilder().setIsAvailable(true).setProductId(
					request.getProductId()).build());
			responseObserver.onCompleted();
			return;
		}
		responseObserver.onNext(CheckQuantityResponse.newBuilder().setIsAvailable(false).setProductId(
				request.getProductId()).build());
		responseObserver.onCompleted();

	}
}
