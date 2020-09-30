package edu.bistu.kshost.kscore.service.matchsystem;

public class Adder implements Runnable
{
    private MatchRequest user;

    private MatchSystem master;

    public Adder(MatchRequest user, MatchSystem master)
    {
        this.user = user;
        this.master = master;
    }

    @Override
    public void run()
    {
        if(master.checkCancel(user.getId()))
        {
            System.out.println("Adder拒绝添加一个已经取消匹配的申请");
            return;
        }
        master.elements[user.getScore() / 10].addUser(user);
    }
}

