package org.xli.helloworld;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.TimeUnit;

import static org.xli.helloworld.GreeterGrpc.*;

/**
 * @author 谢力
 * @Description
 * @Date 创建于 2019/9/16 16:40
 */
public class SimpleClient {
    private ManagedChannel managedChannel;
    // 阻塞stub
    private GreeterBlockingStub blockingStub;
    // 非阻塞stub
    private GreeterStub asyncStub;

    public SimpleClient() {
        this.managedChannel = ManagedChannelBuilder
                .forAddress("localhost", 10222)
                .usePlaintext()
                .build();
        System.out.println("----------成功连接到服务器");
        this.blockingStub = newBlockingStub(this.managedChannel);
        this.asyncStub = newStub(this.managedChannel);
    }

    public void shutdown() throws InterruptedException {
        this.managedChannel
                .shutdown() // 关闭Channel
                .awaitTermination(5, TimeUnit.SECONDS); // 等待Channel终止，如果超时则放弃
    }

    public HelloReply sayHello(HelloRequest request) {
        HelloReply reply = this.blockingStub.sayHello(request);
        return reply;
    }

    public static void main(String[] args) throws InterruptedException {
        SimpleClient simpleClient = new SimpleClient();
        try {
            HelloRequest request = HelloRequest
                    .newBuilder()
                    .setName("AAAAAA")
                    .build();
            HelloReply reply = simpleClient.sayHello(request);
            System.out.println("Response:" + reply.getMessage());
        } finally {
            // 关闭连接
            simpleClient.shutdown();
        }
    }
}
