package edu.bistu.kshost.runnable;

import edu.bistu.kshost.model.ClientMessage;

import java.util.concurrent.TimeUnit;

public class RegisterService extends MyRunnable
{
    public RegisterService()
    {
        super();
    }

    @Override
    public void run()
    {
        while(!isShutdown())
        {
            try
            {
                ClientMessage message = messageQueue.poll(5, TimeUnit.SECONDS);
                if(message == null)
                    continue;

            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}
