package com.grpc.client.service;

import com.grpc.practice.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class StockClientService {

    @GrpcClient("stockService")
    private StockTradingServiceGrpc.StockTradingServiceBlockingStub serviceBlockingStub;

    @GrpcClient("stockService")
    private StockTradingServiceGrpc.StockTradingServiceStub serviceAsyncStub;

    // UNARY - RPC - calling
    public StockResponse getStockPrice(String stockSymbol){
        StockRequest stockRequest = StockRequest.newBuilder()
                .setStockSymbol(stockSymbol)
                .build();
        return serviceBlockingStub.getStockPrice(stockRequest);
    }

    // Server Streaming - calling
    public void subscribeStockPrice(String stockSymbol){
        StockRequest stockRequest = StockRequest.newBuilder()
                .setStockSymbol(stockSymbol)
                .build();
        serviceAsyncStub.subscribeStockPrice(stockRequest, new StreamObserver<StockResponse>() {
            @Override
            public void onNext(StockResponse stockResponse) {
                System.out.println("Stock price update: "+ stockResponse.getStockSymbol()
                +" Price: "+ stockResponse.getPrice()
                +" Time: "+ stockResponse.getTimestamp());
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Error: "+throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("Stock price stream live update completed.");
            }
        });
    }

    // Client Streaming - calling
    public void placeBulkOrders(){
        StreamObserver<OrderSummary> responseObserver = new StreamObserver<OrderSummary>() {
            @Override
            public void onNext(OrderSummary orderSummary) {
                System.out.println("Order summary received from server: ");
                System.out.println("Total orders: "+orderSummary.getTotalOrder());
                System.out.println("Successful orders: "+orderSummary.getSuccessCount());
                System.out.println("Total amount: $"+orderSummary.getTotalAmount());

            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Order summary received error from server: "+throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("Stream completed, server is done sending summary..! ");
            }
        };
        StreamObserver<StockOrder> requestObserver = serviceAsyncStub.bulkStockOrder(responseObserver);

        // Send multiple stream of stock order message/request
        try{
            requestObserver.onNext(
                    StockOrder.newBuilder()
                            .setOrderId("1")
                            .setStockSymbol("AAPL-1")
                            .setOrderType("BUY")
                            .setPrice(150.75)
                            .setQuantity(10)
                            .build()
            );
            requestObserver.onNext(
                    StockOrder.newBuilder()
                            .setOrderId("2")
                            .setStockSymbol("AAPL-2")
                            .setOrderType("SELL")
                            .setPrice(126.75)
                            .setQuantity(18)
                            .build()
            );
            requestObserver.onNext(
                    StockOrder.newBuilder()
                            .setOrderId("3")
                            .setStockSymbol("AAPL-3")
                            .setOrderType("BUY")
                            .setPrice(155.75)
                            .setQuantity(13)
                            .build()
            );
            requestObserver.onNext(
                    StockOrder.newBuilder()
                            .setOrderId("4")
                            .setStockSymbol("AAPL-4")
                            .setOrderType("SELL")
                            .setPrice(175.75)
                            .setQuantity(12)
                            .build()
            );
            // Done sending requests
            requestObserver.onCompleted();
        } catch (Exception e){
            requestObserver.onError(e);
        }

    }
}
