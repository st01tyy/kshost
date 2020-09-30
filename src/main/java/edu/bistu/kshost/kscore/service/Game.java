package edu.bistu.kshost.kscore.service;

import edu.bistu.kshost.Log;
import edu.bistu.kshost.kscore.KnowledgeStorm;
import edu.bistu.kshost.kscore.model.ClientMessage;
import edu.bistu.kshost.kscore.model.ServerMessage;
import edu.bistu.kshost.kscore.service.counter.Counter;
import edu.bistu.kshost.kscore.service.counter.CounterTask;
import edu.bistu.kshost.model.Question;
import edu.bistu.kshost.model.Selection;

import java.util.Arrays;
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

    private final Question[] questions;

    private final Long[] players;

    private final Map<Long, Integer> playerMap; //<学号，数组下标>

    private Integer[] selectionStatus;  //答题状态：未答题，正确，错误（下标和玩家数组对应)

    private GameService master;

    class GameCounterTask extends CounterTask
    {
        private Integer finishSignal;   //小于0

        GameCounterTask(Integer finishSignal)
        {
            this.finishSignal = finishSignal;
        }

        @Override
        public void onStop()
        {
            //do nothing
        }

        @Override
        public void onFinish()
        {
            ServerMessage serverMessage = new ServerMessage();
            serverMessage.setMessageType(finishSignal);
            receiveMessage(serverMessage);
        }

        @Override
        public void process()
        {
            //do nothing
        }
    }

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

        //队伍0（蓝队），队伍1（红队）
        Integer[] totalScore = new Integer[2];
        Arrays.fill(totalScore, 0);

        selectionStatus = new Integer[players.length];
        Integer[] playerScore = new Integer[players.length];
        Arrays.fill(playerScore, 0);

        ExecutorService threadPool = Executors.newSingleThreadExecutor();

        for (Long player : players)
        {
            KnowledgeStorm.sendMessage(ClientMessage.matchResult(gameID), player);
        }

        Counter counter = new Counter(5, new GameCounterTask(-1));
        threadPool.execute(counter);

        Set<Long> set = new HashSet<>();
        int count = 0;
        while(count < players.length)
        {
            try
            {
                ServerMessage message = messageQueue.poll(1, TimeUnit.SECONDS);
                if(message != null)
                {
                    Integer type = message.getMessageType();
                    if(type == 1)
                    {
                        Long studentID = message.getStudentID();
                        if(!set.contains(studentID))
                        {
                            count++;
                            set.add(message.getStudentID());
                        }
                    }
                    else if(type == -1)
                    {
                        System.out.println("对局" + gameID + "收到了计时器时间到的消息，终止循环");
                        break;
                    }
                    else
                        System.out.println("对局" + gameID + "收到了当前不支持的消息类型：" + type);
                }
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        counter.stop();
        Log.d(getClass().getName(), "对局" + gameID + "共有" + count + "名玩家准备完毕！");

        for(int i = 0; i < questions.length; i++)
        {
            for (Long player : players)
            {
                KnowledgeStorm.sendMessage(ClientMessage.startQuestion(i), player);
            }

            Log.d(getClass().getName(), "对局" + gameID + "开始第" + (i + 1) + "题！");

            counter = new Counter(questions[i].getTimeLimit() + 1, new GameCounterTask(-2));
            resetSelectionStatus();
            threadPool.execute(counter);
            count = 0;
            while(count < players.length)
            {
                try
                {
                    ServerMessage message = messageQueue.poll(500, TimeUnit.MILLISECONDS);
                    if(message != null)
                    {
                        Integer type = message.getMessageType();
                        if(type == 2)
                        {
                            count++;
                            Integer playerPosition = playerMap.get(message.getStudentID());
                            if(isSelectionCorrect(i, message.getArr()[1]))
                            {
                                Integer team = getTeam(message.getStudentID());
                                Integer timeRemain = message.getArr()[2];
                                int standardScore = 100;
                                int score = (int) (standardScore *  ((double)timeRemain / (double)questions[i].getTimeLimit()));
                                Log.d(getClass().getName(), "timeRemain = " + timeRemain + "score = " + score);
                                totalScore[team] += score;
                                playerScore[playerPosition] += score;
                                selectionStatus[playerPosition] = 1;
                            }
                            else
                                selectionStatus[playerPosition] = 2;

                            Integer[] arr = new Integer[players.length + 2];
                            System.arraycopy(selectionStatus, 0, arr, 0, selectionStatus.length);
                            arr[arr.length - 2] = totalScore[0];
                            arr[arr.length - 1] = totalScore[1];

                            for (Long player : players)
                            {
                                KnowledgeStorm.sendMessage(ClientMessage.updateStatus(arr), player);
                            }
                        }
                        else if(type == -2)
                        {
                            System.out.println("对局" + gameID + "收到了计时器时间到的消息，终止循环");
                            break;
                        }
                        else
                            System.out.println("对局" + gameID + "收到了当前不支持的消息类型：" + type);
                    }
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            counter.stop();
        }

        int winner;
        if(totalScore[0] > totalScore[1])
            winner = 0;
        else
            winner = 1;

        for(int i = 0; i < players.length; i++)
        {
            Integer[] arr = new Integer[3];
            int team;
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
        Arrays.fill(selectionStatus, 0);
    }
}
