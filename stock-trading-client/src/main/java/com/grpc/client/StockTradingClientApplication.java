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
        System.out.println("**************** Unary grpc calling started *******************");
        System.out.println("<-- Received Server Response: "+stockClientService.getStockPrice("AAPL"));
        Thread.sleep(1000*2);
        System.out.println("**************** Unary grpc calling ended *******************");
        Thread.sleep(1000*5);
        System.out.println();

        System.out.println("**************** Server streaming - calling started *******************");
        // Server streaming grpc call
        stockClientService.subscribeStockPrice("AAPL");
        Thread.sleep(1000*15);
        System.out.println("**************** Server streaming - calling ended *******************");
        Thread.sleep(1000*5);
        System.out.println();

        System.out.println("**************** Client streaming - calling started *******************");
        // Client streaming grpc call
        stockClientService.placeBulkOrders();
        Thread.sleep(1000*15);
        System.out.println("**************** Client streaming - calling ended *******************");
        Thread.sleep(1000*5);
        System.out.println();

        System.out.println("**************** Bidirectional streaming - calling started *******************");
        // Bidirectional streaming grpc call
        stockClientService.startLiveTrading();
        Thread.sleep(1000*15);
        System.out.println("**************** Bidirectional streaming - calling ended *******************");
    }
}
