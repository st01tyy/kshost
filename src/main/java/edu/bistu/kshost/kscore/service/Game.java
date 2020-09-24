package edu.bistu.kshost.kscore.service;

import edu.bistu.kshost.Log;
import edu.bistu.kshost.kscore.KnowledgeStorm;
import edu.bistu.kshost.kscore.model.ClientMessage;
import edu.bistu.kshost.model.Question;

import java.util.Map;

public class Game extends Service
{
    /**
     * 规定：玩家数组的前一半为一个队伍，数组的后一半为一个队伍
     */

    private Integer gameID;

    private Question[] questions;

    private Long[] players;

    private Map<Long, Integer> playerMap; //<学号，数组下标>

    private Integer[] totalScore;   //队伍0，队伍1

    private Integer[] selectionStatus;  //答题状态：未答题，正确，错误（下标和玩家数组对应)

    private Integer countdown; //当前题目倒计时

    private GameService master;

    public Game(Question[] questions, Long[] players, Map<Long, Integer> playerMap)
    {
        this.questions = questions;
        this.players = players;
        this.playerMap = playerMap;
    }

    @Override
    public void run()
    {
        Log.d(getClass().getName(), "ID为" + gameID + "的对局开始！");

        totalScore = new Integer[2];
        selectionStatus = new Integer[players.length];

        for(int i = 0; i < players.length; i++)
        {
            KnowledgeStorm.sendMessage(ClientMessage.matchResult(gameID), players[i]);
        }

        Log.d(getClass().getName(), "ID为" + gameID + "的对局结束！");
        master.endGame(this);
    }

    public Integer getGameID() {
        return gameID;
    }

    public void setGameID(Integer gameID) {
        this.gameID = gameID;
    }

    public void setMaster(GameService master) {
        this.master = master;
    }
}
