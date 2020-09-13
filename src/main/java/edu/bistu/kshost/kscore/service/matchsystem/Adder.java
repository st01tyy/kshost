package edu.bistu.kshost.kscore.service.matchsystem;

public class Adder implements Runnable
{
    private User user;

    private MatchSystem master;

    public Adder(User user, MatchSystem master)
    {
        this.user = user;
        this.master = master;
    }

    @Override
    public void run()
    {
        master.elements[user.getScore() / 10].addUser(user);
    }
}

