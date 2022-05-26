package com.example.grpc.client.grpcclient;

import com.example.grpc.client.grpcclient.payload.ServerCountSummary;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
public class PingPongEndpoint {

    GRPCClientService grpcClientService;


    @Autowired
    public PingPongEndpoint(GRPCClientService grpcClientService) {
        this.grpcClientService = grpcClientService;
    }

    @GetMapping("/ping")
    public String ping() {
        return grpcClientService.ping();
    }

    @GetMapping("/add")
    public String add() {
        return grpcClientService.add();
    }

    @GetMapping("/multiply")
    public String multiply() {
        return grpcClientService.multiply();
    }

    @PostMapping("/multiply")
    public String multiply(
            @RequestParam("fileA") MultipartFile fileA,
            @RequestParam("fileB") MultipartFile fileB,
            @RequestParam("deadline") long deadline,
            RedirectAttributes redirectAttributes) throws IOException {

        String content = new String(fileA.getBytes(), StandardCharsets.UTF_8);
        int[][] matixA = grpcClientService.fileToArray(content);
        content = new String(fileB.getBytes(), StandardCharsets.UTF_8);
        int[][] matixB = grpcClientService.fileToArray(content);
        String s = GRPCClientService.checkError(matixA, matixB);
        if (!s.isEmpty()) {
            return s;
        }

        int[][] res = grpcClientService.multiplyTotal(matixA, matixB, deadline);
        return GRPCClientService.printStr(res);
    }

    @PostMapping("/multiplyAsync")
    public String multiplyAsync(
            @RequestParam("fileA") MultipartFile fileA,
            @RequestParam("fileB") MultipartFile fileB,
            RedirectAttributes redirectAttributes) throws IOException {

        String content = new String(fileA.getBytes(), StandardCharsets.UTF_8);
        int[][] matixA = grpcClientService.fileToArray(content);
        content = new String(fileB.getBytes(), StandardCharsets.UTF_8);
        int[][] matixB = grpcClientService.fileToArray(content);
        String s = GRPCClientService.checkError(matixA, matixB);
        if (!s.isEmpty()) {
            return s;
        }
        int[][] res = grpcClientService.multiplyTotalAsync(matixA, matixB );
        return GRPCClientService.printStr(res);
    }
    @PostMapping("/additionAsync")
    public String additionAsync(
            @RequestParam("fileA") MultipartFile fileA,
            @RequestParam("fileB") MultipartFile fileB
             ) throws IOException {

        String content = new String(fileA.getBytes(), StandardCharsets.UTF_8);
        int[][] matixA = grpcClientService.fileToArray(content);
        content = new String(fileB.getBytes(), StandardCharsets.UTF_8);
        int[][] matixB = grpcClientService.fileToArray(content);
        String s = GRPCClientService.checkError(matixA, matixB);
        if (!s.isEmpty()) {
            return s;
        }
        int[][] res = grpcClientService.additionTotalAsync(matixA, matixB );
        return GRPCClientService.printStr(res);
    }

    @PostMapping("/serverCount")

    public ResponseEntity<ServerCountSummary> getServerCount(
            @RequestParam("fileA") MultipartFile fileA,
            @RequestParam("fileB") MultipartFile fileB,
            @RequestParam("deadline") long deadline) throws IOException {

        String content = new String(fileA.getBytes(), StandardCharsets.UTF_8);
        int[][] matixA = grpcClientService.fileToArray(content);
        content = new String(fileB.getBytes(), StandardCharsets.UTF_8);
        int[][] matixB = grpcClientService.fileToArray(content);
        String s = GRPCClientService.checkError(matixA, matixB);
        if (!s.isEmpty()) {
            return ResponseEntity.ok(new ServerCountSummary(s));
        }
        return ResponseEntity.ok(grpcClientService.getServerCount(matixA, matixB, deadline));

    }


}
