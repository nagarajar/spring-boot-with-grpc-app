package com.grpc.client.service;

import com.grpc.practice.StockRequest;
import com.grpc.practice.StockResponse;
import com.grpc.practice.StockTradingServiceGrpc;
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
                +"Time: "+ stockResponse.getTimestamp());
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
}
