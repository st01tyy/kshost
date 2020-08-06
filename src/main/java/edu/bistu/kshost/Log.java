package edu.bistu.kshost;

public class Log
{
    private static String constructMessage(String tag, String msg)
    {
        return tag + ": " + msg;
    }

    public static void d(String tag, String msg)
    {
        System.out.println(constructMessage(tag, msg));
    }
}
