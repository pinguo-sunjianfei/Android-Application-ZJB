package com.zjb.volley.bean;


import com.zjb.volley.R;
import com.zjb.volley.core.response.HttpResponse;
import com.zjb.volley.utils.ResUtil;

/**
 * time: 15/7/22
 * description: 封装网络错误码
 *
 * @author sunjianfei
 */
public enum ErrorCode {

    DEFAULT(1, ResUtil.getString(R.string.status_default)),
    SUCCESS(200, ResUtil.getString(R.string.status_errorcode200)),
    FAIL401(401, ResUtil.getString(R.string.status_errorcode401)),
    FAIL403(403, ResUtil.getString(R.string.status_errorcode401)),
    FAIL404(404, ResUtil.getString(R.string.status_errorcode404)),
    FAIL405(405, ResUtil.getString(R.string.status_errorcode405)),
    FAIL420(420, ResUtil.getString(R.string.status_errorcode420)),
    FAIL500(500, ResUtil.getString(R.string.status_errorcode500)),
    NOUSER(10000, ResUtil.getString(R.string.status_errorcode10000)),
    ERROR451(451, ResUtil.getString(R.string.status_errorcode451)),
    ERROR452(452, ResUtil.getString(R.string.status_errorcode452)),
    ERROR453(453, ResUtil.getString(R.string.status_errorcode453)),
    ERROR454(454, ResUtil.getString(R.string.status_errorcode454)),
    ERROR455(455, ResUtil.getString(R.string.status_errorcode455)),
    ERROR456(456, ResUtil.getString(R.string.status_errorcode456)),
    ERROR457(457, ResUtil.getString(R.string.status_errorcode457)),
    ERROR458(458, ResUtil.getString(R.string.status_errorcode458)),
    ERROR459(459, ResUtil.getString(R.string.status_errorcode459)),
    ERROR460(460, ResUtil.getString(R.string.status_errorcode460)),
    ERROR461(461, ResUtil.getString(R.string.status_errorcode461)),
    ERROR462(462, ResUtil.getString(R.string.status_errorcode462)),
    ERROR463(463, ResUtil.getString(R.string.status_errorcode463)),
    ERROR464(464, ResUtil.getString(R.string.pls_input_right_varifycode)),
    ERROR465(465, ResUtil.getString(R.string.status_errorcode465)),
    ERROR466(466, ResUtil.getString(R.string.pls_input_right_varifycode)),
    ERROR467(467, ResUtil.getString(R.string.status_errorcode467)),
    ERROR468(468, ResUtil.getString(R.string.status_errorcode468)),
    ERROR469(469, ResUtil.getString(R.string.status_errorcode469)),
    ERROR470(470, ResUtil.getString(R.string.status_errorcode470)),
    ERROR471(471, ResUtil.getString(R.string.status_errorcode471)),
    ERROR472(472, ResUtil.getString(R.string.status_errorcode472)),
    ERROR473(473, ResUtil.getString(R.string.status_errorcode473)),
    ERROR474(474, ResUtil.getString(R.string.status_errorcode474)),
    ERROR475(475, ResUtil.getString(R.string.status_errorcode475)),
    ERROR476(476, ResUtil.getString(R.string.status_errorcode476)),
    ERROR477(477, ResUtil.getString(R.string.status_errorcode477)),
    ERROR500(500, ResUtil.getString(R.string.status_errorcode500)),
    ERROR10001(10001, ResUtil.getString(R.string.status_errorcode10001)),
    ERROR10002(10002, ResUtil.getString(R.string.status_errorcode10002)),
    ERROR10003(10003, ResUtil.getString(R.string.status_errorcode10003)),
    ERROR10004(10004, ResUtil.getString(R.string.status_errorcode10004)),
    ERROR10005(10005, ResUtil.getString(R.string.status_errorcode10005)),
    ERROR10006(10006, ResUtil.getString(R.string.status_errorcode10006)),
    ERROR10007(10007, ResUtil.getString(R.string.status_errorcode10007)),
    ERROR10008(10008, ResUtil.getString(R.string.status_errorcode10008)),
    ERROR10009(10009, ResUtil.getString(R.string.status_errorcode10009)),
    ERROR10010(10010, ResUtil.getString(R.string.status_errorcode10010)),
    ERROR10011(10011, ResUtil.getString(R.string.status_errorcode10011)),
    ERROR10012(10012, ResUtil.getString(R.string.status_errorcode10012)),
    ERROR10013(10013, ResUtil.getString(R.string.status_errorcode10013)),
    ERROR10510(10510, ResUtil.getString(R.string.status_errorcode10510)),
    ERROR10511(10511, ResUtil.getString(R.string.status_errorcode10511)),
    ERROR10512(10512, ResUtil.getString(R.string.status_errorcode10512)),
    ERROR10513(10513, ResUtil.getString(R.string.status_errorcode10513)),
    ERROR10518(10518, ResUtil.getString(R.string.status_errorcode10518)),
    ERROR10519(10519, ResUtil.getString(R.string.status_errorcode10519)),
    ERROR10520(10520, ResUtil.getString(R.string.status_errorcode10520)),
    ERROR10521(10521, ResUtil.getString(R.string.status_errorcode10521)),
    ERROR10522(10522, ResUtil.getString(R.string.status_errorcode10522)),
    ERROR10524(10524, ResUtil.getString(R.string.status_errorcode10524)),
    ERROR10525(10525, ResUtil.getString(R.string.status_errorcode10525)),
    ERROR10535(10535, ResUtil.getString(R.string.status_errorcode10535)),
    ERROR10536(10536, ResUtil.getString(R.string.status_errorcode10536)),
    ERROR10537(10537, ResUtil.getString(R.string.status_errorcode10537)),
    ERROR10538(10538, ResUtil.getString(R.string.status_errorcode10538)),
    ERROR10539(10539, ResUtil.getString(R.string.status_errorcode10539)),
    ERROR10540(10540, ResUtil.getString(R.string.status_errorcode10540)),
    ERROR10541(10541, ResUtil.getString(R.string.status_errorcode10541)),
    ERROR10542(10542, ResUtil.getString(R.string.status_errorcode10542)),
    ERROR10543(10543, ResUtil.getString(R.string.status_errorcode10543)),
    ERROR10622(10622, ResUtil.getString(R.string.status_errorcode10622)),
    ERROR10623(10623, ResUtil.getString(R.string.status_errorcode10623)),
    ERROR10624(10624, ResUtil.getString(R.string.status_errorcode10624)),
    ERROR10625(10625, ResUtil.getString(R.string.status_errorcode10625)),
    ERROR10658(10658, ResUtil.getString(R.string.status_errorcode10658)),
    ERROR10659(10659, ResUtil.getString(R.string.status_errorcode10659)),
    ERROR100560(100560, ResUtil.getString(R.string.status_errorcode100560));

    private final int code;

    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 根据网络返回内容处理特殊信息
     *
     * @param response
     * @return
     */
    public static ErrorCode handleCode(HttpResponse response) {
        for (ErrorCode errorCode : ErrorCode.values()) {
            if (errorCode.getCode() == response.status) {
                return errorCode;
            }
        }

        return DEFAULT;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public String toString() {
        return "ErrorCode{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }

}
