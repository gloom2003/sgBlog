package com.kana.utils;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BeanCopyUtil {
    private BeanCopyUtil() {
    }

    //方法上的泛型<V>,设置返回的类型为V类型，由传入Class参数的泛型决定V的类型
    //类似于List<String>,Class<String>表示传入的是String的字节码对象
    public static <V> V beanCopy(Object source, Class<V> clazz) {
        V v = null;
        try {
            v = clazz.newInstance();
            //使用org.springframework.beans.BeanUtils中的copyProperties方法
            BeanUtils.copyProperties(source, v);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }

    public static <V,O> List<V> beanListCopy(List<O> sourceLists, Class<V> clazz) {
        //1.0写法:
//        List<V> result = new ArrayList<>();
//        for (O o : sourceLists) {
//            result.add(beanCopy(o,clazz));
//        }
//        return result;
        //2.0写法 stream操作
        return sourceLists.stream()
                .map(o->beanCopy(o,clazz))
                .collect(Collectors.toList());

    }
}
