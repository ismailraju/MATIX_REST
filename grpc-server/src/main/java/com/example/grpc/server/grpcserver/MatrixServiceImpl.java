package com.example.grpc.server.grpcserver;


import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

@GrpcService
public class MatrixServiceImpl extends MatrixServiceGrpc.MatrixServiceImplBase {
    private static final Logger logger = LoggerFactory.getLogger(GrpcServerApplication.class);

    @Override
    public void addBlock(MatrixRequest request, StreamObserver<MatrixReply> reply) {
        System.out.println("Request received from client:\n" + request);
        int C00 = request.getA00() + request.getB00();
        int C01 = request.getA01() + request.getB01();
        int C10 = request.getA10() + request.getB10();
        int C11 = request.getA11() + request.getB11();
        MatrixReply response = MatrixReply.newBuilder().setC00(C00).setC01(C01).setC10(C10).setC11(C11).build();
        reply.onNext(response);
        reply.onCompleted();
    }

    @Override
    public void multiplyBlock(MatrixRequest request, StreamObserver<MatrixReply> reply) {
        System.out.println("Request received from client:\n" + request);
        int C00 = request.getA00() * request.getB00() + request.getA01() * request.getB10();
        int C01 = request.getA00() * request.getB01() + request.getA01() * request.getB11();
        int C10 = request.getA10() * request.getB00() + request.getA11() * request.getB10();
        int C11 = request.getA10() * request.getB01() + request.getA11() * request.getB11();
        MatrixReply response = MatrixReply.newBuilder().setC00(C00).setC01(C01).setC10(C10).setC11(C11).build();
        reply.onNext(response);
        reply.onCompleted();
    }

    //****modify*********
    //do sub task for multipication
    // received two list of integer and multiply them
    // return one list of integer
    @Override
    public void multiplySubBlock(MatrixMultiSubRequest request, StreamObserver<MatrixMultiSubReply> reply) {
        logger.info("Multiply Request received from client:\n"  );
        List<Integer> a = request.getAList();
        List<Integer> b = request.getBList();
        int row = request.getRow();
        int clm = request.getClm();
        Integer c = 0;
        for (int i = 0; i < a.size(); i++) {
            c += (a.get(i) * b.get(i));
        }
        MatrixMultiSubReply response = MatrixMultiSubReply.newBuilder()
                .setC(c)
                .setRow(row)
                .setClm(clm)
                .build();
        reply.onNext(response);
        reply.onCompleted();
    }

    //****modify*********
    //do sub task for addition
    // received two list of integer and add them
    // return one list of integer
    public void additionSubBlock(MatrixAddSubRequest request, StreamObserver<MatrixAddSubReply> reply) {
        logger.info("Addition Request received from client:\n"  );

        List<Integer> a = request.getAList();
        List<Integer> b = request.getBList();
        List<Integer> r = new ArrayList<Integer>();
        int row = request.getRow();
        Integer c = 0;
        for (int i = 0; i < a.size(); i++) {
            r.add (a.get(i) + b.get(i));
        }
        MatrixAddSubReply response = MatrixAddSubReply.newBuilder()
                .addAllA(r)
                .setRow(row)
                .build();
        reply.onNext(response);
        reply.onCompleted();
    }


}
