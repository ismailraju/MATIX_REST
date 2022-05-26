package com.example.grpc.client.grpcclient;

import com.example.grpc.client.grpcclient.payload.ServerCountSummary;
import com.example.grpc.server.grpcserver.*;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.NameResolver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class GRPCClientService {

    private static final Logger logger = LoggerFactory.getLogger(GRPCClientService.class);
    private ExecutorService executorService;
    public static ManagedChannel channelAsync = null;
    @Value("${grpc-server.list}")
    String serverListStr;


    private ManagedChannel getChannelAsync() {

        if (channelAsync == null) {
//            NameResolver.Factory nameResolverFactory = new MultiAddressNameResolverFactory(
//                    new InetSocketAddress("localhost", 9090),
//                    new InetSocketAddress("localhost", 9190),
//                    new InetSocketAddress("localhost", 9290)
//            );

            NameResolver.Factory nameResolverFactory = new MultiAddressNameResolverFactory(serverListStr);

            channelAsync = ManagedChannelBuilder.forTarget("service")
                    .nameResolverFactory(nameResolverFactory)
                    .defaultLoadBalancingPolicy("round_robin")
                    .executor(getExecutorService())
                    .usePlaintext()
                    .build();



        }

        return channelAsync;
    }

    //******modification***************
    // grpc client call through this  ExecutorService which have 8 thread.
    // so, At a time 8  grpc-server call can be done
    private ExecutorService getExecutorService() {
        if (executorService == null) {
            executorService = Executors.newFixedThreadPool(8);
        }
        return executorService;
    }

    public String ping() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();
        PingPongServiceGrpc.PingPongServiceBlockingStub stub
                = PingPongServiceGrpc.newBlockingStub(channel);
        PongResponse helloResponse = stub.ping(PingRequest.newBuilder()
                .setPing("")
                .build());
        channel.shutdown();
        return helloResponse.getPong();
    }

    public String add() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();
        MatrixServiceGrpc.MatrixServiceBlockingStub stub
                = MatrixServiceGrpc.newBlockingStub(channel);
        MatrixReply A = stub.addBlock(MatrixRequest.newBuilder()
                .setA00(1)
                .setA01(2)
                .setA10(5)
                .setA11(6)
                .setB00(1)
                .setB01(2)
                .setB10(5)
                .setB11(6)
                .build());
        String resp = A.getC00() + A.getC01() + A.getC10() + A.getC11() + "";
        return resp;
    }

    public String multiply() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();
        MatrixServiceGrpc.MatrixServiceBlockingStub stub
                = MatrixServiceGrpc.newBlockingStub(channel);
        MatrixReply A = stub.multiplyBlock(MatrixRequest.newBuilder()
                .setA00(1)
                .setA01(2)
                .setA10(5)
                .setA11(6)
                .setB00(2)
                .setB01(3)
                .setB10(6)
                .setB11(7)
                .build());
        String resp = A.getC00() + A.getC01() + A.getC10() + A.getC11() + "";
        return resp;
    }

    public ListenableFuture<MatrixMultiSubReply> multiplySubAsync(List<Integer> a, List<Integer> b, int row, int clm) {
        MatrixServiceGrpc.MatrixServiceFutureStub stub = MatrixServiceGrpc.newFutureStub(getChannelAsync());
        MatrixMultiSubRequest matrixMultiSubRequest = MatrixMultiSubRequest.newBuilder()
                .addAllA(a)
                .addAllB(b)
                .setRow(row)
                .setClm(clm)
                .build();
        return stub.multiplySubBlock(matrixMultiSubRequest);
    }
    //******modification***************
    // do sub addition task
    public ListenableFuture<MatrixAddSubReply> additionSubAsync(
            List<Integer> a,
            List<Integer> b,
            int row
    ) {
        MatrixServiceGrpc.MatrixServiceFutureStub stub = MatrixServiceGrpc.newFutureStub(getChannelAsync());
        MatrixAddSubRequest matrixAddSubRequest = MatrixAddSubRequest.newBuilder()
                .addAllA(a)
                .addAllB(b)
                .setRow(row)
                .build();
        return stub.additionSubBlock(matrixAddSubRequest);
    }
    //******modification***************
    // do sub addition task
    public MatrixMultiSubReply multiplySub(List<Integer> a, List<Integer> b, int row, int clm) {

        ManagedChannel channel = getChannelAsync();
        MatrixServiceGrpc.MatrixServiceBlockingStub stub = MatrixServiceGrpc.newBlockingStub(channel);
        MatrixMultiSubReply reply = stub.multiplySubBlock(MatrixMultiSubRequest.newBuilder()
                .addAllA(a)
                .addAllB(b)
                .setRow(row)
                .setClm(clm)
                .build());
        int resp = reply.getC();

        return reply;
    }

    //******modification***************
    // get single footprint time
    public long getFootPrint(List<Integer> a, List<Integer> b, int row, int clm) {

        long startTime = System.nanoTime();
        MatrixServiceGrpc.MatrixServiceBlockingStub stub = MatrixServiceGrpc.newBlockingStub(getChannelAsync());
        MatrixMultiSubReply reply = stub.multiplySubBlock(MatrixMultiSubRequest.newBuilder()
                .addAllA(a)
                .addAllB(b)
                .setRow(row)
                .setClm(clm)
                .build());
        int resp = reply.getC();

        long endTime = System.nanoTime();
        long footprint = endTime - startTime;
        logger.info("footprint (getFootPrint): {}", TimeUnit.MILLISECONDS.convert(footprint, TimeUnit.NANOSECONDS));

        return footprint;
    }
    //******modification***************
    // full multipication
    public int[][] multiplyTotal(int[][] p, int[][] q, long deadline) {

        int pRow = p.length;
        int pClm = p[0].length;

        int qRow = q.length;
        int qClm = q[0].length;
        List<List<Integer>> aList = new ArrayList<List<Integer>>();
        List<List<Integer>> bList = new ArrayList<List<Integer>>();
        int ans[][] = new int[pRow][qClm];

        for (int i = 0; i < pRow; i++) {
            aList.add(Arrays.stream(p[i]).boxed().collect(Collectors.toList()));
        }

        for (int i = 0; i < qClm; i++) {
            List<Integer> b1 = new ArrayList<>();
            for (int j = 0; j < qRow; j++) {
                b1.add(q[j][i]);
            }
            bList.add(b1);
        }
        long timesum = 0;

        for (int i = 0; i < pRow; i++) {

            for (int j = 0; j < qClm; j++) {
                long startTime = System.nanoTime();

                MatrixMultiSubReply reply = multiplySub(aList.get(i), bList.get(j), i, j);

                long endTime = System.nanoTime();
                long footprint = endTime - startTime;
                timesum += footprint;

                int rw = reply.getRow();
                int cl = reply.getClm();
                int c = reply.getC();

                ans[rw][cl] = c;
                logger.info(c + " (" + rw + "," + cl + ")");

            }
        }

//        print(ans);
        logger.info("full task : {}", TimeUnit.MILLISECONDS.convert(timesum, TimeUnit.NANOSECONDS));

        return ans;
    }
    //******modification***************
    // Server Count extimation
    public ServerCountSummary getServerCount(int[][] p, int[][] q, long deadline) {

        int pRow = p.length;
        int pClm = p[0].length;

        int qRow = q.length;
        int qClm = q[0].length;
        List<List<Integer>> aList = new ArrayList<List<Integer>>();
        List<List<Integer>> bList = new ArrayList<List<Integer>>();
        int ans[][] = new int[pRow][qClm];

        for (int i = 0; i < pRow; i++) {
            aList.add(Arrays.stream(p[i]).boxed().collect(Collectors.toList()));
        }

        for (int i = 0; i < qClm; i++) {
            List<Integer> b1 = new ArrayList<>();
            for (int j = 0; j < qRow; j++) {
                b1.add(q[j][i]);
            }
            bList.add(b1);
        }

        long footPrint = getFootPrint(aList.get(0), bList.get(0), 0, 0);
        int subWorkCount = pRow * qClm;
        long numberOfServer = numberOfServer(footPrint, subWorkCount, deadline);

        return new ServerCountSummary(footPrint, numberOfServer, deadline, subWorkCount);

    }

    public static void print(int[][] p) {

        int pRow = p.length;
        int pClm = p[0].length;
        for (int i = 0; i < pRow; i++) {
            System.out.println();
            for (int j = 0; j < pClm; j++) {
                System.out.print(p[i][j] + "\t");
            }

        }
        System.out.println();
    }


    public static String printStr(int[][] p) {
        String s = "";
        int pRow = p.length;
        int pClm = p[0].length;
        for (int i = 0; i < pRow; i++) {

            for (int j = 0; j < pClm; j++) {
                s += p[i][j] + "\t";

            }
            s += "\n";
        }
        s += "\n";
        return s;
    }
    //******modification***************
    // get full multipication
    public int[][] multiplyTotalAsync(int[][] p, int[][] q) {

        int pRow = p.length;
        int pClm = p[0].length;

        int qRow = q.length;
        int qClm = q[0].length;
        List<List<Integer>> aList = new ArrayList<List<Integer>>();
        List<List<Integer>> bList = new ArrayList<List<Integer>>();
        int ans[][] = new int[pRow][qClm];

        for (int i = 0; i < pRow; i++) {
            aList.add(Arrays.stream(p[i]).boxed().collect(Collectors.toList()));
        }

        for (int i = 0; i < qClm; i++) {
            List<Integer> b1 = new ArrayList<>();
            for (int j = 0; j < qRow; j++) {
                b1.add(q[j][i]);
            }
            bList.add(b1);
        }

        List<ListenableFuture<MatrixMultiSubReply>> futures = new ArrayList<ListenableFuture<MatrixMultiSubReply>>();
        for (int i = 0; i < pRow; i++) {
            for (int j = 0; j < qClm; j++) {
                futures.add(multiplySubAsync(aList.get(i), bList.get(j), i, j));
            }
        }

        ListenableFuture<int[][]> call = Futures.whenAllSucceed(futures)
                .call(() -> {
                            int[][] ans1 = new int[pRow][qClm];
                            for (ListenableFuture<MatrixMultiSubReply> f : futures) {
                                MatrixMultiSubReply reply = Futures.getDone(f);
                                int rw = reply.getRow();
                                int cl = reply.getClm();
                                int c = reply.getC();
                                ans1[rw][cl] = c;
                            }
                            logger.info("whenAllSucceed...\n");
                            return ans1;
                        },
                        getExecutorService());
        while (!call.isDone()) {
            logger.info("waiting...\n");
        }
        try {
            ans = call.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
//        logger.info("full task : {}", TimeUnit.MILLISECONDS.convert(timesum, TimeUnit.NANOSECONDS));
        return ans;
    }
    //******modification***************
    // get full addition
    public int[][] additionTotalAsync(int[][] p, int[][] q) {

        int pRow = p.length;
        int pClm = p[0].length;

        int qRow = q.length;
        int qClm = q[0].length;
        List<List<Integer>> aList = new ArrayList<List<Integer>>();
        List<List<Integer>> bList = new ArrayList<List<Integer>>();
        int ans[][] = new int[pRow][pClm];

        for (int i = 0; i < pRow; i++) {
            aList.add(Arrays.stream(p[i]).boxed().collect(Collectors.toList()));
            bList.add(Arrays.stream(q[i]).boxed().collect(Collectors.toList()));
        }

        List<ListenableFuture<MatrixAddSubReply>> futures = new ArrayList<ListenableFuture<MatrixAddSubReply>>();
        for (int i = 0; i < pRow; i++) {
            futures.add(additionSubAsync(aList.get(i), bList.get(i), i));
        }

        ListenableFuture<int[][]> call = Futures.whenAllSucceed(futures)
                .call(() -> {
                            int[][] ans1 = new int[pRow][pClm];
                            for (ListenableFuture<MatrixAddSubReply> f : futures) {
                                MatrixAddSubReply reply = Futures.getDone(f);
                                int rw = reply.getRow();
                                List<Integer> a = reply.getAList();
                                for (int i = 0; i < a.size(); i++) {
                                    ans1[rw][i] = a.get(i);
                                }
                            }
                            logger.info("whenAllSucceed...\n");
                            return ans1;
                        },
                        getExecutorService());
        while (!call.isDone()) {
            logger.info("waiting...\n");
        }
        try {
            ans = call.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
//        logger.info("footprint full task : {}", TimeUnit.MILLISECONDS.convert(timesum, TimeUnit.NANOSECONDS));
        return ans;
    }

    public static long numberOfServer(long footprint, long numBlockCalls, long deadline) {

        long numberServer = ((footprint * numBlockCalls) / deadline);
        logger.info("numberServer:{}", numberServer);
        return numberServer;
    }

    public int[][] fileToArray(String content) throws IOException {
        int[][] p = new int[0][0];
        List<String[]> list = new ArrayList<String[]>();
        String[] lines = content.trim().split("\r\n");
        for (String line : lines) {
            String[] split = line.trim().split("[ ]+");
            list.add(split);
        }

        if (list.size() > 0) {
            String[] ss = list.get(0);
            int col = ss.length;
            int row = list.size();
            p = new int[row][col];

            for (int i = 0; i < row; i++) {
                String[] s = list.get(i);
                for (int j = 0; j < col; j++) {

                    p[i][j] = Integer.parseInt(s[j]);
                }
            }
        }
        return p;
    }

    //******modification***************
    // input matrix validation
    public static String checkError(int[][] p, int[][] q) throws IOException {
        String s = "";
        int p1 = p.length;
        int p2 = p[0].length;

        int q1 = q.length;
        int q2 = q[0].length;

        if (p2 != q1) {
            s = s + "\np matric clm size != q matric clm size";
        }
        if (p1 != p2) {
            s = s + "\np matric row size != p matric clm size";
        }
        if (q1 != q2) {
            s = s + "\nq matric row size != q matric clm size";
        }

//     p1/2;
        s = s + isPowerOf2(p1, "p");
        s = s + isPowerOf2(p1, "q");


        return s;
    }
    //******modification***************

    private static String isPowerOf2(int p1, String m) {
        double log = (Math.log(p1) / Math.log(2));
        int i = (int) log;
        if ((Math.pow(2, i)) != p1) {
            return "\n size of " + m + " matrix power of 2  ";
        }
        return "";
    }


}
