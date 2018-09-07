package com.huiling;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fei
 */
public class WxMsgUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(WxMsgUtil.class);

    private static ObjectMapper mapper = new ObjectMapper();


    private static Integer agentid;

    public static Document createDocument(String sMsg) {

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            StringReader sr = new StringReader(sMsg);
            InputSource is = new InputSource(sr);

            return db.parse(is);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建初始消息，
     *
     * @param touser  成员ID列表（消息接收者，多个接收者用‘|’分隔，最多支持1000个）。特殊情况：指定为@all，则向关注该企业应用的全部成员发送
     * @param toparty 部门ID列表，多个接收者用‘|’分隔，最多支持100个。当touser为@all时忽略本参数
     * @param totag   标签ID列表，多个接收者用‘|’分隔，最多支持100个。当touser为@all时忽略本参数
     * @param msgtype 消息类型
     * @param agentid 企业应用的id，整型。可在应用的设置页面查看
     * @param msg     消息内容
     * @param safe    表示是否是保密消息，0表示否，1表示是，默认0
     * @return Map类型的消息
     */
    public static Map<String, Object> createMsg(String touser, String toparty, String totag, String msgtype,
                                                Integer agentid, Map<String, Object> msg, Integer safe) {

        Map<String, Object> map = new HashMap<>();
        map.put("touser", touser);
        map.put("toparty", toparty);
        map.put("totag", totag);
        map.put("msgtype", msgtype);
        map.put("agentid", agentid);
        map.put(msgtype, msg);
        map.put("safe", safe);

        return map;
    }

    /**
     * 文本消息：
     * {
     * "touser" : "UserID1|UserID2|UserID3",
     * "toparty" : "PartyID1|PartyID2",
     * "totag" : "TagID1 | TagID2",
     * "msgtype" : "text",
     * "agentid" : 1,
     * "text" : {
     * "content" : "你的快递已到，请携带工卡前往邮件中心领取。\n出发前可查看<a href=\"http://work.weixin.qq.com\">邮件中心视频实况</a>，聪明避开排队。"
     * },
     * "safe":0
     * }
     */
    public static Map<String, Object> createTextMsg(String touser, String toparty, String totag, String content, Integer safe) {

        Map<String, Object> text = new HashMap<>();
        text.put("content", content);
        return createMsg(touser, toparty, totag, "text", agentid, text, safe);
    }

    /**
     * 图片消息:
     * {
     * "touser" : "UserID1|UserID2|UserID3",
     * "toparty" : "PartyID1|PartyID2",
     * "totag" : "TagID1 | TagID2",
     * "msgtype" : "image",
     * "agentid" : 1,
     * "image" : {
     * "media_id" : "MEDIA_ID"
     * },
     * "safe":0
     * }
     */
    public static Map<String, Object> createImageMsg(String touser, String toparty, String totag, String media_id, Integer safe) {

        Map<String, Object> image = new HashMap<>();
        image.put("media_id", media_id);
        return createMsg(touser, toparty, totag, "image", agentid, image, safe);
    }

    /**
     * 语音消息：
     * {
     * "touser" : "UserID1|UserID2|UserID3",
     * "toparty" : "PartyID1|PartyID2",
     * "totag" : "TagID1 | TagID2",
     * "msgtype" : "voice",
     * "agentid" : 1,
     * "voice" : {
     * "media_id" : "MEDIA_ID"
     * }
     * }
     */
    public static Map<String, Object> createVoiceMsg(String touser, String toparty, String totag, String media_id) {

        Map<String, Object> voice = new HashMap<>();
        voice.put("media_id", media_id);
        Map<String, Object> map = createMsg(touser, toparty, totag, "voice", agentid, voice, 0);
        map.remove("safe");
        return map;
    }

    /**
     * 视频消息:
     * {
     * "touser" : "UserID1|UserID2|UserID3",
     * "toparty" : "PartyID1|PartyID2",
     * "totag" : "TagID1 | TagID2",
     * "msgtype" : "video",
     * "agentid" : 1,
     * "video" : {
     * "media_id" : "MEDIA_ID",
     * "title" : "Title",
     * "description" : "Description"
     * },
     * "safe":0
     * }
     */
    public static Map<String, Object> createVideoMsg(String touser, String toparty, String totag,
                                                     String media_id, String title, String description, Integer safe) {

        Map<String, Object> video = new HashMap<>();
        video.put("media_id", media_id);
        video.put("title", title);
        video.put("description", description);
        return createMsg(touser, toparty, totag, "video", agentid, video, safe);
    }


    /**
     * 文件消息:
     * <p>
     * {
     * "touser" : "UserID1|UserID2|UserID3",
     * "toparty" : "PartyID1|PartyID2",
     * "totag" : "TagID1 | TagID2",
     * "msgtype" : "file",
     * "agentid" : 1,
     * "file" : {
     * "media_id" : "1Yv-zXfHjSjU-7LH-GwtYqDGS-zz6w22KmWAT5COgP7o"
     * },
     * "safe":0
     * }
     *
     * @return
     */
    public static Map<String, Object> createFileMsg(String touser, String toparty, String totag, String media_id, Integer safe) {

        Map<String, Object> file = new HashMap<>();
        file.put("media_id", media_id);
        return createMsg(touser, toparty, totag, "file", agentid, file, safe);
    }

    /**
     * 文本卡片消息:
     * {
     * "touser" : "UserID1|UserID2|UserID3",
     * "toparty" : "PartyID1 | PartyID2",
     * "totag" : "TagID1 | TagID2",
     * "msgtype" : "textcard",
     * "agentid" : 1,
     * "textcard" : {
     * "title" : "领奖通知",
     * "description" : "<div class=\"gray\">2016年9月26日</div> <div class=\"normal\">恭喜你抽中iPhone 7一台，领奖码：xxxx</div><div class=\"highlight\">请于2016年10月10日前联系行政同事领取</div>",
     * "url" : "URL",
     * "btntxt":"更多"
     * }
     * }
     */
    public static Map<String, Object> createTextCardMsg(String touser, String toparty, String totag,
                                                        String title, String description, String url, String btntxt) {

        Map<String, Object> textcard = new HashMap<>();
        textcard.put("title", title);
        textcard.put("description", description);
        textcard.put("url", url);
        textcard.put("btntxt", btntxt);
        Map<String, Object> map = createMsg(touser, toparty, totag, "textcard", agentid, textcard, 0);
        map.remove("safe");
        return map;
    }

    /**
     * 图文消息:
     * {
     * "touser" : "UserID1|UserID2|UserID3",
     * "toparty" : "PartyID1 | PartyID2",
     * "totag" : "TagID1 | TagID2",
     * "msgtype" : "news",
     * "agentid" : 1,
     * "news" : {
     * "articles" : [
     * {
     * "title" : "中秋节礼品领取",
     * "description" : "今年中秋节公司有豪礼相送",
     * "url" : "URL",
     * "picurl" : "http://res.mail.qq.com/node/ww/wwopenmng/images/independent/doc/test_pic_msg1.png",
     * "btntxt":"更多"
     * }
     * ]
     * }
     * }
     */
    public static Map<String, Object> createNewsMsg(String touser, String toparty, String totag, String title,
                                                    String url, String description, String picurl, String btntxt) {

        Map<String, Object> news = new HashMap<>();
        news.put("title", title);
        news.put("url", url);
        news.put("description", description);
        news.put("picurl", picurl);
        news.put("btntxt", btntxt);
        Map<String, Object> map = createMsg(touser, toparty, totag, "news", agentid, news, 0);
        map.remove("safe");
        return map;
    }

    /**
     * 图文消息（mpnews）
     * mpnews类型的图文消息，跟普通的图文消息一致，唯一的差异是图文内容存储在企业微信。
     * 多次发送mpnews，会被认为是不同的图文，阅读、点赞的统计会被分开计算。
     * {
     * "touser" : "UserID1|UserID2|UserID3",
     * "toparty" : "PartyID1 | PartyID2",
     * "totag": "TagID1 | TagID2",
     * "msgtype" : "mpnews",
     * "agentid" : 1,
     * "mpnews" : {
     * "articles":[
     * {
     * "title": "Title",
     * "thumb_media_id": "MEDIA_ID",
     * "author": "Author",
     * "content_source_url": "URL",
     * "content": "Content",
     * "digest": "Digest description"
     * }
     * ]
     * },
     * "safe":0
     * }
     */
    public static Map<String, Object> createTextMsg(String touser, String toparty, String totag, String media_id, Integer safe) {

        Map<String, Object> map = new HashMap<>();
        map.put("touser", touser);
        map.put("toparty", toparty);
        map.put("totag", totag);
        map.put("msgtype", "video");
        map.put("agentid", agentid);
        Map<String, String> image = new HashMap<>();
        image.put("media_id", media_id);
        image.put("title", title);
        image.put("description", description);
        map.put("video", image);

        return map;
    }

    public static Map<String, Object> createTextMsg(String touser, String toparty, String totag, String media_id, Integer safe) {

        Map<String, Object> map = new HashMap<>();
        map.put("touser", touser);
        map.put("toparty", toparty);
        map.put("totag", totag);
        map.put("msgtype", "video");
        map.put("agentid", agentid);
        Map<String, String> image = new HashMap<>();
        image.put("media_id", media_id);
        image.put("title", title);
        image.put("description", description);
        map.put("video", image);

        return map;
    }

    /**
     * 小程序通知消息:
     * 小程序通知消息只允许小程序应用发送，消息会通过【小程序通知】发送给用户。
     * 小程序应用仅支持发送小程序通知消息，暂不支持文本、图片、语音、视频、图文等其他类型的消息。
     * 不支持@all全员发送
     * {
     * "touser" : "zhangsan|lisi",
     * "toparty": "1|2",
     * "totag": "1|2",
     * "msgtype" : "miniprogram_notice",
     * "miniprogram_notice" : {
     * "appid": "wx123123123123123",
     * "page": "pages/index?userid=zhangsan&orderid=123123123",
     * "title": "会议室预订成功通知",
     * "description": "4月27日 16:16",
     * "emphasis_first_item": true,
     * "content_item": [
     * {
     * "key": "会议室",
     * "value": "402"
     * },
     * {
     * "key": "会议地点",
     * "value": "广州TIT-402会议室"
     * },
     * {
     * "key": "会议时间",
     * "value": "2018年8月1日 09:00-09:30"
     * },
     * {
     * "key": "参与人员",
     * "value": "周剑轩"
     * }
     * ]
     * }
     * }
     */
    public static Map<String, Object> createTextMsg(String touser, String toparty, String totag, String media_id, Integer safe) {

        Map<String, Object> map = new HashMap<>();
        map.put("touser", touser);
        map.put("toparty", toparty);
        map.put("totag", totag);
        map.put("msgtype", "video");
        map.put("agentid", agentid);
        Map<String, String> image = new HashMap<>();
        image.put("media_id", media_id);
        image.put("title", title);
        image.put("description", description);
        map.put("video", image);

        return map;
    }

    public static String getTextContent(Document doc, String tagName) {
        NodeList nodelist = doc.getDocumentElement().getElementsByTagName(tagName);
        return nodelist.item(0).getTextContent();
    }


    public static String createTextRespXml(String sXmlMsg, String respText) throws ParserConfigurationException {

        Document doc = createDocument(sXmlMsg);

        String toUser = getTextContent(doc, "ToUserName");
        String fromUser = getTextContent(doc, "FromUserName");
        getTextContent(doc, "CreateTime");
        getTextContent(doc, "MsgType");
        getTextContent(doc, "Content");
        getTextContent(doc, "MsgId");
        getTextContent(doc, "AgentID");


        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.newDocument();
        Element xml = document.createElement("xml");

        Element toUserName = document.createElement("ToUserName");
        toUserName.appendChild(document.createCDATASection(fromUser));

        Element fromUserName = document.createElement("FromUserName");
        fromUserName.appendChild(document.createCDATASection(toUser));

        Element createTime = document.createElement("CreateTime");
        createTime.setTextContent(Fei.timestamp());

        Element msgType = document.createElement("MsgType");
        msgType.appendChild(document.createCDATASection("text"));

        Element content = document.createElement("Content");
        content.appendChild(document.createCDATASection(respText));

        xml.appendChild(toUserName);
        xml.appendChild(fromUserName);
        xml.appendChild(createTime);
        xml.appendChild(msgType);
        xml.appendChild(content);

        document.appendChild(xml);

        return domToStr(document);
    }

    public static Document MapToXml(Map<String, Object> map) throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.newDocument();


        Element xml = document.createElement("xml");

        Element toUserName = document.createElement("ToUserName");
        toUserName.appendChild(document.createCDATASection(fromUser));

        Element fromUserName = document.createElement("FromUserName");
        fromUserName.appendChild(document.createCDATASection(toUser));

        Element createTime = document.createElement("CreateTime");
        createTime.setTextContent(Fei.timestamp());

        Element msgType = document.createElement("MsgType");
        msgType.appendChild(document.createCDATASection("text"));

        Element content = document.createElement("Content");
        content.appendChild(document.createCDATASection(respText));

        xml.appendChild(toUserName);
        xml.appendChild(fromUserName);
        xml.appendChild(createTime);
        xml.appendChild(msgType);
        xml.appendChild(content);

        document.appendChild(xml);
    }

    public static String XmlToMap(String Xml) {

    }


    public static String createRespData(Map<String, String> respDataMap) throws ParserConfigurationException, TransformerException {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.newDocument();
        Element xml = document.createElement("xml");

        for (Map.Entry<String, String> entry : respDataMap.entrySet()) {
            Element element = document.createElement(entry.getKey());
            element.appendChild(document.createCDATASection(entry.getValue()));
            xml.appendChild(element);
        }

        document.appendChild(xml);

        return domToStr(document);
    }

    /**
     * 创建文本回复数据
     *
     * @param toUserNameText
     * @param fromUserNameText
     * @param respText
     * @return
     * @throws TransformerException
     * @throws ParserConfigurationException
     */
    public static String createTextRespData(String toUserNameText, String fromUserNameText, String respText)
            throws TransformerException, ParserConfigurationException {

        Map<String, String> map = new HashMap<>();
        map.put("ToUserName", toUserNameText);
        map.put("FromUserName", fromUserNameText);
        map.put("CreateTime", Fei.timestamp());
        map.put("MsgType", "text");
        map.put("Content", respText);

        return createRespData(map);
    }

    /**
     * 创建图片回复数据
     *
     * @param toUserNameText
     * @param fromUserNameText
     * @param respText
     * @return
     * @throws TransformerException
     * @throws ParserConfigurationException
     */
    public static String createImageRespData(String toUserNameText, String fromUserNameText, String respText)
            throws TransformerException, ParserConfigurationException {

        Map<String, String> map = new HashMap<>();
        map.put("ToUserName", toUserNameText);
        map.put("FromUserName", fromUserNameText);
        map.put("CreateTime", Fei.timestamp());
        map.put("MsgType", "text");
        map.put("Content", respText);

        return createRespData(map);
    }

    /**
     * 将Document实例转换为String
     *
     * @param document Document实例
     * @return 字符串
     */
    public static String domToStr(Document document) {

        try {
            Source source = new DOMSource(document);
            StringWriter stringWriter = new StringWriter();
            Result result = new StreamResult(stringWriter);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);
            return stringWriter.getBuffer().toString();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return "";
    }


}
