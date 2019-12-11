## SpringBoot使用@Validated验证参数

### 一、前言
在前端传递参数给后台接口的时候，后端会对传递的参数做一个基础校验，以前是手动写if一个个验证，效率极其低，而且还做了很多重复工作。本例没有太对基础和原理讲解，直接上代码，就是要简单粗暴，大家先用起来再说。项目源代码请访问github获取。

[github](https://github.com/AggerChen/validator-test/tree/master)

[博客地址](https://blog.csdn.net/github_36086968/article/details/103115128)



### 二、使用步骤
Hibernate Validator在JSR 303校验框架中提供了很多注解类。此Hibernate与ORM框架无关，只是一个实现了JSR-303规范的验证框架。
@Validated可以看作是@Valid的加强注解，@Valid能只能作用在方法、属性、构造、参数上，而@Validated可以作用在类上。

#### 2.1 在VO类上加上验证规则
在VO类属性上，我们可以加上我们需要的验证规则。

```java
package com.agger.validatortest.vo;

import com.agger.validatortest.system.annotation.PhoneValidator;
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

    private Integer id;

    @NotNull(message = "用户姓名不能为空")
    @Size(min=1,max=20,message = "用户姓名超出范围限制{min}-{max}")
    private String name;

    @NotBlank(message = "手机号码不能为空")
    @PhoneValidator         //自定义验证注解
    private String phone;

    @NotNull
    @Max(value = 100,message = "超出年龄限制{value}")
    @Min(value = 1,message = "最小年龄为{value}")
    private Integer age;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
}
```
#### 2.2 Controller类
@Validated注解作用在控制类上，会将类中的所有方法都开启参数校验。只有作用在类上，GET方式的请求才会校验。单独作用在请求方法上，只有POST请求校验生效，GET请求校验不会生效。

```java
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
@Validated                      //整个控制器都需要验证参数
@RestController
public class UserController {

    //RESTful 风格请求
    @GetMapping("/user/{phone}")
    public ResultVO user(
            @PathVariable
            @NotBlank(message = "手机号码不能为空")
            @PhoneValidator                 //使用自定义注解
                String phone){
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
```
其中使用到的ResultVO类是我自定义的一个返回对象，大家可以参考：
ResultVO.java
```java
package com.agger.validatortest.vo;

import lombok.Data;
/**
 * @classname: ResultVO
 * @description: 控制器返回结果VO
 * @author chenhx
 * @date 2019-11-17 21:29
 */
@Data
public class ResultVO {
    private Integer code;   //返回编码
    private String msg;     //返回信息
    private Object data;    //返回数据

    public ResultVO() {
    }
    public ResultVO(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
```
#### 2.3 自定义异常返回格式
此时参数验证就已经生效了，不过请求的返回格式是默认的，可能并不是我们需要的格式，所以接下来我们可以对参数异常经行处理，从而得到我们需要的返回格式。当验证框架验证参数不各规则时，会抛出异常，此时异常是验证框架自动处理的，我们可以编写一个全局的异常处理器，来自己处理异常返回。

GlobalExceptionHandlerController.java
```java
package com.agger.validatortest.controller;

import com.agger.validatortest.vo.ResultVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * @program: portal
 * @description: 全局异常处理
 * @author: chenhx
 * @create: 2019-11-14 17:00
 **/
@RestControllerAdvice
public class GlobalExceptionHandlerController {

    /**
     * @Title: handleConstraintViolationException
     * @Description: Get方式参数验证异常
     * @author chenhx
     * @date 2019-11-17 16:55:54
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ResultVO handleConstraintViolationException(ConstraintViolationException ex) throws IOException {
        // 获取所有错误信息
        HashSet<ConstraintViolation<?>> set = (HashSet<ConstraintViolation<?>>) ex.getConstraintViolations();
        Iterator<ConstraintViolation<?>> iterator = set.iterator();
        if(iterator.hasNext()){
            ConstraintViolation<?> next = iterator.next();
            // 只取一个异常信息返回
            String msg = next.getMessageTemplate();
            //返回自定义信息格式
            return new ResultVO(-1,msg);
        }
        return new ResultVO(-1,"参数错误");
    }

    /**
     * @Title: handleConstraintViolationException
     * @Description: Post方式参数验证异常
     * @author chenhx
     * @date 2019-11-17 16:33:49
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResultVO handleConstraintViolationException(MethodArgumentNotValidException ex) throws IOException {
        //获取所有错误异常
        List<ObjectError> allErrors = ex.getBindingResult().getAllErrors();
        //只返回第一个信息
        ObjectError error = allErrors.get(0);
        //返回自定义信息格式
        return new ResultVO(-1,error.getDefaultMessage());
    }
}
```

### 2.4 添加配置类
一次请求参数验证，会验证所有的规则是否合规，其实我们只需要让他验证到一个不合规就可以返回了，并不用默认全部验证完成才返回，所以我们可以添加一个Valid配置类
```java
package com.agger.validatortest.system.config;

import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * @classname: ValidatorConfig
 * @description: 参数验证框架配置
 * @author chenhx
 * @date 2019-11-17 16:03
 */
@Configuration
public class ValidatorConfig {

    @Bean
    public Validator Validator(){
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
                .configure()
                // true开启快速校验，判断到有一个校验不通过就返回
                .failFast(false)
                .buildValidatorFactory();
        return validatorFactory.getValidator();
    }
}
```
### 二、自定义验证规则
到此为止，上面的验证就已经可以用了，但是如果我们想要实现我们自己的验证规则怎么办？没关系，我们可以自己实现验证注解。这里以上面出现的@PhoneValidator注解为例，显示编写一个验证手机号码的注解。

PhoneValidator.java
```java
package com.agger.validatortest.system.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @classname: PhoneValidator
 * @description: 自定义注解：phone字段格式验证
 * @author chenhx
 * @date 2019-11-17 17:00
 */
@Documented
@Retention(RUNTIME)
@Target({FIELD, METHOD,PARAMETER})
//指定真正执行校验规则的类
@Constraint(validatedBy = PhoneValidatotClass.class)
public @interface PhoneValidator {
    String message()  default "手机号码格式不正确";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
```

真正实现校验规则的类：PhoneValidatotClass.java
```java
package com.agger.validatortest.system.annotation;

import org.apache.commons.lang3.StringUtils;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @classname: PhoneValidationValidator
 * @description: 手机号码规则验证器
 * @author chenhx
 * @date 2019-11-17 17:11
 */
public class PhoneValidatotClass implements ConstraintValidator<PhoneValidator, String> {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if(StringUtils.isBlank(value)){
            return false;
        }
        Matcher m = PHONE_PATTERN.matcher(value);
        return m.matches();
    }
}
```
至此，我们自定义的校验注解也可以运行了，当然，我们还可以写很多自己需要的校验注解。
现有的校验注解如下：

| 注解 | 说明 |
| --- | --- |
| @Null | 限制只能是mull |
| @NotNull | 限制值必须不为null |
| @AssertFalse | 限制值必须为false |
| @AssertTrue | 限制值必须为true |
| @DecimalMax(value) | 限制值必须为一个不大于指定值的数字 |
| @DecimalMin(value) | 限制值必须为一个不小于指定值的数字 |
| @Digits(integer,fraction) | 限制必须为一个小数，且整数部分的位数不能超过integer，小数部分的位数不能超过fraction |
| @Future | 限制必须是一个将来的日期 |
| @Past | 限制必须是一个过去的日期 |
| @Max(value) | 限制必须为一个不大于指定值的数字 |
| @Min(value) | 限制必须为一个不小于指定值的数字 |
| @Pattern(value) | 限制必须符合指定的正则表达式 |
| @Size(max,min) | 限制字符长度必须在min到max之间 |
| @Past | 验证注解的元素值（日期类型）比当前时间早|
| @NotEmpty | 验证字符串值不为null且不为空（字符串长度不为0、集合大小不为0）|
| @NotBlank | 验证字符串值不为空（不为null、去除首位空格后长度为0 |
| @Email | 验证注解的元素值是Email，也可以通过正则表达式和flag指定自定义的email格式 |
| @Length | 被注释的字符串的大小必须在指定的范围内 |
| @Range | 被注释的元素必须在合适的范围内 |


### 三、分组校验
如果新增和修改两个接口需要验证的字段不同，比如id字段，新增可以不传递，但是修改必须传递id，我们又不可能写两个vo来满足不同的校验规则。所以就需要用到分组校验来实现。

步骤：
#### 3.1 创建分组接口
例如定义接口 Insert、Update、Select来表示不同的操作；这些接口没有具体的方法，只是用来标识不同的分组。
> Update.class
```java
package dgbc.common.data.vo;

/**
 * @program: Update
 * @description: 分组标识
 * @author: chenhx
 * @create: 2019-12-10 14:30
 **/
public interface Update {
}
```
#### 3.2 model校验指定分组

修改UserVO属性上的校验分组
> UserVO.java
```java
package com.agger.validatortest.vo;

import com.agger.validatortest.system.annotation.PhoneValidator;
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
    private Integer id;
    
    // 指明了分组校验为Insert.class和Update.class
    @NotNull(message = "用户姓名不能为空",groups = {Insert.class,Update.class})
    @Size(min=1,max=20,message = "用户姓名超出范围限制{min}-{max}")
    private String name;

    // 未指定分组 则都生效
    @NotBlank(message = "手机号码不能为空")
    @PhoneValidator         //自定义验证注解
    private String phone;

    @NotNull
    @Max(value = 100,message = "超出年龄限制{value}")
    @Min(value = 1,message = "最小年龄为{value}")
    private Integer age;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
}
```
#### 3.3 接口方法指明分组
 1. @Validated(Update.class)作用在方法上
```java
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
```
2. @Validated(Update.class)作用在model上
```java
// 分组校验@Validated作用在方法上，vo上必须使用@Valid注解
@PostMapping("/updateUser")
public ResultVO updateUser(@RequestBody @Validated(Update.class) UserVO user){
	ResultVO result = new ResultVO();
	result.setCode(0);
	result.setMsg("修改成功");
	result.setData(user);
	return result;
}
```
注意：
1. @Validated作用在方法上时，model前必须使用加上@Valid注解
2. @Validated作用在model上时，不需要@Valid注解

#### 3.4 分组校验顺序
使用@GroupSequence注解来定义子分组校验的顺序。例：
> Group1.java
```java
package com.agger.validatortest.system.group;
import javax.validation.GroupSequence;

/**
 * @classname: Group1
 * @description: 校验分组接口，此接口没有任何实现，只是用来标识分组信息
 * @author chenhx
 * @date 2019-12-11 11:14:24
 */
@GroupSequence({Insert.class,Update.class})
public interface Group1 {
    // 分组校验排序，先验证Insert分组，再验证Update分组
}
```

### 四、github代码路径

[github](https://github.com/AggerChen/validator-test/tree/master)
