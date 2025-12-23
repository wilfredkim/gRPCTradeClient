package com.grpc.wilfred.grpctradeclient;

import com.grpc.wilfred.grpctradeclient.service.StockClientService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GRpcTradeClientApplication implements CommandLineRunner {
    private final StockClientService stockClientService;

    public GRpcTradeClientApplication(StockClientService stockClientService) {
        this.stockClientService = stockClientService;
    }

    public static void main(String[] args) {
        SpringApplication.run(GRpcTradeClientApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Grpc Client Says::::::::::::::::::"+ stockClientService.getStockPrice("NYUNDO"));
    }
}
