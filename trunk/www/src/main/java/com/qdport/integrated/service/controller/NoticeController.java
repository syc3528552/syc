package com.qdport.integrated.service.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qdport.integrated.service.properties.UrlMap;
import com.qdport.integrated.service.util.*;
import com.qdport.logsdk.annotation.QdLogPlus;
import com.qdport.logsdk.model.OpType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ：SYC
 * @date ：Created in 2019/11/1 9:54
 * @description：综合业务平台
 * @modified By：
 */
@Slf4j
@RestController
@RequestMapping(value = "/notice")
public class NoticeController {

    // 公钥
    String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCY1oxcG5yafndbrRlYSVcd7NK/GrAZvWXu6olpgUvlv7/4tC3v6siBkjHxHSFdidumC05yD87wq6CjBMg9RBHGaHB8fQnQOhRzSqciEXNoh22mnii7bLqMRFBfD9ueEClc56S1uXCmJnOWrBheMh/xW29PxcKpmMxuTrPfgJQqzQIDAQAB";
    // 私钥
    String privateKey="MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJjWjFwbnJp+d1utGVhJVx3s0r8asBm9Ze7qiWmBS+W/v/i0Le/qyIGSMfEdIV2J26YLTnIPzvCroKMEyD1EEcZocHx9CdA6FHNKpyIRc2iHbaaeKLtsuoxEUF8P254QKVznpLW5cKYmc5asGF4yH/Fbb0/FwqmYzG5Os9+AlCrNAgMBAAECgYAGzOUJ5CCfryjYZ2dNSlYZ+sOVxTY4rviuHLlofFxIKLP9+OZiWmv5d+vI8qbdZpjjVnVzFcj7ZGeNWVUfhLvVqmjZinfTNOBc4zyFi1u9hsJCphvK2RjwNUb3R+9T5Hmtgc7qPcuuB4EBd/LyIWDtHduIXQyz3S5GHtGT4MDc8QJBAODIprQSB4tKU5GGZqePPz1dwM28kDEBv5XVSGoJQ001+S3LKIE3HwyKIPvKA2YCLCn83LEpiN8q6n+VSeDt3LMCQQCuECLAEZ+I5sPAutNNr2ihhWor3M8mUcsVGAK6T7xnKJe+YL8F1t2PST4e4xEGJDkvSHc+Sxa6JUNzI/dk8Jp/AkEAp8k+nhUDKpbNcEfpYlOLueu8WhXRb7NFZbsxdWz6nqyWFfeagXE6rOqCGlA9Xyxp+OF1foxpTWPEbH1sCwih/wJAZR64oW5biRPO2xNuGC50EYnJQUwZB8DywwFc7FHQOPnLFe1sa/8EXe6F6TmutSusEPz/UTAcNu3Y/nYBjiF9DwJAUnjxZ37G8S1dTuxvdLKGjoDwRcUuwSQ/TCzIwIgqcAl8QY0BaOssrQhtsIOs4FSPHNc6NQ11+cgB3Nt+j8Ckhg==";

    /**
     * @author ：SYC
     * @date   ：Created in 2020/6/17 8:51
     * @description：增加或修改公告
     * @params:
     */
    @PostMapping("/editNotice")
    @QdLogPlus(opDesc = "增加或修改公告",opType = OpType.SAVEORUPDATE)
    public RestfulResult editNotice(@RequestBody String data) {
        if(StrUtil.isEmpty(data)){
           new RestfulResult(500,"参数不能为空");
        }
        try {
            //生成验签标志
            JSONObject param = JSON.parseObject(data);
//            String parStr = SignUtil.formatUrlMap((Map)param, false, false);
//            String sign = SignUtil.sign(parStr,privateKey,"UTF-8");
//            param.put("sign",sign);

            String result = HttpClientUtils.sendPost(UrlMap.EDIT_NOTICE, createJsonHeader(), param);
            if (null == result) {
                new RestfulResult(500,"公告接口通信失败");
            }
            JSONObject jsonObject = JSON.parseObject(result);
            if(!"success".equals(jsonObject.getString("status"))){
                new RestfulResult(500,jsonObject.getString("msg"));
            }
            return new RestfulResult(0,jsonObject.getString("msg"),jsonObject.getString("assignId"));
        } catch (Exception e) {
            log.error("公告添加或修改异常", e);
            return new RestfulResult(500,"公告添加或修改异常");
        }
    }

    /**
     * @author ：SYC
     * @date   ：Created in 2020/6/17 8:51
     * @description：删除公告
     * @params:
     */
    @PostMapping("/delNotice")
    @QdLogPlus(opDesc = "删除公告",opType = OpType.DELETE)
    public RestfulResult delNotice(@RequestBody String data) {
        if(StrUtil.isEmpty(data)){
            new RestfulResult(500,"参数不能为空");
        }
        try {
            //生成验签标志
            JSONObject param = JSON.parseObject(data);
//            String parStr = SignUtil.formatUrlMap((Map)param, false, false);
//            String sign = SignUtil.sign(parStr,privateKey,"UTF-8");
//            param.put("sign",sign);

            String result = HttpClientUtils.sendPost(UrlMap.DEL_NOTICE, createJsonHeader(), param);
            if (null == result) {
                new RestfulResult(500,"公告接口通信失败");
            }
            JSONObject jsonObject = JSON.parseObject(result);
            if(!"success".equals(jsonObject.getString("status"))){
                new RestfulResult(500,jsonObject.getString("msg"));
            }
            return new RestfulResult(0,jsonObject.getString("msg"));
        } catch (Exception e) {
            log.error("公告删除异常", e);
            return new RestfulResult(500,"公告删除异常");
        }
    }

    /**
     * @author ：SYC
     * @date   ：Created in 2020/6/17 8:51
     * @description：
     * @params:
     */
    @PostMapping("/login")
    @QdLogPlus(opDesc = "登录综合业务平台",opType = OpType.OTHER)
    public JSONObject login(@RequestBody String data) {
        System.out.println("Running*****************************");
        if(StrUtil.isEmpty(data)){
            new RestfulResult(500,"参数不能为空");
        }
        JSONObject result = new JSONObject();
        result.put("url","");
        result.put("type","error");
        try {
            JSONObject param = JSON.parseObject(data);
            String encodedData = param.getString("encodedData");
            String sign = param.getString("sign");
            if(StrUtil.isEmpty(encodedData) || StrUtil.isEmpty(sign)){
                result.put("msg","参数信息不完整！");
                return result;
            }

            //验签
            byte[] encodedDataStr = SignUtil.decode(encodedData);
            boolean pass = RSAUtil.verify(encodedDataStr,publicKey,sign);
            if(!pass){
                result.put("msg","验签失败");
                return result;
            }
            //调用api验证账号密码是否正确，返回一个url后边带着加密的参数和sign,url会自动登录云港通
            System.out.println("开始调用api:"+param);
            String object = HttpClientUtils.sendPost(UrlMap.LOGIN_URL,createJsonHeader(),param);
            System.out.println("调用api成功:"+JSON.parseObject(object));
            if (null == object) {
                result.put("msg","登录通信失败");
                return result;
            }
            return JSON.parseObject(object);
        } catch (Exception e) {
            log.error("登录异常",e);
            result.put("msg","登录异常");
            return result;
        }
    }

    private  Map<String, String> createJsonHeader() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json;charset=UTF-8");
        return headers;
    }


}
