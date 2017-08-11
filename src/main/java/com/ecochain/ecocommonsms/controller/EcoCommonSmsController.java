package com.ecochain.ecocommonsms.controller;

import com.ecochain.ecocommonsms.entity.SmsHistoryInfo;
import com.ecochain.ecocommonsms.service.EcoCommonSmsService;
import com.ecochain.ecocommonsms.beans.ReturnBody;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/sms")
public class EcoCommonSmsController {

    @Autowired
    private EcoCommonSmsService ecoCommonSmsService;

    @ApiOperation(value = "发送短信接口", notes = "发送短信例如：http://127.0.0.1:8081/sms/send?type=1&content=沙快速习大大啥都看按时打卡&mobile=18001234567")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "type", value = "发送短信类型", dataType = "String", required = true),
            @ApiImplicitParam(paramType = "query", name = "mobile", value = "手机号码", dataType = "String", required = false),
            @ApiImplicitParam(paramType = "query", name = "content", value = "短信内容", dataType = "String", required = true)
    })
    @RequestMapping(value = "/send", method = RequestMethod.GET)
    @ResponseBody
    public String SendAPI(@RequestParam(value = "type", required = true) String type,
            @RequestParam(value = "mobile", required = false) String mobile,
            @RequestParam(value = "content", required = true) String content ){
        String returnJson = "";
        ReturnBody rby = new ReturnBody();
        rby.setSucess(false);

        rby = ecoCommonSmsService.checkBeforeSend(rby,type,mobile,content);
        if(StringUtils.isBlank(rby.getInfo())){
            System.out.println("发送原文："+content);
            content = ecoCommonSmsService.sensitiveWordFilter(content);
            System.out.println("敏感字筛查后："+content);
            rby = ecoCommonSmsService.SendSMS(rby,type,mobile,content);
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            returnJson = mapper.writeValueAsString(rby);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return  returnJson;
    }

    @ApiOperation(value = "查询发送方最近5条短信", notes = "查询发送方最近5条短信例如：http://127.0.0.1:8081/sms/query/last5?senderID=1")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "senderID", value = "发送方ID", dataType = "String", required = true)
    })
    @RequestMapping(value = "/query/last5", method = RequestMethod.GET)
    @ResponseBody
    public String queryLast5(@RequestParam(value = "senderID", required = true) String senderID){
        String json = "";
        List<SmsHistoryInfo> smsHistoryInfos = this.ecoCommonSmsService.findBySenderIDOrderBySendTimeDesc(senderID);
        if(smsHistoryInfos == null || smsHistoryInfos.size()==0){
            return json;
        }
        ObjectMapper mapper = new ObjectMapper();

        try {
           json =  mapper.writeValueAsString(smsHistoryInfos);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }
}
