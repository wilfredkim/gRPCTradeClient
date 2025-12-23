package com.grpc.wilfred.grpctradeclient.service;

import com.wilfred.grpc.StockRequest;
import com.wilfred.grpc.StockResponse;
import com.wilfred.grpc.StockTradingServiceGrpc;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.stereotype.Service;

@Service
public class StockClientService {
    private final StockTradingServiceGrpc.StockTradingServiceBlockingStub stockTradingServiceBlockingStub;

    public StockClientService(GrpcChannelFactory channelFactory) {
        var channel = channelFactory.createChannel("stockService");
        this.stockTradingServiceBlockingStub = StockTradingServiceGrpc.newBlockingStub(channel);
    }

    public StockResponse getStockPrice(String stockSymbol) {
        StockRequest request = StockRequest.newBuilder().setStockSymbol(stockSymbol).build();
        return stockTradingServiceBlockingStub.getStockPrice(request);
    }
}
