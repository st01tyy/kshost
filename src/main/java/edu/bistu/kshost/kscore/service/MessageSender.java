package edu.bistu.kshost.kscore.service;

import edu.bistu.kshost.Log;
import edu.bistu.kshost.kscore.model.ClientMessage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class MessageSender implements Runnable
{
    private final ClientMessage message;

    private final SocketChannel socketChannel;

    private ByteBuffer byteBuffer;

    public MessageSender(ClientMessage message, SocketChannel socketChannel)
    {
        this.message = message;
        this.socketChannel = socketChannel;
    }

    @Override
    public void run()
    {
        if(socketChannel == null)
        {
            Log.d(getClass().getName(), "socketChannel is null");
            return;
        }

        byteBuffer = ByteBuffer.allocateDirect(calculateBufferSize());
        loadByteBuffer();

        try
        {
            int length = socketChannel.write(byteBuffer);
            Log.d(getClass().getName(), "发送了长度为" + length + "字节的数据");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private Integer calculateBufferSize()
    {
        if(message == null)
            Log.d(getClass().getName(), "message is null");
        else if(message.getN() == null)
            Log.d(getClass().getName(), "message.getN() is null");
        return 16 + message.getN() * 4;
    }

    private void loadByteBuffer()
    {
        byteBuffer.putLong(message.getTime());
        byteBuffer.putInt(message.getType());
        byteBuffer.putInt(message.getN());
        if(message.getN() > 0)
        {
            Integer[] arr = message.getArr();
            for (Integer integer : arr)
            {
                byteBuffer.putInt(integer);
            }
        }
        byteBuffer.position(0);
    }
}
