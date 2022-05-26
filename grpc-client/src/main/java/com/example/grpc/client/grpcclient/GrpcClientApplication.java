package com.example.grpc.client.grpcclient;

import com.example.grpc.server.grpcserver.HelloRequest;
import com.example.grpc.server.grpcserver.HelloResponse;
import com.example.grpc.server.grpcserver.HelloServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@SpringBootApplication
@Configuration
public class GrpcClientApplication extends SpringBootServletInitializer {

    private static final String CHILD = "GrpcClient-";
    private static final Logger logger = LoggerFactory.getLogger(GrpcClientApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(GrpcClientApplication.class, args);
        /**
         ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
         .usePlaintext()
         .build();

         HelloServiceGrpc.HelloServiceBlockingStub stub
         = HelloServiceGrpc.newBlockingStub(channel);

         HelloResponse helloResponse = stub.hello(HelloRequest.newBuilder()
         .setFirstName("Baeldung")
         .setLastName("gRPC")
         .build());

         System.out.println(helloResponse);

         channel.shutdown();

         ManagedChannel channel1 = ManagedChannelBuilder.forAddress("localhost", 8080)
         .usePlaintext()
         .build();

         HelloServiceGrpc.HelloServiceBlockingStub stub1
         = HelloServiceGrpc.newBlockingStub(channel1);

         HelloResponse helloResponse1 = stub1.hello(HelloRequest.newBuilder()
         .setFirstName("Baeldung")
         .setLastName("gRPC")
         .build());
         System.out.println("8080: "+helloResponse1);
         channel1.shutdown();
         **/
//		SpringApplication.run(GrpcClientApplication.class, args);
    }

}
