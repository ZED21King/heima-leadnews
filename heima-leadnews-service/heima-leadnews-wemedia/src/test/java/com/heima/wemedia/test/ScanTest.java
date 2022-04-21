package com.heima.wemedia.test;

import com.heima.common.aliyun.AliyunGreenContentScan;
import com.heima.common.aliyun.GreenImageScan;
import com.heima.common.minio.MinioTemplate;
import com.heima.wemedia.WemediaApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WemediaApplication.class)
public class ScanTest {

    @Autowired
    private AliyunGreenContentScan greenTextScan;
    @Autowired
    private GreenImageScan greenImageScan;
    @Autowired
    private MinioTemplate minioTemplate;

    /**
     * 检测文本
     */
    @Test
    public void testScanText() throws Exception {
        List<String> list = new ArrayList<>();
        list.add("我是程序员");
        list.add("我是傻瓜");
        list.add("冰毒");
        Map<String, String> result = greenTextScan.greeTextScan(list);
        System.out.println(result);
    }

    /**
     * 检测图片
     */
    @Test
    public void testScanImage() {
        //1.从minio下载图片
        String url = "http://106.12.113.105:9000/leadnews/wemedia/2022/03/18/d5427224c38545cc9fb6f8e6dde43c43.jpg";
        byte[] image = minioTemplate.downLoadFile(url);

        //2.给阿里云检测
//        List<byte[]> images = new ArrayList<>();
//        images.add(image);
//        Map<String, String> result = greenImageScan.imageScan(images);
//        System.out.println(result);

        ArrayList<String> urlList = new ArrayList<>();
        urlList.add(url);
        Map<String, String> result = greenImageScan.imageScan(urlList);
        System.out.println(result);
    }

    @Test
    public void test1() {
        BitSet bitSet = new BitSet();

        HashMap<String, BitSet> map = new HashMap<>();
        BitSet bitSet135 = map.computeIfAbsent("135", s -> bitSet);
        bitSet135.set(66789098);

        System.out.println(bitSet135.get(66789098));
    }
}