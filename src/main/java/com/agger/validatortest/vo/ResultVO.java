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
