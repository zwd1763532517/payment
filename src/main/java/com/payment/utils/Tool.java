package com.payment.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.*;
import java.util.Map.Entry;

public class Tool {

    private static final Logger logger = LoggerFactory.getLogger(Tool.class);

    private static String inputCharset = "utf-8";
    private static PoolingHttpClientConnectionManager cm = null;
    private static CloseableHttpClient httpclient = null;

    public static String getInputCharset() {
        return inputCharset;
    }

    public static void setInputCharset(String inputCharset) {
        Tool.inputCharset = inputCharset;
    }

    public static CloseableHttpClient getHttpclient() {
        return httpclient;
    }

    public static void setHttpclient(CloseableHttpClient httpclient) {
        Tool.httpclient = httpclient;
    }

    /**
     *  初始化httpclient代码
     */
    public static void initHttpClient() {
        cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(100);//开100个链接
        httpclient = HttpClients.custom().setConnectionManager(cm).build();
    }

    /**
     * 销毁httpclient代码
     */
    public static void cleanHttpClient() {
        try {
            if (httpclient != null) {
                httpclient.close();
                httpclient = null;
            }
            if (cm != null) {
                cm.close();
                cm = null;
            }
        } catch (IOException e) {
            logger.info("====================cleanHttpClient=========================" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 将Map<String, Object>转换成Map<String, String>
     * @param properties 集合
     * @return
     */


    public static Map getParameterMap(Map properties) {// 返回值使用泛型时应该是Map<String,String[]>形式
        // 返回值Map
        Map returnMap = new HashMap();
        Iterator entries = properties.entrySet().iterator();
        Entry entry;
        String name = "";
        String value = "";
        while (entries.hasNext()) {
            entry = (Entry) entries.next();
            name = (String) entry.getKey();
            Object valueObj = entry.getValue();
            if (null == valueObj) {
                value = "";
            } else if (valueObj instanceof String[]) {
                String[] values = (String[]) valueObj;
                for (int i = 0; i < values.length; i++) {
                    // System.out.println(values[i]);
                    value = values[i] + ",";
                }
                value = value.substring(0, value.length() - 1);
            } else {
                value = valueObj.toString();
            }
            returnMap.put(name, value);
        }
        return returnMap;
    }

    /**
     * Bean --> Map 1: 利用Introspector和PropertyDescriptor 将Bean --> Map
     */
    public static Map<String, String> transBean2Map(Object obj) {

        if (obj == null) {
            return null;
        }
        Map map = new HashMap();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();

                // 过滤class属性
                if (!key.equals("class")) {
                    // 得到property对应的getter方法
                    Method getter = property.getReadMethod();
                    Object value = getter.invoke(obj);
                    if (value != null)// 去掉空值
                        map.put(key, value);
                }
            }
        } catch (Exception e) {
            logger.error("=================transBean2Map=================", e.getMessage());
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     *
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    public static String createLinkString(Map<String, String> params) {

        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        StringBuffer prestr = new StringBuffer();

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);

            if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                prestr.append(key).append("=").append(value);
            } else {
                prestr.append(key).append("=").append(value).append("&");
            }
        }
        logger.info("===========createLinkString===========" + prestr);
        return prestr.toString();
    }

    /**
     * 分转元
     */
    public static String centToDollar(Long cent) {
        String resStr = String.valueOf(cent);
        if (resStr.length() < 3) {
            switch (resStr.length()) {
                case 2:
                    resStr = "0." + resStr;
                    break;
                case 1:
                    resStr = "0.0" + resStr;
                    break;
            }
        } else {
            StringBuffer strBuf = new StringBuffer();
            strBuf.append(resStr);
            strBuf.insert(resStr.length() - 2, ".");
            resStr = strBuf.toString();
        }

        return resStr;
    }

