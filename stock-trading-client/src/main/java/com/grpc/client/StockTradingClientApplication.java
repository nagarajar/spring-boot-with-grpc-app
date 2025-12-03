package com.grpc.client;

import com.grpc.client.service.StockClientService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StockTradingClientApplication implements CommandLineRunner {

    private StockClientService stockClientService;

    public StockTradingClientApplication(StockClientService stockClientService) {
        this.stockClientService = stockClientService;
    }

	public static void main(String[] args) {
		SpringApplication.run(StockTradingClientApplication.class, args);
	}

    @Override
    public void run(String... args) throws Exception {
        // Unary grpc calling
        System.out.println("**************** Unary grpc calling *******************");
        System.out.println("Server Response: "+stockClientService.getStockPrice("AAPL"));

        System.out.println("**************** Server streaming - calling *******************");
        // Server streaming grpc call
        stockClientService.subscribeStockPrice("AAPL");
    }
}
