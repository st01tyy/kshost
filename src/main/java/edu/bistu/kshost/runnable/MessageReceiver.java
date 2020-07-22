package edu.bistu.kshost.runnable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
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

}
