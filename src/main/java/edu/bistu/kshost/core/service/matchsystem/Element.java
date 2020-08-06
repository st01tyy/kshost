package edu.bistu.kshost.core.service.matchsystem;

import edu.bistu.kshost.core.service.MatchService;

import java.util.*;

public class Element
{
    public final Object lock = new Object();

    private Set<User> userSet;

    private int index;

    public Element(int index)
    {
        userSet = new HashSet<>();
        this.index = index;
    }

    public void addUser(User u)
    {
        synchronized (lock)
        {
            userSet.add(u);
        }
    }

    public void update()
    {
        synchronized (lock)
        {
            Iterator<User> iterator = userSet.iterator();
            while(iterator.hasNext())
            {
                User user = iterator.next();
                user.setT(user.getT() + 1);
            }
        }
    }

    public List<User> match()
    {
        List<User> list = new ArrayList<>();
        synchronized (lock)
        {
            if(userSet.size() >= 6)
            {
                list = getUsers(6);
            }
            else if(userSet.size() > 0)
            {
                int maxP = 0;
                Iterator<User> iterator = userSet.iterator();
                while(iterator.hasNext())
                {
                    User user = iterator.next();
                    int temp = getP(user);
                    if(temp > maxP)
                        maxP = temp;
                    list.add(user);
                    iterator.remove();
                }
                int require = 6 - list.size();
                for(int i = 1; i <= maxP && require > 0; i++)
                {
                    if(index - i >= 0)
                    {
                        cloneUserList(index - i, list, require);
                    }
                    require = 6 - list.size();
                    if(require == 0)
                        break;
                    if(index + i < Memory.elements.length)
                    {
                        cloneUserList(index + i, list, require);
                    }
                    require = 6 - list.size();
                }
            }
        }
        return list;
    }

    public List<User> getUsers(int number)
    {
        int count = 0;
        List<User> list = new ArrayList<>();
        synchronized (lock)
        {
            Iterator<User> iterator = userSet.iterator();
            while(iterator.hasNext() && count < number)
            {
                User user = iterator.next();
                list.add(user);
                iterator.remove();
                count++;
            }
        }
        return list;
    }

    private void cloneUserList(int elementIndex, List<User> list, int require)
    {
        List<User> temp = Memory.elements[elementIndex].getUsers(require);
        for(User u : temp)
        {
            list.add(u);
        }
    }

    private int getP(User u)
    {
        int t = u.getT();
        return t * t;
    }


}
