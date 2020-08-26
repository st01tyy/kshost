package edu.bistu.kshost.httpapi;

public class LoginResult
{
    /**
     * 100：登录成功
     * 101：C语言平台API返回登录失败
     * 102：C语言平台API出现异常
     * 103：账号已登录
     */

    private Integer result;

    private Integer token;  //token的作用是防止他人通过http request恶意提交登出申请

    public LoginResult()
    {
        result = null;
        token = null;
    }

    public LoginResult(Integer result, Integer token)
    {
        this.result = result;
        this.token = token;
    }

    public Integer getToken()
    {
        return token;
    }

    public void setToken(Integer token)
    {
        this.token = token;
    }

    public Integer getResult()
    {
        return result;
    }

    public void setResult(Integer result)
    {
        this.result = result;
    }
}
