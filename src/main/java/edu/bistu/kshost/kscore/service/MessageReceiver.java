package edu.bistu.kshost.kscore.service;

import edu.bistu.kshost.Log;
import edu.bistu.kshost.Memory;
import edu.bistu.kshost.kscore.KnowledgeStorm;
import edu.bistu.kshost.kscore.model.ClientMessage;
import edu.bistu.kshost.kscore.model.ServerMessage;

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
                        if(socketChannel != null)
                        {
                            socketChannel.configureBlocking(false);
                            socketChannel.register(selector, SelectionKey.OP_READ);
                        }
                    }

                    if(selectionKey.isReadable())
                    {
                        Log.d(this.getClass().getName(), "检测到READ动作");
                        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                        if(socketChannel != null)
                        {
                            try
                            {
                                Integer length = socketChannel.read(byteBuffer);
                                Log.d(getClass().getName(), "接收长度为" + length + "字节的数据");

                                if(length == -1)
                                {
                                    Log.d(getClass().getName(), "客户端主动断开");
                                    selectionKey.cancel();
                                }
                                else if(length == 0)
                                    Log.d(getClass().getName(), "接收到长度为0的数据");
                                else
                                {
                                    byteBuffer.limit(byteBuffer.position());
                                    byteBuffer.position(0);

                                    while(byteBuffer.position() < byteBuffer.limit())
                                    {
                                        ServerMessage message = new ServerMessage();
                                        message.setStudentID(byteBuffer.getLong());
                                        message.setTime(byteBuffer.getLong());
                                        message.setServiceNumber(byteBuffer.getInt());
                                        message.setMessageType(byteBuffer.getInt());
                                        message.setN(byteBuffer.getInt());
                                        if(message.getN() > 0)
                                        {
                                            Integer[] arr = new Integer[message.getN()];
                                            for(int i = 0; i < arr.length; i++)
                                            {
                                                arr[i] = byteBuffer.getInt();
                                            }
                                            message.setArr(arr);
                                        }

                                        Log.d(getClass().getName(), "学号：" + message.getStudentID() + "时间：" + message.getTime());
                                        if(message.getServiceNumber() == 0)
                                        {
                                            if(message.getMessageType() == 1)
                                            {
                                                if(Memory.isUserLoggedIn(message.getStudentID()))
                                                {
                                                    KnowledgeStorm.connectedUsers.put(message.getStudentID(), socketChannel);
                                                    Log.d(getClass().getName(), "学号为" + message.getStudentID() + "的用户已连接");
                                                    KnowledgeStorm.sendMessage(ClientMessage.registerSuccess(), message.getStudentID());
                                                }
                                                else
                                                    Log.d(getClass().getName(), "学号为" + message.getStudentID() + "的用户非法连接");
                                            }
                                            else if(message.getMessageType() == 2)
                                            {
                                                KnowledgeStorm.connectedUsers.remove(message.getStudentID());
                                                Log.d(getClass().getName(), "学号为" + message.getStudentID() + "的用户断开连接");
                                                selectionKey.cancel();
                                                socketChannel.close();
                                            }
                                        }
                                        else
                                            KnowledgeStorm.services[message.getServiceNumber()].receiveMessage(message);
                                    }
                                    byteBuffer.clear();
                                }

                            }
                            catch (Exception e)
                            {
                                System.out.println("MessageReceiver抛出异常：" + e.getMessage());
                                selectionKey.cancel();
                            }
                        }
                    }
                    iterator.remove();
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
