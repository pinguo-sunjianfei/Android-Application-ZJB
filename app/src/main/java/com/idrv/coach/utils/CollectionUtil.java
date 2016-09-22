package com.idrv.coach.utils;

import java.util.List;

/**
 * time: 15/7/18
 * description: Collection工具类
 *
 * @author sunjianfei
 */
public class CollectionUtil {

    public static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    public static String join(String separator, long... array) {
        checkNotNull(separator);
        if (array.length == 0) {
            return "";
        }

        // For pre-sizing a builder, just get the right order of magnitude
        StringBuilder builder = new StringBuilder(array.length * 10);
        builder.append(array[0]);
        for (int i = 1; i < array.length; i++) {
            builder.append(separator).append(array[i]);
        }
        return builder.toString();
    }

    public static String join(String separator, int... array) {
        checkNotNull(separator);
        if (array.length == 0) {
            return "";
        }

        // For pre-sizing a builder, just get the right order of magnitude
        StringBuilder builder = new StringBuilder(array.length * 10);
        builder.append(array[0]);
        for (int i = 1; i < array.length; i++) {
            builder.append(separator).append(array[i]);
        }
        return builder.toString();
    }

    public static String join(String separator, double... array) {
        checkNotNull(separator);
        if (array.length == 0) {
            return "";
        }

        // For pre-sizing a builder, just get the right order of magnitude
        StringBuilder builder = new StringBuilder(array.length * 10);
        builder.append(array[0]);
        for (int i = 1; i < array.length; i++) {
            builder.append(separator).append(array[i]);
        }
        return builder.toString();
    }

    public static String join(String separator, float... array) {
        checkNotNull(separator);
        if (array.length == 0) {
            return "";
        }

        // For pre-sizing a builder, just get the right order of magnitude
        StringBuilder builder = new StringBuilder(array.length * 10);
        builder.append(array[0]);
        for (int i = 1; i < array.length; i++) {
            builder.append(separator).append(array[i]);
        }
        return builder.toString();
    }

    public static String join(String separator, String... array) {
        checkNotNull(separator);
        if (array == null || array.length == 0) {
            return "";
        }

        // For pre-sizing a builder, just get the right order of magnitude
        StringBuilder builder = new StringBuilder(array.length * 10);
        builder.append(array[0]);
        for (int i = 1; i < array.length; i++) {
            builder.append(separator).append(array[i]);
        }
        return builder.toString();
    }

    public static String join(String separator, char... array) {
        checkNotNull(separator);
        if (array.length == 0) {
            return "";
        }

        // For pre-sizing a builder, just get the right order of magnitude
        StringBuilder builder = new StringBuilder(array.length * 10);
        builder.append(array[0]);
        for (int i = 1; i < array.length; i++) {
            builder.append(separator).append(array[i]);
        }
        return builder.toString();
    }

    public static String join(String separator, List<?> list) {
        if (!ValidateUtil.isValidate(list)) {
            return null;
        }
        checkNotNull(separator);
        // For pre-sizing a builder, just get the right order of magnitude
        StringBuilder builder = new StringBuilder();
        builder.append(list.get(0));
        for (int i = 1; i < list.size(); i++) {
            builder.append(separator).append(list.get(i).toString().trim());
        }
        return builder.toString();

    }

    public static <T> List<T> filter(List<T> origin, List<T> filter) {
        if (ValidateUtil.isValidate(origin)
                && ValidateUtil.isValidate(filter)) {
            for (T t : filter) {
                origin.remove(t);
            }
        }
        return origin;
    }

    /**
     * 将一个List转换成一个String
     *
     * @param separator 元素之间的间隔符
     * @param shortfix  每个元素外面包一层的符号，比如1234,加了shortfix为 '1234'
     * @param list
     * @return
     */
    public static String join(String separator, String shortfix, List<?> list) {
        if (!ValidateUtil.isValidate(list)) {
            return null;
        }
        checkNotNull(separator);
        // For pre-sizing a builder, just get the right order of magnitude
        StringBuilder builder = new StringBuilder();
        builder.append(list.get(0));
        for (int i = 1; i < list.size(); i++) {
            builder.append(separator)
                    .append(shortfix)
                    .append(list.get(i).toString().trim())
                    .append(shortfix);

        }
        return builder.toString();

    }

    public static String join(List<String> list) {
        if (!ValidateUtil.isValidate(list)) {
            return null;
        }
        // For pre-sizing a builder, just get the right order of magnitude
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            builder.append(list.get(i));
        }
        return builder.toString();

    }
}