    /**
     * @param map
     * @return 实现对map按照key排序 注：支付宝用到了
     */
    public static Map<String, String> getSortedHashtableByKey(Map<String, String> map) {
        Map<String, String> treeMap1 = new TreeMap<String, String>();
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Entry pairs = (Entry) it.next();
            treeMap1.put(pairs.getKey().toString(), pairs.getValue().toString());
//			try {//进行url编码，防止转义问题
//				treeMap1.put(pairs.getKey().toString(), java.net.URLEncoder.encode(pairs.getValue().toString(), map.get("_input_charset")));
//			} catch (UnsupportedEncodingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
        }
        return treeMap1;
    }

    /**
     * 除去数组中的空值和签名参数
     * @param sArray 签名参数组
     * @return 去掉空值与签名参数后的新签名参数组 注：支付宝用到了
     */
    public static Map<String, String> paraFilter(Map<String, String> sArray) {

        Map<String, String> result = new HashMap<String, String>();

        if (sArray == null || sArray.size() <= 0) {
            return result;
        }

        for (String key : sArray.keySet()) {
            String value = sArray.get(key);
            if (value == null || value.equals("") || key.equalsIgnoreCase("sign") || key.equalsIgnoreCase("sign_type")) {
                continue;
            }
            result.put(key, value);
        }

        return result;
    }

    //注：支付宝用到了
    public static Map<String, String> paraMap(String urlPairs, String sign, String deCode) {
        if (urlPairs == null || "".equals(urlPairs)) {
            return null;
        }
        Map<String, String> map = new HashMap<String, String>();
        String[] tmpPairs = urlPairs.split(sign);
        String[] tmpStr = null;
        for(int i=0; i<tmpPairs.length; i++){
            //System.out.println(tmpPairs[i]);
            tmpStr = tmpPairs[i].split("=");
            if(deCode != null && !"".equals(deCode)){
                try {
                    map.put(tmpStr[0], URLDecoder.decode(tmpStr[1], deCode));
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    logger.info("====================paraMap============================="+ e.getMessage());
                    e.printStackTrace();
                }
            }else
                map.put(tmpStr[0], tmpStr[1]);
        }

        return map;
    }

    //返回某个节点字符串，没有命名空间的;//direct_trade_create_res/request_token 注：支付宝用到了
    public static String getSingleNodeStr(String xmlStr, String singleNode){
        Document document = null;
        String xmlText = null;
        try {
            document = DocumentHelper.parseText(xmlStr);
            if(document.selectSingleNode(singleNode)!= null)
                xmlText = document.selectSingleNode(singleNode).getText();
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            logger.info("====================getSingleNodeStr============================="+ e.getMessage());
            e.printStackTrace();
        }

        return xmlText;
    }

    /**
     * MAP类型数组转换成NameValuePair类型
     * @param properties  MAP类型数组
     * @return NameValuePair类型数组
     * @throws UnsupportedEncodingException
     */
    public static List<NameValuePair> generatNameValuePair(Map<String, String> properties) throws UnsupportedEncodingException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            //System.out.println("-----------------="+ entry.getKey());
            params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        return params;
    }

    /**
     * 将字符串转成map (str1=1&str2=2)
     * @param str 字符串
     * @param inputCharset 字符集
     * @return
     */
    public static Map<String, Object> stringConvertMap(String str, String inputCharset){
        String[] array = str.split("&");
        Map<String, Object> map = new HashMap<String, Object>();
        for (int i = 0; i < array.length; i++){
            String key = array[i].substring(0, array[i].indexOf("="));
            String value = "";
            if(StringUtils.isNotBlank(inputCharset)){
                try{
                    value = URLDecoder.decode(array[i].substring(array[i].lastIndexOf("=") + 1), Tool.getInputCharset());
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }else{
                value = array[i].substring(array[i].lastIndexOf("=") + 1);
            }
            map.put(key, value);
        }
        return map;
    }

    /**
     * map.tostring 后转换成map类型
     * @param str
     * @return
     */
    public static Map<String, String> mapStringToMap(String str){
        str = str.substring(1, str.length()-1);
        String[] strs = str.split(",");
        Map<String,String> map = new HashMap<String, String>();
        for (String string : strs) {
            String key = string.split("=")[0];
            String value = string.split("=")[1];
            map.put(key, value);}
        return map;
    }
}
