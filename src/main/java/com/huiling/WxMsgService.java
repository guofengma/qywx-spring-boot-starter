package com.huiling;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huiling.webpocket.domain.AccessToken;
import com.huiling.webpocket.utils.weixin.mp.aes.WXBizMsgCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

import static com.huiling.WxMsgUtil.createFileMsg;
import static com.huiling.WxMsgUtil.createTextMsg;


/**
 * @author fei
 * <p>
 * 接收微信消息并回复
 * 主动发送消息
 */
@Service
public class WxMsgService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WxMsgService.class);

    private static ObjectMapper mapper = new ObjectMapper();

    public String access_token;

    @Autowired
    private RestTemplate template;

    /**
     * 发送文本消息
     *
     * @param content 文本消息内容
     * @return errmsg
     */
    public String sendTextMsg(String content) {
//        try {
//            content = new String(content.getBytes(), "ISO-8859-1");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        Map<String, Object> textMsg = createTextMsg(maintainerUser, null, null, content, 0);
        return sendMsg(textMsg);
    }


    /**
     * 通过微信发送消息
     *
     * @param msg 消息内容
     * @return errcode
     */
    private String sendMsg(Map<String, Object> msg) {
        LOGGER.info("sendMsg:" + msg);

        if (access_token == null) {
            return "access_token为空！";
        }

        String url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=" + access_token;

        Map body = template.postForObject(url, msg, Map.class);

        if (body != null) {
            LOGGER.info(body.toString());

            Integer errcode = (Integer) body.get("errcode");
            String errmsg = (String) body.get("errmsg");
//            String invaliduser = (String) body.get("invaliduser");
//            String invalidparty = (String) body.get("invalidparty");
//            String invalidtag = (String) body.get("invalidtag");

            if (errcode.equals(0)) {
                LOGGER.info("发送成功！");
            }
            return errmsg;
        }
        return "微信响应为空！";
    }


}

