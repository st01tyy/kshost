package edu.bistu.kshost.kscore.service;

import edu.bistu.kshost.Log;
import edu.bistu.kshost.kscore.model.ClientMessage;

import java.util.concurrent.TimeUnit;

public class RegisterService extends Service
{
    @Override
    public void run()
    {
        Log.d(this.getClass().getName(), "服务启动");

        while(!isShutdown())
        {
            try
            {
                ClientMessage message = messageQueue.poll(5, TimeUnit.SECONDS); //5s阻塞

                //更新用户集合
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        Log.d(this.getClass().getName(), "服务关闭");
    }
}
