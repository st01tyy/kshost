package edu.bistu.kshost.kscore;

import edu.bistu.kshost.Log;
import edu.bistu.kshost.kscore.model.ClientMessage;
import edu.bistu.kshost.kscore.service.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class KnowledgeStorm
{
    public static Map<Long, SocketChannel> connectedUsers; //已连接的用户

    public static Service[] services;   //各种服务

    private static ExecutorService serviceThreadPool;   //服务线程池

    private static ExecutorService messageThreadPool;   //发送消息的线程池

    private static ServerSocketChannel serverSocketChannel; //tcp主机通道

    private static final int port = 2333;   //端口

    private static MessageReceiver messageReceiver;

    private static MatchService matchService;

    public static GameService gameService;

    public static void start()
    {
        Log.d(KnowledgeStorm.class.getName(), "启动KS");

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

        try
        {
            //启动tcp主机
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(port));
            serverSocketChannel.configureBlocking(false);

            //启动服务，未完成！！！！
            messageReceiver = new MessageReceiver(serverSocketChannel);
            matchService = new MatchService();
            gameService = new GameService();

            services[1] = matchService;
            services[2] = gameService;

            /*registerService = new RegisterService();
            matchService = new MatchService();
            services[0] = registerService;
            services[1] = matchService;*/

            serviceThreadPool.execute(messageReceiver);
            serviceThreadPool.execute(matchService);
            serviceThreadPool.execute(gameService);
            Log.d(KnowledgeStorm.class.getName(), "KS已启动");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void shutdown()
    {

    }

    public static void sendMessage(ClientMessage message, Long studentID)
    {
        SocketChannel socketChannel = connectedUsers.get(studentID);
        MessageSender messageSender = new MessageSender(message, socketChannel);
        messageThreadPool.execute(messageSender);
    }

}
