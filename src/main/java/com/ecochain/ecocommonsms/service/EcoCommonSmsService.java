package com.ecochain.ecocommonsms.service;

import com.ecochain.ecocommonsms.beans.ReturnBody;
import com.ecochain.ecocommonsms.dao.SmsHistoryInfoDao;
import com.ecochain.ecocommonsms.entity.SmsHistoryInfo;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EcoCommonSmsService {

    public static final Integer SMS_SEND_MOBILE = 0;            //按照“,”分隔的手机号依次发送短信
    public static final Integer SMS_SEND_TAG = 1;               //按照人员标签发送短信

    @Value("${sms.url}")
    private String url;
    @Value("${sms.username}")
    private String username ;
    @Value("${sms.password}")
    private String password ;
    @Value("${sms.productid}")
    private String productid;

    @Autowired
    private SmsHistoryInfoDao smsHistoryInfoDao;


    private SensitiveWordFilterService sw = SensitiveWordFilterService.getInstance();

    public ReturnBody checkBeforeSend(ReturnBody rby,String type,String mobile,String content){
        if( String.valueOf(EcoCommonSmsService.SMS_SEND_MOBILE).equals(type) == false
                && String.valueOf(EcoCommonSmsService.SMS_SEND_TAG).equals(type) == false
                ){
            rby.setInfo("无效的type参数值");
            return rby;
        }

        if(EcoCommonSmsService.SMS_SEND_MOBILE == Integer.parseInt(type) &&
                StringUtils.isEmpty(mobile)){
            rby.setInfo("手机号码不能为空!");
            return rby;
        }

        if(StringUtils.isEmpty(content)){
            rby.setInfo("短信内容不能为空!");
            return rby;
        }

        List<SmsHistoryInfo> smsHistoryInfos = this.smsHistoryInfoDao.findByMobileOrderBySendTimeDesc(mobile);
        SmsHistoryInfo smsHistoryInfo = (smsHistoryInfos!=null && smsHistoryInfos.size()>0) ?
                smsHistoryInfos.get(0) :null;

        if(smsHistoryInfo!=null && System.currentTimeMillis()-60*1000-smsHistoryInfo.getSendTime().getTime()<0){
            rby.setInfo("短信发送频率过快，请稍后再发!");
            return rby;
        }
        if(smsHistoryInfos.size()>=50){
            rby.setInfo("发送上限已经达到50条，请明天再发!");
            return rby;
        }
        return rby;
    }

    public String sensitiveWordFilter(String content){
        return sw.filterInfo(content);
    }

    public ReturnBody SendSMS(ReturnBody rby,String type,String mobile,String content){
        SmsHistoryInfo smsHistoryInfo = SendRecordinfo(type,content,mobile);

        Map<String, String> bodyMap = new HashMap<String, String>();
        bodyMap.put("username", username);//用户名
        String startime = new SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis());
        String pass = DigestUtils.md5Hex(DigestUtils.md5Hex(password)+startime);
        bodyMap.put("tkey",  startime);
        bodyMap.put("password", pass);//加密后密码
        bodyMap.put("productid", productid);//产品id
        bodyMap.put("mobile", mobile);//号码
        bodyMap.put("content",  content);//内容
        bodyMap.put("xh",  "");
        String status = "0";
//        status  = HttpUtil.doPost(url,paramentMap);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        MediaType mediaType = MediaType.parseMediaType("application/x-www-form-urlencoded; charset=UTF-8");
        headers.setContentType(mediaType);
        headers.add("Accept", MediaType.APPLICATION_FORM_URLENCODED.toString());
        HttpEntity<Map<String, String>> requestEntity  = new HttpEntity<Map<String, String>>(bodyMap, headers);
//        status = restTemplate.postForObject(url, requestEntity, String.class);
        System.out.println("sms return code:"+status);
        if(status.contains(",")&&status.substring(0, 1).equals("1")){
            rby.setSucess(true);
            rby.setInfo("发送短信成功!");
            SetSuccess(smsHistoryInfo);
        }else{
            rby.setSucess(false);
            rby.setInfo("发送短信失败!");
        }
        return rby;
    }

    public SmsHistoryInfo SendRecordinfo(String type,String content,String mobile){
        SmsHistoryInfo smsInfo = new SmsHistoryInfo();
        smsInfo.setContent(content);
        smsInfo.setIsSuccess(0);
        smsInfo.setMobile(mobile);
        smsInfo.setSendTime(new Timestamp(System.currentTimeMillis()));
        smsInfo.setType(type);
        return this.smsHistoryInfoDao.save(smsInfo);
    }

    public void SetSuccess(SmsHistoryInfo smsInfo){
        smsInfo.setIsSuccess(1);
        this.smsHistoryInfoDao.save(smsInfo);
    }


    public List<SmsHistoryInfo> findBySenderIDOrderBySendTimeDesc(String senderID){
        return this.smsHistoryInfoDao.findBySenderIDOrderBySendTimeDesc(Integer.parseInt(senderID));
    }
}
