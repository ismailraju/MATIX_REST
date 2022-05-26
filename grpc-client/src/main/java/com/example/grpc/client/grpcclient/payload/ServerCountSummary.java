package com.example.grpc.client.grpcclient.payload;

import java.io.Serializable;

public class ServerCountSummary implements Serializable {
    long footPrint;
    long numberOfServer;
    long deadline;
    int subWorkCount;
    String message;

    public ServerCountSummary(long footPrint, long numberOfServer, long deadline, int subWorkCount) {
        this.footPrint = footPrint;
        this.numberOfServer = numberOfServer;
        this.deadline = deadline;
        this.subWorkCount = subWorkCount;
    }

    public ServerCountSummary(String message) {
        this.message = message;
    }

    public long getFootPrint() {
        return footPrint;
    }

    public void setFootPrint(long footPrint) {
        this.footPrint = footPrint;
    }

    public long getNumberOfServer() {
        return numberOfServer;
    }

    public void setNumberOfServer(long numberOfServer) {
        this.numberOfServer = numberOfServer;
    }

    public long getDeadline() {
        return deadline;
    }

    public void setDeadline(long deadline) {
        this.deadline = deadline;
    }

    public int getSubWorkCount() {
        return subWorkCount;
    }

    public void setSubWorkCount(int subWorkCount) {
        this.subWorkCount = subWorkCount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
