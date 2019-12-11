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
