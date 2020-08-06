package edu.bistu.kshost.core.service.matchsystem;

import edu.bistu.kshost.Log;
import edu.bistu.kshost.core.service.MatchService;

import java.util.ArrayList;
import java.util.List;

public class Memory
{
    static MatchService matchService;

    static Element[] elements;

    static List<List<User>> result;

    static int matched = 0;

    static int target = 60000;

    public static void initialize(MatchService matchService)
    {
        Log.d(Memory.class.getName(), "初始化匹配系统内存");

        Memory.matchService = matchService;

        elements = new Element[1000];
        for(int i = 0; i < elements.length; i++)
        {
            elements[i] = new Element(i);
        }

        result = new ArrayList<>();
    }
}
