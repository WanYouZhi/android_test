package com.huawei.broser.http;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.$Gson$Types;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 *
 * @author wWX531311
 * @date 2017/12/29
 */

public class JsonUtil {
    private static final Gson gson = new Gson();

    public static <T> T fromGenericJson(String json, Class<T> obj) {
        return (T) gson.fromJson(json, getSuperclassTypeParameter(obj));
    }

    public static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new JsonSyntaxException("Missing type parameter.");
        }
        ParameterizedType param = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(param.getActualTypeArguments()[0]);
    }


    /**
     * List 的java转换
     * @param str
     * @param obj
     * @return
     */
    public static <T> List<T> listConvert(String str, Class<T> obj) {
        Type type = $Gson$Types.newParameterizedTypeWithOwner(null, List.class, obj);
        return gson.fromJson(str, type);
    }

    public static <T>  String toJson(T obj) {
        return gson.toJson(obj);
    }
}