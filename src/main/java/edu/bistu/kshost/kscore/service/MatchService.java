package edu.bistu.kshost.kscore.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import edu.bistu.kshost.Log;
import edu.bistu.kshost.kscore.model.ClientMessage;
import edu.bistu.kshost.kscore.model.ServerMessage;
import edu.bistu.kshost.kscore.service.matchsystem.*;
import edu.bistu.kshost.model.Subject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MatchService extends Service
{
    private Map<Long, MatchSystem> matchSystemMap;

    private ExecutorService cachedThreadPool;

    @Override
    public void run()
    {
        Log.d(this.getClass().getName(), "服务启动");

        matchSystemMap = new HashMap<>();

        cachedThreadPool = Executors.newCachedThreadPool(r ->
        {
            Thread thread = new Thread(r);
            thread.setName("match-system");
            return thread;
        });

        /* 找题 */
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url("http://" + "localhost" + ":" + "8080"
                + "/get_subjects");
        requestBuilder.get();

        Request request = requestBuilder.build();

        OkHttpClient okHttpClient = new OkHttpClient();
        Response response = null;

        try
        {
            response = okHttpClient.newCall(request).execute();
            String json = response.body().string();
            Gson gson = new Gson();
            List<Subject> subjectList = gson.fromJson(json, new TypeToken<List<Subject>>(){}.getType());
            for(Subject subject : subjectList)
            {
                addMatchSystem(subject);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        //开始循环
        while(!isShutdown())
        {
            try
            {
                ServerMessage message = messageQueue.poll(5, TimeUnit.SECONDS);
                if(message != null)
                {
                    Long selectedSubjectID = message.getArr()[0].longValue();
                    Log.d(getClass().getName(), "收到用户" + message.getStudentID() + "对科目" + message.getArr()[0].longValue() +
                            "的匹配请求");
                    matchSystemMap.get(selectedSubjectID).receiveMessage(message);
                }
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        Iterator<MatchSystem> iterator = matchSystemMap.values().iterator();
        while(iterator.hasNext())
        {
            MatchSystem matchSystem = iterator.next();
            matchSystem.shutdown();
        }

        cachedThreadPool.shutdown();
        Log.d(this.getClass().getName(), "服务关闭");
    }

    public void addMatchSystem(Subject subject)
    {
        MatchSystem matchSystem = new MatchSystem(subject);
        matchSystemMap.put(subject.getId(), matchSystem);
        cachedThreadPool.execute(matchSystem);
    }
}
