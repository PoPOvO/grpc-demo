package org.xli.helloworld;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;

/**
 * @author 谢力
 * @Description
 * @Date 创建于 2019/9/16 16:40
 */
public class SimpleServer {
    private Server server;

    public SimpleServer() {
        this.server = ServerBuilder
                .forPort(10222)
                .addService(new HelloWorldService())
                .build();
    }

    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(()-> {
            this.server.shutdown();
            System.out.println("------服务器关闭！");
        }));

        try {
            this.server.start();
            System.out.println("-------服务器启动成功！");
        } catch (IOException e) {
            e.printStackTrace();

            if (!this.server.isShutdown()) {
                this.server.shutdown();
                System.out.println("----------服务器关闭！");
            }
        }
    }

    static class HelloWorldService extends GreeterGrpc.GreeterImplBase {
        @Override
        public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
            HelloReply reply = HelloReply
                    .newBuilder()
                    .setMessage("Hello World " + request.getName())
                    .build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }

    // gRPC使用的守护线程，主线程需要等待到server执行shutdown()后才被唤醒
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        SimpleServer simpleServer = new SimpleServer();
        simpleServer.start();
        simpleServer.blockUntilShutdown();
    }
}
