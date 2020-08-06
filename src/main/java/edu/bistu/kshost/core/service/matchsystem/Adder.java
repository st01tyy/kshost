package edu.bistu.kshost.core.service.matchsystem;

public class Adder implements Runnable
{
    private User user;

    public Adder(User user)
    {
        this.user = user;
    }

    @Override
    public void run()
    {
        Memory.elements[user.getScore() / 10].addUser(user);
    }
}

