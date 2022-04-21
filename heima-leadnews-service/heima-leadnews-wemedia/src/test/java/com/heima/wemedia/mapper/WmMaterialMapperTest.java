package com.heima.wemedia.mapper;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zed21@aliyun.com
 * @date 2022/3/18 10:42
 */
public class WmMaterialMapperTest {
    private static WmMaterialMapper mapper;

    @BeforeClass
    public static void setUpMybatisDatabase() {
        SqlSessionFactory builder = new SqlSessionFactoryBuilder().build(WmMaterialMapperTest.class.getClassLoader().getResourceAsStream("mybatisTestConfiguration/WmMaterialMapperTestConfiguration.xml"));
        //you can use builder.openSession(false) to not commit to database
        mapper = builder.getConfiguration().getMapper(WmMaterialMapper.class, builder.openSession(true));
    }

    @Test
    public void testDeletePicture() {
//        mapper.deletePicture();

        //因为content是一个Json
        List<Map> mapList = new ArrayList<Map>();
        HashMap<String, String> hashMap = new HashMap<>();
        HashMap<String, String> hashMap1 = new HashMap<>();
        hashMap.put("type", "text");
        hashMap.put("value", "随着智能手机的普及");
        hashMap1.put("type", "image");
        hashMap1.put("value", "http://192");
        mapList.add(hashMap);
        mapList.add(hashMap1);

        List<Object> collect =  mapList.stream().map(map -> {
            if ("image".equals(map.get("type"))) {
                return map.get("value");
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());


        List<String> convert = Convert.convert(new TypeReference<List<String>>() {
        }, collect);
        System.out.println(convert);

    }
}
