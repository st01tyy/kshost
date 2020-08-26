package edu.bistu.kshost;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MyCommandLineRunner implements CommandLineRunner
{

    @Override
    public void run(String... args) throws Exception
    {
        /**
         * 在此方法初始化游戏服务
         */

        Memory.initialize();    //初始化内存

        System.out.println("hello ");
    }
}
