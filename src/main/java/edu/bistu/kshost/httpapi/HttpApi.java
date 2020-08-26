package edu.bistu.kshost.httpapi;

import edu.bistu.kshost.Log;
import edu.bistu.kshost.Memory;
import edu.bistu.kshost.model.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class HttpApi
{
    /**
     * API功能：
     * 考试平台账号验证
     * 可游玩科目
     * 题目信息
     */

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

    @GetMapping("/subjects")
    public void getSubjects()
    {

    }

}
