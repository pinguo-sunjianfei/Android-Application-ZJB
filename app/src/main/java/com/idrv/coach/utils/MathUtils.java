package com.idrv.coach.utils;

import java.math.BigDecimal;

/**
 * Created by xiechaojun on 15-10-19.
 * description:数学计算的工具类
 *
 * @author crab
 */
public class MathUtils {
    /**
     * 把一个double类型转化为对应的string类型
     */
    public static String doubleParseToString(double d1) {
        Double d = new Double(d1);
        BigDecimal bd = new BigDecimal(d.toString());
        return bd.toPlainString();
    }

    /**
     * 保留两位小数
     */
    public static String decimalFormat(float value) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
        return df.format(value);
    }
}
