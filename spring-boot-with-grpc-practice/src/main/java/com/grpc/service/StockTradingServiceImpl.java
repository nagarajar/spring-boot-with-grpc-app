package com.grpc.service;

import com.grpc.entity.Stock;
import com.grpc.practice.*;
import com.grpc.repo.StockRepository;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

import java.time.Instant;
import java.util.Random;

@GrpcService
public class StockTradingServiceImpl extends StockTradingServiceGrpc.StockTradingServiceImplBase {
    private final StockRepository stockRepository;

    public StockTradingServiceImpl(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
    public void getStockPrice(StockRequest request, StreamObserver<StockResponse> responseObserver) {
        //Take stockName -> DB -> map response
        String stockSymbol = request.getStockSymbol();

        Stock stockEntity = stockRepository.findByStockSymbol(stockSymbol);

        StockResponse stockResponse = StockResponse.newBuilder()
                .setStockSymbol(stockEntity.getStockSymbol())
                .setPrice(stockEntity.getPrice())
                .setTimestamp(stockEntity.getLastUpdated().toString())
                .build();
        responseObserver.onNext(stockResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void subscribeStockPrice(StockRequest request, StreamObserver<StockResponse> responseObserver) {
        String symbol = request.getStockSymbol();
        try {
            for (int i = 0; i < 10; i++) {
                StockResponse stockResponse = StockResponse.newBuilder()
                        .setStockSymbol(symbol)
                        .setPrice(new Random().nextDouble(200))
                        .setTimestamp(Instant.now().toString())
                        .build();

                responseObserver.onNext(stockResponse);
                Thread.sleep(1000);
            }
            responseObserver.onCompleted();
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
    }

    @Override
    public StreamObserver<StockOrder> bulkStockOrder(StreamObserver<OrderSummary> responseObserver) {
        return new StreamObserver<StockOrder>() {
            private int totalOrder=0;
            private double totalAmount=0;
            private int successCount=0;
            @Override
            public void onNext(StockOrder stockOrder) {
                totalOrder++;
                totalAmount+=stockOrder.getPrice()*stockOrder.getQuantity() ;
                successCount++;
                System.out.println("Stock order received : "+stockOrder);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Error occurred while processing the stock order: "+throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                OrderSummary orderSummary =OrderSummary.newBuilder()
                        .setTotalOrder(totalOrder)
                        .setTotalAmount(totalAmount)
                        .setSuccessCount(successCount)
                        .build();
                responseObserver.onNext(orderSummary);
                responseObserver.onCompleted();
            }
        };
    }
}
