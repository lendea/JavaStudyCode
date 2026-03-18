package com.lendea.concurrent;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.NoIvGenerator;
import org.jasypt.iv.RandomIvGenerator;

/**
 * @author lendea
 * @project JavaStudyCode
 * @description
 * @date 2024/05/17 16:36
 **/
public class Main {

    public static void main(String[] args) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setAlgorithm("PBEWithMD5AndDES");
        encryptor.setPassword("Leaf#pswd7");
        encryptor.setIvGenerator(new NoIvGenerator());

        // 加密
//        mZPkYJJcanyQwUGf3ZtRZNCZ0poBSVLh leaf_portal

//        String encryptText = encryptor.encrypt("#pe4.7o3q4");
//        String encryptText = encryptor.encrypt("admin123");
        String encryptText = encryptor.encrypt("leaf_audit");
        System.out.println(encryptText);

        // 解密
//        String decryptText = encryptor.decrypt("SxjXCDfK8e2zURWjRf9LJ383BX+UdTOt");
//        System.out.println(decryptText);
    }
}
