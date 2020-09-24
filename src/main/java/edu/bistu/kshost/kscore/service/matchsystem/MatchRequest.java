package edu.bistu.kshost.kscore.service.matchsystem;

public class MatchRequest
{
    private Long id;    //学号

    private Integer score;  //得分

    private Integer t;  //等待时间

    public MatchRequest(Long id, Integer score)
    {
        this.id = id;
        this.score = score;
        t = 1;
    }

    public Integer getT()
    {
        return t;
    }

    public void setT(Integer t)
    {
        this.t = t;
    }

    public Long getId()
    {
        return id;
    }

    public Integer getScore()
    {
        return score;
    }
}
