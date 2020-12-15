package com.qst.dms.entity;

import java.math.BigInteger;
import java.security.MessageDigest;


public class MD5 {

    public static String getMD5(String inputStr)
    {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(inputStr.getBytes("UTF-8"));
            byte[] encryption = md5.digest();

            StringBuffer strBuf = new StringBuffer();
            for (int i = 0; i < encryption.length; i++) {
                strBuf.append(Integer.toHexString(0xff & encryption[i]));
            }
            return strBuf.toString();
        } catch (Exception e) {
            return "";
        }
    }

}