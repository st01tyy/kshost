package edu.bistu.kshost.kscore.service;

import edu.bistu.kshost.Log;
import edu.bistu.kshost.kscore.KnowledgeStorm;
import edu.bistu.kshost.kscore.model.ClientMessage;
import edu.bistu.kshost.kscore.model.ServerMessage;
import edu.bistu.kshost.model.Question;
import edu.bistu.kshost.model.Selection;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Game extends Service
{
    /**
     * 规定：玩家数组的前一半为一个队伍，数组的后一半为一个队伍
     */

    private Integer gameID;

    private Question[] questions;

    private Long[] players;

    private Map<Long, Integer> playerMap; //<学号，数组下标>

    private Integer[] totalScore;   //队伍0（蓝队），队伍1（红队）

    private Integer[] playerScore;

    private Integer[] selectionStatus;  //答题状态：未答题，正确，错误（下标和玩家数组对应)

    private Integer countdown; //当前题目倒计时

    private GameService master;

    private ExecutorService threadPool;

    private final int standardScore = 100;

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
        totalScore[0] = 0;
        totalScore[1] = 0;

        selectionStatus = new Integer[players.length];
        playerScore = new Integer[players.length];
        for(int i = 0; i < playerScore.length; i++)
        {
            playerScore[i] = 0;
        }

        threadPool = Executors.newSingleThreadExecutor();

        for(int i = 0; i < players.length; i++)
        {
            KnowledgeStorm.sendMessage(ClientMessage.matchResult(gameID), players[i]);
        }

        Counter counter = new Counter(10);
        threadPool.execute(counter);

        Set<Long> set = new HashSet<>();
        while(counter.getTime().intValue() > 0 && set.size() < players.length)
        {
            try
            {
                ServerMessage message = messageQueue.poll(1, TimeUnit.SECONDS);
                if(message != null && message.getMessageType() == 1)
                    set.add(message.getStudentID());
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        Log.d(getClass().getName(), "对局" + gameID + "共有" + set.size() + "名玩家准备完毕！");

        for(int i = 0; i < questions.length; i++)
        {
            for(int j = 0; j < players.length; j++)
            {
                KnowledgeStorm.sendMessage(ClientMessage.startQuestion(i), players[j]);
            }

            Log.d(getClass().getName(), "对局" + gameID + "开始第" + (i + 1) + "题！");

            counter = new Counter(questions[i].getTimeLimit() + 1);
            resetSelectionStatus();

            threadPool.execute(counter);

            Integer count = 0;
            while(counter.getTime() > 0 && count < players.length)
            {
                try
                {
                    ServerMessage message = messageQueue.poll(500, TimeUnit.MILLISECONDS);
                    if(message != null && message.getMessageType() == 2)
                    {
                        count++;
                        Integer playerPosition = playerMap.get(message.getStudentID());
                        if(isSelectionCorrect(i, message.getArr()[1]))
                        {
                            Integer team = getTeam(message.getStudentID());
                            Integer timeRemain = message.getArr()[2];
                            Integer score = (int) (standardScore *  ((double)timeRemain / (double)questions[i].getTimeLimit()));
                            Log.d(getClass().getName(), "timeRemain = " + timeRemain + "score = " + score);
                            totalScore[team] += score;
                            playerScore[playerPosition] += score;
                            selectionStatus[playerPosition] = 1;
                        }
                        else
                            selectionStatus[playerPosition] = 2;

                        Integer[] arr = new Integer[players.length + 2];
                        for(int j = 0; j < selectionStatus.length; j++)
                        {
                            arr[j] = selectionStatus[j];
                        }
                        arr[arr.length - 2] = totalScore[0];
                        arr[arr.length - 1] = totalScore[1];

                        for(int j = 0; j < players.length; j++)
                        {
                            KnowledgeStorm.sendMessage(ClientMessage.updateStatus(arr), players[j]);
                        }
                    }
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }

        Integer winner;
        if(totalScore[0] > totalScore[1])
            winner = 0;
        else
            winner = 1;

        for(int i = 0; i < players.length; i++)
        {
            Integer[] arr = new Integer[3];
            Integer team;
            if(i < arr.length / 2)
                team = 0;
            else
                team = 1;
            if(team == winner)
                arr[0] = 0;
            else
                arr[0] = 1;
            arr[1] = totalScore[team];
            arr[2] = playerScore[i];
            KnowledgeStorm.sendMessage(ClientMessage.endGame(arr), players[i]);
        }

        threadPool.shutdown();
        Log.d(getClass().getName(), "ID为" + gameID + "的对局结束！");
        master.endGame(this);
    }

    public Integer getGameID() {
        return gameID;
    }

    public Question[] getQuestions() {
        return questions;
    }

    public Long[] getPlayers() {
        return players;
    }

    public Integer getTeam(Long id)
    {
        Integer i = playerMap.get(id);
        if(i == null)
            return null;
        if(i >= players.length / 2)
            return 1;
        else
            return 0;
    }

    public void setGameID(Integer gameID) {
        this.gameID = gameID;
    }

    public void setMaster(GameService master) {
        this.master = master;
    }

    private Boolean isSelectionCorrect(Integer questionPosition, Integer selectionPosition)
    {
        Question question = questions[questionPosition];
        Selection[] selections = question.getSelections();
        return selections[selectionPosition].getAnswer();
    }

    private void resetSelectionStatus()
    {
        for(int i = 0; i < selectionStatus.length; i++)
        {
            selectionStatus[i] = 0;
        }
    }

    private void setPlayerSelectionStatus(Integer playerPosition, Integer playerStatus)
    {
        selectionStatus[playerPosition] = playerStatus;
    }
}
