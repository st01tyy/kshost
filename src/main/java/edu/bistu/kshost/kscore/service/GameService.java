package edu.bistu.kshost.kscore.service;

import edu.bistu.kshost.Log;
import edu.bistu.kshost.kscore.model.ServerMessage;
import edu.bistu.kshost.model.GameInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GameService extends Service
{
    private Map<Integer, Game> gameMap;

    private ExecutorService gameThreadPool;

    @Override
    public void run()
    {
        Log.d(getClass().getName(), "GameService启动！");

        gameMap = new ConcurrentHashMap<>();

        gameThreadPool = Executors.newCachedThreadPool();

        while(!isShutdown())
        {
            try
            {
                ServerMessage serverMessage = messageQueue.poll(5, TimeUnit.SECONDS);
                if (serverMessage != null)
                {
                    Integer gameID = serverMessage.getArr()[0];
                    Game game = gameMap.get(gameID);
                    if(game != null)
                        game.receiveMessage(serverMessage);
                }
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        gameThreadPool.shutdown();

        Log.d(getClass().getName(), "GameService关闭！");
    }

    public synchronized void startGame(Game game)
    {
        Integer id = new Random().nextInt();
        while(gameMap.containsKey(id))
        {
            id = new Random().nextInt();
        }
        game.setGameID(id);
        game.setMaster(this);
        gameMap.put(id, game);
        gameThreadPool.execute(game);
    }

    public void endGame(Game game)
    {
        gameMap.remove(game.getGameID());
    }

    public Game getGame(Integer gameID)
    {
        return gameMap.get(gameID);
    }
}
