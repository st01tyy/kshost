package edu.bistu.kshost.kscore.service.matchsystem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import edu.bistu.kshost.Log;
import edu.bistu.kshost.kscore.KnowledgeStorm;
import edu.bistu.kshost.kscore.service.Game;
import edu.bistu.kshost.model.Question;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameCreator implements Runnable
{
    private List<MatchRequest> matchResult;

    private MatchSystem master;

    private final int gameSize = 3;

    public GameCreator(List<MatchRequest> matchResult, MatchSystem master)
    {
        this.matchResult = matchResult;
        this.master = master;
    }

    @Override
    public void run()
    {
        /* 找题 */
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url("http://" + "localhost" + ":" + "8080"
                + "/get_questions_by_subject" + "?subjectID=" + master.getSubjectID());
        requestBuilder.get();

        Request request = requestBuilder.build();

        OkHttpClient okHttpClient = new OkHttpClient();
        Response response = null;
        try
        {
            response = okHttpClient.newCall(request).execute();
            String json = response.body().string();
            Gson gson = new Gson();
            List<Question> questionList = gson.fromJson(json, new TypeToken<List<Question>>(){}.getType());
            Collections.shuffle(questionList);
            Question[] questions = new Question[Math.min(questionList.size(), gameSize)];
            for(int i = 0; i < questions.length; i++)
            {
                questions[i] = questionList.get(i);
            }
            Long[] players = new Long[matchResult.size()];
            Map<Long, Integer> playerMap = new HashMap<>();
            for(int i = 0; i < players.length; i++)
            {
                players[i] = matchResult.get(i).getId();
                playerMap.put(matchResult.get(i).getId(), i);
            }
            Game game = new Game(questions, players, playerMap);

            KnowledgeStorm.gameService.startGame(game);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.d(getClass().getName(), "对局创建出错：" + e.getMessage());
            master.addBack(matchResult);
        }

    }
}
