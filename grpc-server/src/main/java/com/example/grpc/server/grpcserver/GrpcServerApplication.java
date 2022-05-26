package com.example.grpc.server.grpcserver;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.IOException;
import java.util.concurrent.Executor;

@SpringBootApplication
public class GrpcServerApplication extends SpringBootServletInitializer {
    private static final Logger logger = LoggerFactory.getLogger(GrpcServerApplication.class);


    @Autowired
    @Qualifier("taskExecutor")
    private static Executor taskExecutor;
    public static void main(String[] args) throws IOException, InterruptedException {
        SpringApplication.run(GrpcServerApplication.class, args);

//        Server server = ServerBuilder
//                .forPort(8080)
//                .executor(taskExecutor)
//                .addService(new HelloServiceImpl()).build();
//
//        server.start();
//        server.awaitTermination();
    }



}
