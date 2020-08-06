package edu.bistu.kshost;

import edu.bistu.kshost.runnable.MessageReceiver;

import java.util.HashSet;
import java.util.Set;

public class Memory
{
    public static Set<Long> connectedUsers;

    public static MessageReceiver messageReceiver;

    public static void initialize()
    {
        connectedUsers = new HashSet<>();
    }
}
