package edu.bistu.kshost.core;

import edu.bistu.kshost.core.model.User;
import edu.bistu.kshost.core.service.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class KnowledgeStorm
{
    public static Map<Long, User> connectedUsers; //已连接的用户

    public static Service[] services;   //各种服务

    private static ExecutorService serviceThreadPool;   //服务线程池

    private static ExecutorService messageThreadPool;   //发送消息的线程池

    private static ServerSocketChannel serverSocketChannel; //tcp主机通道

    private static final int port = 2333;   //端口

    public static void start()
    {
        //初始化内存
        connectedUsers = new ConcurrentHashMap<>();

        services = new Service[4];

        serviceThreadPool = Executors.newFixedThreadPool(5, new ThreadFactory()
        {
            @Override
            public Thread newThread(Runnable r)
            {
                Thread thread = new Thread(r);
                thread.setName("service-thread");
                return thread;
            }
        }); //固定线程数

        messageThreadPool = Executors.newCachedThreadPool(new ThreadFactory()
        {
            @Override
            public Thread newThread(Runnable r)
            {
                Thread thread = new Thread(r);
                thread.setName("message-thread");
                return thread;
            }
        }); //不固定线程数

        //启动tcp主机
        try
        {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(port));
            serverSocketChannel.configureBlocking(false);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        //启动服务

    }

    public static void shutdown()
    {

    }

}
