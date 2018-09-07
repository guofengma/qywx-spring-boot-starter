package com.huiling;


import com.huiling.weixin.mp.aes.AesException;
import com.huiling.weixin.mp.aes.WXBizMsgCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.net.URL;
import java.util.Map;


/**
 * @author fei
 */
@Configuration
@ConditionalOnClass()
@EnableAutoConfiguration
public class WxAutoConfigure {

    @Autowired
    private CorpWxProperties corpWxProperties;

    @Bean
    public WXBizMsgCrypt wxcpt() throws AesException {
        return new WXBizMsgCrypt(corpWxProperties.getToken(), corpWxProperties.getEncodingAESKey(),
                corpWxProperties.getCorpID());
    }

    @Scheduled(cron = "0 0/60 * * * ?")
    public void getAccessToken() {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=" + corpID + "&corpsecret=" + secret;

        ObjectMapper mapper = new ObjectMapper();

        //        方式一：
//        RestTemplate template = new RestTemplate();
//        HttpEntity<String> request = new HttpEntity<>(new HttpHeaders());
//        String response = template.postForObject(url, request, String.class);
//        LOGGER.info(response);
//        try {
//            accessToken = mapper.readValue(response, AccessToken.class);
//            LOGGER.info("获取的{}", accessToken);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        方式二：
        try {
            Map<String, Object> value = mapper.readValue(new URL(url), Map.class);
            LOGGER.info("获取的{}", value);

            Integer errcode = (Integer) value.get("errcode");
            if (errcode == 0) {
                access_token = (String) value.get("access_token");
                Integer expires_in = (Integer) value.get("expires_in");
                LOGGER.info("凭证的有效时间={}秒", expires_in);
            } else {
                String errmsg = (String) value.get("errmsg");
                LOGGER.info("errmsg={}", errmsg);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
