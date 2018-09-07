package com.huiling;

import com.huiling.webpocket.service.LoginService;
import com.huiling.webpocket.service.WxMsgService;
import com.huiling.webpocket.utils.WxMsgUtil;

import com.huiling.webpocket.utils.weixin.mp.aes.AesException;
import com.huiling.weixin.mp.aes.WXBizMsgCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;

import javax.xml.parsers.ParserConfigurationException;

import static com.huiling.WxMsgUtil.createDocument;
import static com.huiling.WxMsgUtil.getTextContent;


/**
 * @author fei
 */
@RestController
class WxMessageController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WxMessageController.class);

    @Autowired
    private WXBizMsgCrypt wxcpt;


    @Autowired
    private WxMsgService wxMsgService;


    /**
     * 填写的URL需要正确响应企业微信验证URL的请求。
     *
     * @param msg_signature
     * @param timestamp
     * @param nonce
     * @param echostr
     * @return
     * @throws AesException
     */
    @RequestMapping("handleMsg2")
    String printMsg(String msg_signature, String timestamp, String nonce, String echostr) throws AesException {

        System.out.println(msg_signature);
        System.out.println(timestamp);
        System.out.println(nonce);
        System.out.println(echostr);

        //需要返回的明文
        String sEchoStr = null;
        try {
            sEchoStr = wxcpt.VerifyURL(msg_signature, timestamp, nonce, echostr);
            System.out.println("verifyurl echostr: " + sEchoStr);
            // 验证URL成功，将sEchoStr返回
            // HttpUtils.SetResponse(sEchoStr);
        } catch (Exception e) {
            // 验证URL失败，错误原因请查看异常
            e.printStackTrace();
        }

        return sEchoStr;
    }

    /**
     * 接收微信消息，并响应
     *
     */
    @RequestMapping("handleMsg")
    String handleMsg(String msg_signature, String timestamp, String nonce, @RequestBody String sReqData)
            throws AesException, ParserConfigurationException {

//        LOGGER.info("msg_signature: ",msg_signature);
//        LOGGER.info("timestamp: ",timestamp);
//        LOGGER.info("nonce: ",nonce);
//        LOGGER.info("sReqData: ",sReqData);

        String sMsg = wxcpt.DecryptMsg(msg_signature, timestamp, nonce, sReqData);
        LOGGER.info("after decrypt msg: {}", sMsg);

        // 解析明文xml标签,根据消息类型以及内容生成回复的xml文本
        String respData = responseRegular(sMsg);

        LOGGER.info("回复消息: {}", respData);
        return wxcpt.EncryptMsg(respData);

    }

    private String responseRegular(String msg) throws ParserConfigurationException {
        Document doc = createDocument(msg);
        String msgType = getTextContent(doc, "MsgType");

        String respText = "";

        if (msgType.equals("text")) {
            String content = getTextContent(doc, "Content");

            // 设置回复规则
            int i = 0;
            i = content.matches("1") ? 1 : i;// 登录信息
            i = content.matches("2") ? 2 : i;// 获取短信验证码
            i = content.matches("3") ? 3 : i;// 获取cookie
            i = content.matches("4") ? 4 : i;// 强制为登录状态
            i = content.matches("5") ? 5 : i;//
            i = content.matches("\\d{6}") ? 6 : i;// 获取短信验证码
            i = content.matches("^COOKIE:.*") ? 7 : i;// 设置Cookie
            switch (i) {
                case 1:
                    respText = LoginService.getLoginInfo();
                    break;
                case 2:
                    respText = LoginService.sendSms();
                    break;
                case 3:
                    respText = LoginService.getCookieInfo();
                    break;
                case 4:
                    LoginService.mIsLogin = true;
                    respText = "已强制为登录状态";
                    break;
                case 5:
                    respText = "123456";
                    break;
                case 6:
                    if (!LoginService.mIsLogin) {
                        respText = LoginService.login(content);
                    } else {
                        respText = "现在已经是登录状态！无需验证码。";
                    }
                    break;
                case 7:
                    respText = LoginService.setCookieInfo(content.substring(7));
                    break;
                default:
                    respText = "未识别您的指令！请重新输入 ^_^";
            }
            return WxMsgUtil.createTextRespXml(msg, respText);

//        } else if (msgType.equals("event")) {
//            String eventKey = getTextContent(doc, "EventKey");
//            respText = eventKey;
        }
        return respText;

    }

}