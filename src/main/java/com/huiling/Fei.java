package com.huiling;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * @author fei
 */
public class Fei {
    private static final Logger LOGGER = LoggerFactory.getLogger(Fei.class);

    public static String date() {
        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        return format.format(new Date());
    }

    public static String dateCN() {
        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH：mm：ss");
        return format.format(new Date());
    }

    public static Long minuteDifferentNow(Date fromDate) {
        return minuteDifferent(fromDate, new Date());
    }

    public static Long minuteDifferent(Date fromDate, Date toDate) {
        long toDateTime = toDate.getTime();
        long fromDateTime = fromDate.getTime();

        return (toDateTime - fromDateTime) / (1000 * 60);
    }

    public static String getCharset(String str) {
        String[] charset = new String[]{"UTF-8", "GBK", "GB2312", "ISO-8859-1"};
        byte[] bytes;
        String result;
        try {
            for (int i = 0; i < charset.length; i++) {
                for (int j = 0; j < charset.length; j++) {
                    if (i == j) {
                        continue;
                    }
                    bytes = str.getBytes(charset[i]);
                    result = new String(bytes, charset[j]);
                    System.out.println(str + " " + charset[i] + " \t" + charset[j] + " \t" + result);

                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String getCharsetSquare(String str) {
        String[] charset = new String[]{"UTF-8", "GBK", "GB2312", "ISO-8859-1"};
        byte[] bytes;
        String result;
        String result2;
        try {
            for (int i = 0; i < charset.length; i++) {
                for (int j = 0; j < charset.length; j++) {
                    if (i == j) {
                        continue;
                    }
                    bytes = str.getBytes(charset[i]);
                    result = new String(bytes, charset[j]);
//                    System.out.println(str + " " + charset[i] + " \t" + charset[j] + " \t" + result);

                    for (int k = 0; k < charset.length; k++) {
                        if (k == j) {
                            continue;
                        }
                        for (int l = 0; l < charset.length; l++) {
                            if (l == k) {
                                continue;
                            }
                            bytes = result.getBytes(charset[k]);
                            result2 = new String(bytes, charset[l]);
                            System.out.println(str + " " + charset[i] + " \t" + charset[j] + " \t" + result + " \t" + charset[k] + " \t" + charset[l] + " \t" + result2);

                        }
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static List<String> toCookieList(String cookie) {
        String[] coo = cookie.split("; ");
        List<String> list = Arrays.asList(coo);
        return new ArrayList<>(list);
    }

    public static String toCookieStr(List<String> cookie) {
        StringBuilder coo = new StringBuilder();
        for (String s : cookie) {
            coo.append(s).append("; ");
        }
        return coo.substring(0, coo.length() - 2);
    }

    public static String saveFile(String path, byte[] data) {
        if (data != null) {
            File file = new File(path);
            try {
                if (!file.exists() && !file.getParentFile().exists()) {
                    boolean result = file.getParentFile().mkdirs();
                    if (!result) {
                        LOGGER.info("文件目录创建失败！");
                        return "";
                    }
                }
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(data);
                fos.flush();
                fos.close();
                LOGGER.info("文件保存成功！");
                return file.getAbsolutePath();

            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.info("文件保存失败！");
            }
        } else {
            LOGGER.info("数据为空！");
        }
        return "";
    }

    /**
     * 把原始的xml转换成map key为节点的路径，值为节点值 ，遇到重复节点用”[数字]“标识第几个节点
     * 如:<xml><list><test>test1</test><test>test2</test></list></xml> 会转换成
     * /xml/list[1]/test=test1 /xml/list[2]/test=test2
     *
     * @param message
     * @return
     */
    public static Map<String, String> xml2Map(String message) {
        Map<String, String> data = new LinkedHashMap<String, String>();
        Document doc = null;
        try {
            doc = DocumentHelper.parseText(message);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        Element root = doc.getRootElement();
        String path = "/" + root.getName();
        element2Map(root, data, path);
        return data;
    }

    private static void element2Map(Element ele, Map<String, String> data, String path) {
        if (ele == null) {
            return;
        }
        List<Element> childrens = ele.elements();
        if (childrens != null && childrens.size() > 0) {
            Element pre = null;
            Element cur = null;
            Element next = null;
            int nodeIndex = 1;
            int length = childrens.size();
            for (int i = 0; i < length; i++) {
                cur = childrens.get(i);
                String nodePath = path + "/" + cur.getName();
                if (pre == null) {
                    next = childrens.get(i + 1);
                    if (next.getName().equals(cur.getName())) {
                        nodePath += "[" + nodeIndex + "]";
                        nodeIndex++;
                    }
                } else {
                    if (pre.getName().equals(cur.getName())) {
                        nodePath += "[" + nodeIndex + "]";
                        nodeIndex++;
                    } else {
                        nodeIndex = 1;
                    }
                }
                element2Map(cur, data, nodePath);
                pre = cur;
            }
        } else {

            data.put(path, ele.getText());

        }

    }

    /**
     * 把map转换成xml
     *
     * @param map
     * @return
     */
    public static String map2Xml(Map<String, String> map) {
        String xml = "";
//根据map用dom4j创建Document
        Document doc = DocumentHelper.createDocument();
        Iterator<Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, String> e = it.next();
            String key = e.getKey();
            String value = e.getValue();

            Element ele = DocumentHelper.makeElement(doc, key);
            ele.setText(value);
        }

//格式化
        OutputFormat format = OutputFormat.createPrettyPrint();
//format.setNewLineAfterDeclaration(true);
        format.setExpandEmptyElements(false);
        StringWriter out = new StringWriter();
        XMLWriter writer = new XMLWriter(out, format);
        try {
            writer.write(doc);
        } catch (IOException e) {
            e.printStackTrace();
        }
        xml = out.toString();

        System.out.println(xml);
        xml = xml.replaceAll("\\[\\d*\\]", "");
        return xml;
    }

    public static String timestamp() {
        return String.valueOf(System.currentTimeMillis());
    }

    public static String nonce() {
        return String.valueOf(new Random(8).nextInt());
    }

}
