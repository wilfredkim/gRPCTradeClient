package com.grpc.wilfred.grpctradeclient.service;

import com.wilfred.grpc.*;
import net.devh.boot.grpc.client.inject.GrpcClient;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

@Service
public class StockClientService {
    @GrpcClient("stockService")
    private StockTradingServiceGrpc.StockTradingServiceStub stockTradingServiceBlockingStub;

    /*public StockResponse getStockPrice(String stockSymbol) {
        StockRequest request = StockRequest.newBuilder().setStockSymbol(stockSymbol).build();
        try {
            StockResponse response = stockTradingServiceBlockingStub.getStockPrice(request);

            System.out.println("Received stock price: " + response.getStockSymbol() +
                    " Price: " + response.getPrice() +
                    " Timestamp: " + response.getTimestamp());

            return response;
        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
            throw e;
        }
    }*/

    public void subscribeStockPrice(String stockSymbol) {
        StockRequest request = StockRequest.newBuilder().setStockSymbol(stockSymbol).build();
        stockTradingServiceBlockingStub.subscribeStockPrice(request, new StreamObserver<>() {
            @Override
            public void onError(Throwable throwable) {
                System.out.println("Error occurred: " + throwable.getMessage());

            }

            @Override
            public void onCompleted() {
                System.out.println("Subscription completed.");
            }

            @Override
            public void onNext(StockResponse stockResponse) {
                System.out.println("Received stock update: " + stockResponse.getStockSymbol() +
                        " Price: " + stockResponse.getPrice() +
                        " Timestamp: " + stockResponse.getTimestamp());

            }
        });
    }

    public void placeOrders() {
        StreamObserver<OrderSummary> responseObserver = new StreamObserver<OrderSummary>() {
            @Override
            public void onNext(OrderSummary summary) {
                System.out.println("Order Summary Received from Server:");
                System.out.println("Total Orders: " + summary.getTotalOrders());
                System.out.println("Successful Orders: " + summary.getSuccessCount());
                System.out.println("Total Amount: KES " + summary.getTotalAmount());
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Order Summary Receivedn error from Server:" + throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("Stream completed , server is done sending summary !");
            }
        };
        StreamObserver<StockOrder> stockOrderStreamObserver = stockTradingServiceBlockingStub.bulkStockOrder(responseObserver);
        //send multiple stock orders!!
        try {
            stockOrderStreamObserver.onNext(
                    StockOrder.newBuilder()
                            .setOrderId("ORD1")
                            .setStockSymbol("AAPL")
                            .setQuantity(10)
                            .setPrice(150.0)
                            .build());
            stockOrderStreamObserver.onNext(
                    StockOrder.newBuilder()
                            .setOrderId("ORD2")
                            .setStockSymbol("ORG")
                            .setQuantity(12)
                            .setPrice(205)
                            .build());
            stockOrderStreamObserver.onNext(
                    StockOrder.newBuilder()
                            .setOrderId("ORD3")
                            .setStockSymbol("MELON")
                            .setQuantity(22)
                            .setPrice(60)
                            .build());
            stockOrderStreamObserver.onCompleted();
        } catch (Exception e) {
            e.printStackTrace();
            stockOrderStreamObserver.onError(e);
        }
    }

    public void liveStockTrading() throws InterruptedException {
        StreamObserver<StockOrder> requestObserver = stockTradingServiceBlockingStub.liveStockTrading(new StreamObserver<TradeStatus>() {
            @Override
            public void onNext(TradeStatus tradeStatus) {
                System.out.println("Server ::: Trade Status Update: Order ID: " + tradeStatus.getOrderId() +
                        " Status: " + tradeStatus.getMessage() +
                        " Timestamp: " + tradeStatus.getTimestamp());
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Error occurred in live trading: " + throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("Live trading session completed.");
            }
        });
        //sending multiple stock orders from client side
        //sending multiple order request from client

        for (int i = 1; i <= 10; i++) {
            StockOrder stockOrder = StockOrder.newBuilder()
                    .setOrderId("ORDER-" + i)
                    .setStockSymbol("APPL")
                    .setQuantity(i * 10)
                    .setPrice(150.0 + i)
                    .setOrderType("BUY")
                    .build();
            requestObserver.onNext(stockOrder);
            Thread.sleep(500);
        }
        requestObserver.onCompleted();
    }
}
