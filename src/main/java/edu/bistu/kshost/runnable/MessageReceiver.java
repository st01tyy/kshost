package edu.bistu.kshost.runnable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class MessageReceiver extends MyRunnable
{
    private final int port = 2333;

    @Override
    public void run()
    {
        messageQueue = null;    //delete queue from memory

        try
        {
            Selector selector = Selector.open();

            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(port));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);

            while(!isShutdown())
            {
                selector.select(5000);
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while(iterator.hasNext())
                {
                    SelectionKey selectionKey = iterator.next();
                    if(selectionKey.isAcceptable())
                    {

                    }
                    if(selectionKey.isReadable())
                    {
                        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                        socketChannel.read(byteBuffer);

                        byteBuffer.flip();
                        handleMessage(byteBuffer);
                        byteBuffer.clear();
                    }
                }
            }

            serverSocketChannel.close();
            selector.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void handleMessage(ByteBuffer byteBuffer)
    {
        byte type = byteBuffer.get();
        if(type == 0)
        {
            /**
             * 心跳包
             */
        }
        else if(type == 1)
        {
            /**
             * 一般消息
             */

        }
    }

}
