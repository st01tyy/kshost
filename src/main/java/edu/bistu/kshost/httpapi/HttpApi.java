package edu.bistu.kshost.httpapi;

import edu.bistu.kshost.Log;
import edu.bistu.kshost.Memory;
import edu.bistu.kshost.kscore.KnowledgeStorm;
import edu.bistu.kshost.kscore.service.Game;
import edu.bistu.kshost.model.GameInfo;
import edu.bistu.kshost.model.Question;
import edu.bistu.kshost.model.Subject;
import edu.bistu.kshost.model.User;
import edu.bistu.kshost.service.QuestionService;
import edu.bistu.kshost.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@CrossOrigin
@RestController
public class HttpApi
{
    /**
     * API功能：
     * 考试平台账号验证
     * 可游玩科目
     * 题目信息
     */

    private SubjectService subjectService;
    private QuestionService questionService;

    @Autowired
    public HttpApi(SubjectService subjectService, QuestionService questionService)
    {
        this.subjectService = subjectService;
        this.questionService = questionService;
    }

    @PostMapping("/login")
    public LoginResult login(@RequestBody LoginRequest loginRequest)
    {
        /**
         * 调用C语言考试平台API验证身份
         * 判断是否重复登录
         * 内存维护
         * 返回结果
         */

        /* 调用C语言平台API */

        Integer token = new Random().nextInt();
        User user = new User(loginRequest.getId(), token);
        boolean result = Memory.userLogin(user);
        if(result)
        {
            /* 登录成功 */
            Log.d(this.getClass().getName(), "学号为" + loginRequest.getId() + "的用户登录成功");
            return new LoginResult(100, user);
        }
        else
        {
            /* 重复登录 */
            Log.d(this.getClass().getName(), "学号为" + loginRequest.getId() + "的用户重复登录");
            return new LoginResult(103, null);
        }
    }

    @PostMapping("/logout")
    public void logout(@RequestBody LogoutRequest logoutRequest)
    {
        /**
         * 登出
         */

        Memory.userLogout(new User(logoutRequest.getId(), logoutRequest.getToken()));
        Log.d(this.getClass().getName(), "学号为" + logoutRequest.getId() + "的用户登出");
    }

    @PostMapping("/new_subject")
    public Long createNewSubject(@RequestBody Subject subject)
    {
        return subjectService.createNewSubject(subject);
    }

    @GetMapping("/get_subjects")
    public Subject[] getSubjects()
    {
        return subjectService.getAllSubjects();
    }

    @PostMapping("/delete_subject")
    public Boolean deleteSubject(@RequestParam Long subjectID)
    {
        return subjectService.deleteSubject(subjectID);
    }

    @PostMapping("/edit_subject")
    public Boolean editSubject(@RequestBody Subject subject)
    {
        return subjectService.editSubject(subject);
    }

    @PostMapping("/new_question")
    public Boolean createNewQuestion(@RequestBody Question question)
    {
        return questionService.createNewQuestion(question);
    }

    @GetMapping("/get_questions_by_subject")
    public Question[] getQuestionsBySubject(@RequestParam Long subjectID)
    {
        return questionService.getQuestionsBySubjectID(subjectID);
    }

    @GetMapping("/get_game_info")
    public GameInfo getGameInfo(@RequestParam Integer gameID, @RequestParam Long playerID)
    {
        GameInfo gameInfo = new GameInfo();
        Game game = KnowledgeStorm.gameService.getGame(gameID);
        if(game == null)
            return null;
        gameInfo.setQuestions(game.getQuestions());
        Long[] players = game.getPlayers();
        String[] names = new String[players.length];
        for(int i = 0; i < players.length; i++)
        {
            names[i] = players[i].toString().substring(0, 1);
        }
        gameInfo.setNames(names);
        gameInfo.setTeam(game.getTeam(playerID));
        return gameInfo;
    }

    @PostMapping("/delete_all_questions")
    public Boolean deleteAllQuestionsBySubjectID(@RequestBody Subject subject)
    {
        if(subject != null)
            return questionService.deleteAllQuestionsBySubject(subject.getId());
        else
        {
            System.out.println("subject is null");
            return false;
        }
    }

}
