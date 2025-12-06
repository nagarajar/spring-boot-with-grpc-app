package com.grpc.client.service;

import com.grpc.practice.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.Random;

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
        System.out.println("--> Sending stock request from client: " + stockRequest);
        return serviceBlockingStub.getStockPrice(stockRequest);
    }

    // Server Streaming - calling
    public void subscribeStockPrice(String stockSymbol){
        StockRequest stockRequest = StockRequest.newBuilder()
                .setStockSymbol(stockSymbol)
                .build();
        System.out.println("--> Sending stock request from client: " + stockRequest);
        serviceAsyncStub.subscribeStockPrice(stockRequest, new StreamObserver<StockResponse>() {
            @Override
            public void onNext(StockResponse stockResponse) {
                System.out.println("<-- Received Stock price update: "+ stockResponse.getStockSymbol()
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
        // 1. Create/Prepare the response observer
        StreamObserver<OrderSummary> responseObserver = new StreamObserver<OrderSummary>() {
            @Override
            public void onNext(OrderSummary orderSummary) {
                System.out.println("<-- Order summary received from server: ");
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

        // 2. Send the prepared response observer
        StreamObserver<StockOrder> requestObserver = serviceAsyncStub.bulkStockOrder(responseObserver);

        // 3. Create/Prepare the request observer
        // Send multiple stream of stock order message/request
        try{

            for (int i=0; i<=10; i++) {
                StockOrder stockOrder = StockOrder.newBuilder()
                        .setOrderId("ORDER-" + i)
                        .setStockSymbol("AACL-" + i)
                        .setOrderType(i % 2 == 0 ? "BUY" : "SELL")
                        .setQuantity(new Random().nextInt(20))
                        .setPrice(new Random().nextDouble(200))
                        .build();
                System.out.println("--> Sending stock order request from client: " + stockOrder);
                requestObserver.onNext(stockOrder);
                Thread.sleep(1000);
            }
//            requestObserver.onNext(
//                    StockOrder.newBuilder()
//                            .setOrderId("1")
//                            .setStockSymbol("AAPL-1")
//                            .setOrderType("BUY")
//                            .setPrice(150.75)
//                            .setQuantity(10)
//                            .build()
//            );
//            requestObserver.onNext(
//                    StockOrder.newBuilder()
//                            .setOrderId("2")
//                            .setStockSymbol("AAPL-2")
//                            .setOrderType("SELL")
//                            .setPrice(126.75)
//                            .setQuantity(18)
//                            .build()
//            );
//            requestObserver.onNext(
//                    StockOrder.newBuilder()
//                            .setOrderId("3")
//                            .setStockSymbol("AAPL-3")
//                            .setOrderType("BUY")
//                            .setPrice(155.75)
//                            .setQuantity(13)
//                            .build()
//            );
//            requestObserver.onNext(
//                    StockOrder.newBuilder()
//                            .setOrderId("4")
//                            .setStockSymbol("AAPL-4")
//                            .setOrderType("SELL")
//                            .setPrice(175.75)
//                            .setQuantity(12)
//                            .build()
//            );
            // Done sending requests
            requestObserver.onCompleted();
        } catch (Exception e){
            requestObserver.onError(e);
        }

    }

    // Bidirectional Streaming - calling
    public void startLiveTrading(){
        // 1. Create/Prepare the response observer
        // Receive multiple stream of trade status message/response
        StreamObserver<TradeStatus> responseObserver = new StreamObserver<TradeStatus>() {
            @Override
            public void onNext(TradeStatus tradeStatus) {
                System.out.println("<-- Received the trade status from server: "+tradeStatus);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Error "+throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("Server stream completed ..!");
            }
        };
        // 2. Send the prepared response observer
        StreamObserver<StockOrder> requestObserver = serviceAsyncStub.liveTrading(responseObserver);

        // 3. Create/Prepare the request observer
        // Send multiple stream of stock order message/request
        try {
            for (int i=0; i<=10; i++){
                StockOrder stockOrder = StockOrder.newBuilder()
                        .setOrderId("ORDER-"+i)
                        .setStockSymbol("AACL-"+i)
                        .setOrderType(i%2==0 ? "BUY":"SELL")
                        .setQuantity(new Random().nextInt(20))
                        .setPrice(new Random().nextDouble(200))
                        .build();
                System.out.println("--> Sending stock order request from client: "+stockOrder);
                requestObserver.onNext(stockOrder);
                Thread.sleep(1000);
            }
            requestObserver.onCompleted();
        } catch (Exception ex){
            System.out.println("Error occurred while sending the stock order message: "+ex.getMessage());
        }
    }
}
