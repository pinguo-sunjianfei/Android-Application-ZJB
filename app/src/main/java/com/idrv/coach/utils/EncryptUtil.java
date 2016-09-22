package com.idrv.coach.utils;

import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptUtil {
    static {
        System.loadLibrary("idrv");
    }

    public static native String md5(String input);

    public static native String base64encode(String input);

    public static native String base64decode(String input);

    public static native String pwdEncode(String input);

    public static String getQETAG(String path) {
        byte[] SHA1Byte;
        try {
            SHA1Byte = getFileSHA1(path);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        if (null == SHA1Byte) {
            throw new IllegalArgumentException("SHA1 must not be empty!");
        }

        if (SHA1Byte.length != 20) {
            throw new IllegalArgumentException(
                    "SHA1 length must be 20! Current length:" + SHA1Byte.length);
        }

        byte[] QETAGByte = new byte[21];
        QETAGByte[0] = 0x16;

        System.arraycopy(SHA1Byte, 0, QETAGByte, 1, 20);

        return Base64.encodeToString(QETAGByte, Base64.URL_SAFE | Base64.NO_WRAP);
    }

    public static byte[] getFileSHA1(String path) throws IOException {

        File file = new File(path);
        FileInputStream in = new FileInputStream(file);
        MessageDigest messagedigest;
        try {
            messagedigest = MessageDigest.getInstance("SHA-1");

            byte[] buffer = new byte[1024 * 64];
            int len;

            while ((len = in.read(buffer)) > 0) {
                messagedigest.update(buffer, 0, len);
            }

            return messagedigest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            throw e;
        } finally {
            in.close();
        }
        return null;
    }
}
