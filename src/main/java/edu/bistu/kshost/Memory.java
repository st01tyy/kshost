package edu.bistu.kshost;

import edu.bistu.kshost.kscore.KnowledgeStorm;
import edu.bistu.kshost.model.User;

import java.util.concurrent.ConcurrentHashMap;

public class Memory
{
    private static ConcurrentHashMap<Long, User> loggedInUser;    //<学号, 用户>

    public static void initialize()
    {
        loggedInUser = new ConcurrentHashMap<>();
        KnowledgeStorm.start();
    }

    private static KnowledgeStorm knowledgeStorm;

    public static boolean isUserLoggedIn(Long id)
    {
        /**
         * 判断学号为id的用户是否已登录
         */

        User user = loggedInUser.get(id);   //通过学号查找
        if(user == null)
            return false;
        else
            return true;
    }

    public static boolean userLogin(User user)
    {
        /**
         * 通过C语言平台身份验证后，调用此方法
         */

//        boolean isLoggedIn = isUserLoggedIn(user.getId());
//        if(isLoggedIn)
//            return false;   //重复登录
//        else
//        {
//            addNewUser(user);
//            return true;
//        }
        addNewUser(user);
        return true;
    }

    public static void userLogout(User user)
    {
        /**
         * 用户提交登出request，调用此方法
         */

        User u = loggedInUser.get(user.getId());
        if(u != null)
        {
            if(u.getToken().equals(user.getToken()))    //验证token
                removeUser(user.getId());
        }
    }

    private static void addNewUser(User user)
    {
        loggedInUser.put(user.getId(), user);
    }

    private static void removeUser(Long id)
    {
        /**
         * 将学号为id的用户从map中删除，完成登出
         */

        loggedInUser.remove(id);
    }
}
