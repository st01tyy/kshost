package edu.bistu.kshost.kscore.service.matchsystem;

import java.util.*;

public class Element
{
    private MatchSystem master;

    public final Object lock = new Object();

    private Set<MatchRequest> userSet;

    private int index;

    public Element(int index, MatchSystem master)
    {
        userSet = new HashSet<>();
        this.index = index;
        this.master = master;
    }

    public void addUser(MatchRequest u)
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
            Iterator<MatchRequest> iterator = userSet.iterator();
            while(iterator.hasNext())
            {
                MatchRequest user = iterator.next();
                if(master.checkCancel(user.getId()))
                    userSet.remove(user);
                else
                    user.setT(user.getT() + 1);
            }
        }
    }

    public List<MatchRequest> match()
    {
        List<MatchRequest> list = new ArrayList<>();
        synchronized (lock)
        {
            if(userSet.size() >= master.size)
            {
                list = getUsers(master.size);
            }
            else if(userSet.size() > 0)
            {
                int maxP = 0;
                Iterator<MatchRequest> iterator = userSet.iterator();
                while(iterator.hasNext())
                {
                    MatchRequest user = iterator.next();
                    int temp = getP(user);
                    if(temp > maxP)
                        maxP = temp;
                    list.add(user);
                    iterator.remove();
                }
                int require = master.size - list.size();
                for(int i = 1; i <= maxP && require > 0; i++)
                {
                    if(index - i >= 0)
                    {
                        cloneUserList(index - i, list, require);
                    }
                    require = master.size - list.size();
                    if(require == 0)
                        break;
                    if(index + i < master.elements.length)
                    {
                        cloneUserList(index + i, list, require);
                    }
                    require = master.size - list.size();
                }
            }
        }
        return list;
    }

    public List<MatchRequest> getUsers(int number)
    {
        int count = 0;
        List<MatchRequest> list = new ArrayList<>();
        synchronized (lock)
        {
            Iterator<MatchRequest> iterator = userSet.iterator();
            while(iterator.hasNext() && count < number)
            {
                MatchRequest user = iterator.next();
                list.add(user);
                iterator.remove();
                count++;
            }
        }
        return list;
    }

    private void cloneUserList(int elementIndex, List<MatchRequest> list, int require)
    {
        List<MatchRequest> temp = master.elements[elementIndex].getUsers(require);
        for(MatchRequest u : temp)
        {
            list.add(u);
        }
    }

    private int getP(MatchRequest u)
    {
        int t = u.getT();
        return t * t;
    }


}
