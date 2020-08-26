package edu.bistu.kshost.model;

public class User
{
    /**
     * 已通过C语言平台API登录的用户
     */

    private Long id;    //学号

    private Integer token;

    public User()
    {
        id = null;
        token = null;
    }

    public User(Long id, Integer token)
    {
        this.id = id;
        this.token = token;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Integer getToken()
    {
        return token;
    }

    public void setToken(Integer token)
    {
        this.token = token;
    }
}
