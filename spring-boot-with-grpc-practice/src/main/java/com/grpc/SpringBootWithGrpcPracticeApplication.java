package com.grpc;

import com.grpc.entity.Stock;
import com.grpc.repo.StockRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringBootWithGrpcPracticeApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootWithGrpcPracticeApplication.class, args);
	}

    @Bean
    public CommandLineRunner dataInitializer(StockRepository stockRepository) {
        return args -> {
            stockRepository.save(new Stock("AAPL", 175.50));
            stockRepository.save(new Stock("GOOGL", 2800.75));
            stockRepository.save(new Stock("AMZN", 3400.00));
        };
    }

    /*
        CREATE TABLE stock (
            id INT AUTO_INCREMENT PRIMARY KEY,
            stock_symbol VARCHAR(10) UNIQUE NOT NULL,
            price DOUBLE PRECISION NOT NULL,
            last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
        );

        INSERT INTO stocks (stock_symbol, price) VALUES ('AAPL', 175.50);
        INSERT INTO stocks (stock_symbol, price) VALUES ('GOOGL', 2800.75);
        INSERT INTO stocks (stock_symbol, price) VALUES ('AMZN', 3400.00);

        SELECT * FROM stocks;

        ALTER TABLE stocks
        ADD COLUMN currency VARCHAR(3) NOT NULL DEFAULT 'USD';
     */

}
