package edu.bistu.kshost.kscore.service;

import edu.bistu.kshost.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class MessageReceiver extends Service
{
    private Selector selector;

    private ServerSocketChannel serverSocketChannel;

    public MessageReceiver(ServerSocketChannel serverSocketChannel) throws IOException
    {
        this.serverSocketChannel = serverSocketChannel;
        selector = Selector.open();
    }

    @Override
    public void run()
    {
        Log.d(this.getClass().getName(), "服务启动");

        try
        {
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT); //注册ACCEPT动作

            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);    //缓冲区

            while(!isShutdown())
            {
                selector.select(5000);  //5s阻塞时间
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while(iterator.hasNext())
                {
                    SelectionKey selectionKey = iterator.next();
                    if(selectionKey.isAcceptable())
                    {
                        Log.d(this.getClass().getName(), "检测到ACCEPT动作");
                        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        socketChannel.register(selector, SelectionKey.OP_READ);
                    }
                    if(selectionKey.isReadable())
                    {
                        Log.d(this.getClass().getName(), "检测到READ动作");
                        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                        try
                        {
                            socketChannel.read(byteBuffer);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            continue;
                        }

                        //封装ClientMessage
                    }
                }
            }


        }
        catch (ClosedChannelException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        Log.d(this.getClass().getName(), "服务关闭");
    }
}
