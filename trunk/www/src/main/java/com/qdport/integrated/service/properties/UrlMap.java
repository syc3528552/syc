package com.qdport.integrated.service.properties;

/**
 * @author ：SYC
 * @date   ：Created in 2020/6/17 9:46
 * @description：综合业务平台
 * @params:
 */
public class UrlMap {

    //新增或修改公告
    public final static String EDIT_NOTICE = "http://47.98.115.108:8080/notice/editNotice";
    //删除公告
    public final static String DEL_NOTICE = "http://47.98.115.108:8080/notice/delNotice";


    //测试登录验证
    public final static String LOGIN_URL_TEST = "http://10.201.199.94:8901/sso/loginVerify.do";
    //正式登录验证
    public final static String LOGIN_URL = "http://10.199.39.51:8080/sso/loginVerify.do";
}
