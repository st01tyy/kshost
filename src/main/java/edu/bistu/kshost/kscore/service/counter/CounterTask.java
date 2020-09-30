package edu.bistu.kshost.kscore.service.counter;

public abstract class CounterTask
{
    public abstract void onStop(); //计时器提前终止时执行此逻辑
    public abstract void onFinish();   //计时器计时结束时执行此逻辑
    public abstract void process();    //计时器计时途中执行此逻辑
}
