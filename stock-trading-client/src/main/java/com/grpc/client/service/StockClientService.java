package com.grpc.client.service;

import com.grpc.practice.StockRequest;
import com.grpc.practice.StockResponse;
import com.grpc.practice.StockTradingServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class StockClientService {

    @GrpcClient("stockService")
    private StockTradingServiceGrpc.StockTradingServiceBlockingStub serviceBlockingStub;

    public StockResponse getStockPrice(String stockSymbol){
        StockRequest stockRequest = StockRequest.newBuilder()
                .setStockSymbol(stockSymbol)
                .build();
        return serviceBlockingStub.getStockPrice(stockRequest);
    }
}
