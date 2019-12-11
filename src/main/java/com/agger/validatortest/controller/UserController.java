package com.agger.validatortest.controller;


import com.agger.validatortest.system.annotation.PhoneValidator;
import com.agger.validatortest.system.group.Group1;
import com.agger.validatortest.system.group.Update;
import com.agger.validatortest.vo.ResultVO;
import com.agger.validatortest.vo.UserVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * @classname: UserController
 * @description: user控制器
 * @author chenhx
 * @date 2019-11-17 21:23
 */
@RestController
@Validated
public class UserController {

    @GetMapping("/user/{phone}")
    public ResultVO user(
            @PathVariable
            @NotBlank(message = "手机号码不能为空")
            @PhoneValidator  String phone){
        ResultVO result = new ResultVO();
        result.setCode(0);
        result.setMsg("通过手机号查询用户成功");
        result.setData("用户phone为：" + phone);
        return result;
    }

    // get请求校验要起作用，必须在类上加注解@Validated
    @GetMapping("/getUser")
    public ResultVO getUser(
            @RequestParam
            @Max(value = 100,message = "超出最大限制100")
            @NotNull(message = "用户id不能为空") Integer id){
        ResultVO result = new ResultVO();
        result.setCode(0);
        result.setMsg("查询用户成功");
        result.setData("用户id为：" + id);
        return result;
    }


    // 没指定分组，则默认分组
    @PostMapping("/addUser")
    @Validated
    public ResultVO addUser(@RequestBody @Valid UserVO user){
        ResultVO result = new ResultVO();
        result.setCode(0);
        result.setMsg("新增成功");
        result.setData(user);
        return result;
    }

    // 分组校验@Validated作用在方法上，vo上必须使用@Valid注解
    @PostMapping("/updateUser")
    @Validated(Update.class)
    public ResultVO updateUser(@RequestBody @Valid UserVO user){
        ResultVO result = new ResultVO();
        result.setCode(0);
        result.setMsg("修改成功");
        result.setData(user);
        return result;
    }

    // 分组校验@Validated直接作用在vo上，可行
    @PostMapping("/updateUser2")
    public ResultVO updateUser2(@RequestBody @Validated(Group1.class) UserVO user){
        ResultVO result = new ResultVO();
        result.setCode(0);
        result.setMsg("修改成功");
        result.setData(user);
        return result;
    }
}
