package com.agger.validatortest.controller;


import com.agger.validatortest.system.annotation.PhoneValidator;
import com.agger.validatortest.vo.ResultVO;
import com.agger.validatortest.vo.UserVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @classname: UserController
 * @description: user控制器
 * @author chenhx
 * @date 2019-11-17 21:23
 */
@Validated
@RestController
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

    @GetMapping("/getUser")
    public ResultVO getUser(@RequestParam @NotNull(message = "用户id不能为空") Integer id){
        ResultVO result = new ResultVO();
        result.setCode(0);
        result.setMsg("查询用户成功");
        result.setData("用户id为：" + id);
        return result;
    }


    @PostMapping("/addUser")
    public ResultVO addUser(@RequestBody @Valid UserVO user){
        ResultVO result = new ResultVO();
        result.setCode(0);
        result.setMsg("新增成功");
        result.setData(user);
        return result;
    }
}
