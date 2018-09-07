package com.huiling;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import static com.huiling.webpocket.utils.WxMsgUtil.createFileMsg;
import static com.huiling.webpocket.utils.WxMsgUtil.createTextMsg;

/**
 * @author fei
 */
@Service
public class WxMediaService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WxMediaService.class);

    private static ObjectMapper mapper = new ObjectMapper();

    public String access_token;

    @Value("${token}")
    private String token;
    @Value("${corpID}")
    private String corpID;
    @Value("${encodingAESKey}")
    private String encodingAESKey;

    @Value("${agentId}")
    private Integer agentId;
    @Value("${secret}")
    private String secret;


    @Value("${admin-user}")
    private String adminUser;
    @Value("${maintainer-user}")
    private String maintainerUser;







    public String uploadMedia(String wordPath) {
        return uploadMedia(access_token, "file", wordPath);
    }

    /**
     * 请求方式：POST（HTTPS）
     * 请求地址：https://qyapi.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE
     * 使用multipart/form-data POST上传文件， 文件标识名为”media”
     * form-data中媒体文件标识，有filename、filelength、content-type等信息
     * <p>
     * 上传媒体文件
     *
     * @param accessToken  接口访问凭证
     * @param type         媒体文件类型，分别有图片（image）、语音（voice）、视频（video），普通文件(file)
     * @param mediaFileUrl 媒体文件的url
     *                     上传的媒体文件限制
     *                     图片（image）:1MB，支持JPG格式
     *                     语音（voice）：2MB，播放长度不超过60s，支持AMR格式
     *                     视频（video）：10MB，支持MP4格式
     *                     普通文件（file）：10MB
     */
    public static String uploadMedia(String accessToken, String type, String mediaFileUrl) {
        // 拼装请求地址
        String uploadMediaUrl = "https://qyapi.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";
        uploadMediaUrl = uploadMediaUrl.replace("ACCESS_TOKEN", accessToken).replace("TYPE", type);

        // 定义数据分隔符
        String boundary = "------------7da2e536604c8";
        try {
            URL uploadUrl = new URL(uploadMediaUrl);
            HttpURLConnection uploadConn = (HttpURLConnection) uploadUrl.openConnection();
            uploadConn.setDoOutput(true);
            uploadConn.setDoInput(true);
            uploadConn.setRequestMethod("POST");
            // 设置请求头Content-Type
            uploadConn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            // 获取媒体文件上传的输出流（往微信服务器写数据）
            OutputStream outputStream = uploadConn.getOutputStream();

//            URL mediaUrl = new URL(mediaFileUrl);
//            HttpURLConnection meidaConn = (HttpURLConnection) mediaUrl.openConnection();
//            meidaConn.setDoOutput(true);
//            meidaConn.setRequestMethod("GET");
//
//            // 从请求头中获取内容类型
//            String contentType = meidaConn.getHeaderField("Content-Type");
//            // 根据内容类型判断文件扩展名
            String filename = new File(mediaFileUrl).getName();
            // 请求体开始
            outputStream.write(("--" + boundary + "\r\n").getBytes());
            outputStream.write(String.format("Content-Disposition: form-data; name=\"media\"; filename=\"%s\"\r\n", filename).getBytes());
            outputStream.write(String.format("Content-Type: %s\r\n\r\n", MediaType.APPLICATION_OCTET_STREAM_VALUE).getBytes());
//
            FileInputStream fis = new FileInputStream(mediaFileUrl);
            // 获取媒体文件的输入流（读取文件）
            BufferedInputStream bis = new BufferedInputStream(fis);
            byte[] buf = new byte[8096];
            int size;
            while ((size = bis.read(buf)) != -1) {
                // 将媒体文件写到输出流（往微信服务器写数据）
                outputStream.write(buf, 0, size);
            }
            // 请求体结束
            outputStream.write(("\r\n--" + boundary + "--\r\n").getBytes());
            outputStream.close();
            bis.close();
//            meidaConn.disconnect();

            // 获取媒体文件上传的输入流（从微信服务器读数据）
            InputStream inputStream = uploadConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer buffer = new StringBuffer();
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            // 释放资源
            inputStream.close();
            uploadConn.disconnect();

            // 修改
            ObjectMapper mapper = new ObjectMapper();
            Map map = mapper.readValue(buffer.toString(), Map.class);
            if (map.get("errcode").equals(0)) {
                LOGGER.info("上传成功！==> media_id:{}", map.get("media_id"));

                return map.get("media_id").toString();
            } else {
                LOGGER.info("上传失败!");
                LOGGER.info("errcode:{}", map.get("errcode"));
                LOGGER.info("errmsg:{}", map.get("errmsg"));
                LOGGER.info("type:{}", map.get("type"));
                LOGGER.info("media_id:{}", map.get("media_id"));
                LOGGER.info("created_at:{}", map.get("created_at"));
            }
            // 使用JSON-lib解析返回结果
//            JSONObject jsonObject = JSONObject.fromObject(buffer.toString());
            // 测试打印结果
            // System.out.println("打印测试结果"+jsonObject);
//            weixinMedia = new WeixinMedia();
//            weixinMedia.setType(jsonObject.getString("type"));
            // type等于 缩略图（thumb） 时的返回结果和其它类型不一样
//            if ("thumb".equals(type))
//                weixinMedia.setMediaId(jsonObject.getString("thumb_media_id"));
//            else
//                weixinMedia.setMediaId(jsonObject.getString("media_id"));
//            weixinMedia.setCreatedAt(jsonObject.getInt("created_at"));
        } catch (Exception e) {
//
            LOGGER.info("上传媒体文件失败：{}", e);
        }
        return "";
    }
}

