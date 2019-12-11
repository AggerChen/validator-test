package com.agger.validatortest.vo;


import com.agger.validatortest.system.annotation.PhoneValidator;
import com.agger.validatortest.system.group.Update;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.*;

/**
 * @classname: User
 * @description: User类
 * @author chenhx
 * @date 2019-11-17 21:07
 */
@Data
@ToString
public class UserVO {

    // 指明了分组校验为Update.class
    @NotNull(message = "用户id不能为空",groups = Update.class)
    @Max(value = 100,message = "id超出大小{value}")
    private Integer id;

    @NotNull(message = "用户姓名不能为空")
    @Size(min=1,max=20,message = "用户姓名超出范围限制{min}-{max}")
    private String name;

    @NotBlank(message = "手机号码不能为空")
    @PhoneValidator
    private String phone;

    @NotNull
    @Max(value = 100,message = "超出年龄限制{value}")
    @Min(value = 1,message = "最小年龄为{value}")
    private Integer age;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
}
