package com.ecochain.ecocommonsms.service;
import lombok.Data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 *  敏感词过滤 工具类
 * @author hubiao
 * @version 0.1
 * @CreateDate 2015年4月16日 15:28:32
 */

@Data
public class SensitiveWordFilterService {
    private StringBuilder replaceAll;//初始化
    private String encoding = "UTF-8";
    private String replceStr = "*";
    private int replceSize = 500;
    private String fileName = "CensorWords.txt";
    private List<String> arrayList;

    private volatile static SensitiveWordFilterService sw = null;
    /**
     * 文件要求路径在src或resource下，默认文件名为CensorWords.txt
     * @param fileName 词库文件名(含后缀)
     */
    public SensitiveWordFilterService(String fileName)
    {
        this.fileName = fileName;
    }

    /**
     * @param replceStr 敏感词被转换的字符
     * @param replceSize 初始转义容量
     */
    public SensitiveWordFilterService(String replceStr, int replceSize)
    {
        this.replceStr = fileName;
        this.replceSize = replceSize;
    }

    public SensitiveWordFilterService()
    {
        initializationWork();
    }

    public static SensitiveWordFilterService getInstance()
    {
        synchronized (SensitiveWordFilterService.class){
            if(sw == null){
                sw =  new SensitiveWordFilterService("CensorWords.txt");
                sw.initializationWork();
            }
        }
        return sw;
    }
    /**
     * @param str 将要被过滤信息
     * @return 过滤后的信息
     */
    public String filterInfo(String str)
    {
        StringBuilder buffer = new StringBuilder(str);
        HashMap<Integer, Integer> hash = new HashMap<Integer, Integer>(arrayList.size());
        String temp;
        for(int x = 0; x < arrayList.size();x++)
        {
            temp = arrayList.get(x);
            int findIndexSize = 0;
            for(int start = -1;(start=buffer.indexOf(temp,findIndexSize)) > -1;)
            {
                findIndexSize = start+temp.length();//从已找到的后面开始找
                Integer mapStart = hash.get(start);//起始位置
                if(mapStart == null || (mapStart != null && findIndexSize > mapStart))//满足1个，即可更新map
                {
                    hash.put(start, findIndexSize);
                }
            }
        }
        Collection<Integer> values = hash.keySet();
        for(Integer startIndex : values)
        {
            Integer endIndex = hash.get(startIndex);
            buffer.replace(startIndex, endIndex, replaceAll.substring(0,endIndex-startIndex));
        }
        hash.clear();
        return buffer.toString();
    }
    /**
     *   初始化敏感词库
     */
    public void initializationWork()
    {
        replaceAll = new StringBuilder(replceSize);
        for(int x=0;x < replceSize;x++)
        {
            replaceAll.append(replceStr);
        }
        //加载词库
        arrayList = new ArrayList<String>();
        InputStreamReader read = null;
        BufferedReader bufferedReader = null;
        try {
            read = new InputStreamReader(SensitiveWordFilterService.class.getClassLoader().getResourceAsStream(fileName),encoding);
            bufferedReader = new BufferedReader(read);
            for(String txt = null;(txt = bufferedReader.readLine()) != null;){
                if(!arrayList.contains(txt))
                    arrayList.add(txt);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                if(null != bufferedReader)
                    bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if(null != read)
                    read.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
//    public StringBuilder getReplaceAll() {
//        return replaceAll;
//    }
//    public void setReplaceAll(StringBuilder replaceAll) {
//        this.replaceAll = replaceAll;
//    }
//    public String getReplceStr() {
//        return replceStr;
//    }
//    public void setReplceStr(String replceStr) {
//        this.replceStr = replceStr;
//    }
//    public int getReplceSize() {
//        return replceSize;
//    }
//    public void setReplceSize(int replceSize) {
//        this.replceSize = replceSize;
//    }
//    public String getFileName() {
//        return fileName;
//    }
//    public void setFileName(String fileName) {
//        this.fileName = fileName;
//    }
//    public List<String> getArrayList() {
//        return arrayList;
//    }
//    public void setArrayList(List<String> arrayList) {
//        this.arrayList = arrayList;
//    }
//    public String getEncoding() {
//        return encoding;
//    }
//    public void setEncoding(String encoding) {
//        this.encoding = encoding;
//    }

    public static void main(String[] args) {
        SensitiveWordFilterService sw = new SensitiveWordFilterService("CensorWords.txt");
        sw.initializationWork();
        long startNumer = System.currentTimeMillis();
        String str = "太多的伤yuming感情怀也许只局限于饲养基地 荧幕中的情节，主人公尝试着去用某种方式渐渐的很潇洒地释自杀指南怀那些自己经历的伤感。"
                + "然后法轮功 我们的扮演的角色就是跟随着主人yum公的喜红客联盟 怒于饲养基地 荧幕中的情节，主人公尝试着去用某种方式渐渐的很潇洒地释自杀指南怀那些自己经历的伤感。"
                + "然后法轮功 我们的扮演的角色就是跟随着主人yum公的喜红客联盟 怒哀20于饲养基地 荧幕中的情节，主人公尝试着去用某种方式渐渐的很潇洒地释自杀指南怀那些自己经历的伤感。"
                + "然后法轮功 我们的扮演的角色就是跟随着主人yum公的喜红客联盟 怒哀20哀2015/4/16 20152015/4/16乐而过于牵强的把自己的情感也附加于银幕情节中，然后感动就流泪，"
                + "关, 人, 流, 电, 发, 情, 太, 限, 法轮功, 个人, 经, 色, 许, 公, 动, 地, 方, 基, 在, 上, 红, 强, 自杀指南, 制, 卡, 三级片, 一, 夜, 多, 手机, 于, 自，"
                + "难过就躺在某一个人的怀里尽情的阐述心扉或者手机卡复制器一个人一杯红酒一部电影在夜三级片 深人静的晚上，关上电话静静的发呆着。";
        System.out.println("被检测字符:"+str.length());
        str = sw.filterInfo(str);
        long endNumber = System.currentTimeMillis();
        System.out.println("耗时(毫秒):"+(endNumber-startNumer));
        System.out.println("过滤之后:"+str);
    }
}
