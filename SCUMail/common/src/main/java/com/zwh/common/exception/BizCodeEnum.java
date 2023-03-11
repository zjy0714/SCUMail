package com.zwh.common.exception;

/**
 * 错误状态码和错误信息定义类
 * 1.错误码定义规则为5位数字
 * 2.前两位表示业务场景，后三位表示错误码。
 * 3.维护错误码后需要维护错误描述，将它们定义为枚举形式
 * 错误码列表：
 *  10：通用
 *     001参数格式校验
 *     002短信验证码频率太高
 *  12：订单
 *  13：购物车
 *  14：物流
 *  15：用户
 *  21:库存
 */
public enum BizCodeEnum {
    UNKNOWN_EXCEPTION(10000,"系统未知异常"),
    VALID_EXCEPTION(10001,"参数格式校验失败"),
    SMS_CODE_EXCEPTION(10002,"发送验证码频率太高"),
    PRODUCT_UP_EXCEPTION(11000,"商品上架异常"),
    USER_EXIST_EXCEPTION(15001,"用户名已存在"),
    PHONE_EXIST_EXCEPTION(15002,"手机号已存在"),
    LOGINACCT_PASSWORD_INVALID_EXCEPTION(15003,"账号密码错误"),
    NO_STOCK_EXCEPTION(21000,"商品没有库存");

    private int code;
    private String msg;

    BizCodeEnum(int code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
