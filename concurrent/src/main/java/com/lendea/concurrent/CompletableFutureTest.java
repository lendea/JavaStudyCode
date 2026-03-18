package com.lendea.concurrent;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author lendea
 * @project JavaStudyCode
 * @description
 * @date 2024/02/01 15:14
 **/
public class CompletableFutureTest {

    public static void main(String[] args) throws DecoderException {

        CompletableFuture<String> stringCompletableFuture = CompletableFuture.supplyAsync(CompletableFutureTest::task);
        CompletableFuture<String> stringCompletableFuture2 = CompletableFuture.supplyAsync(CompletableFutureTest::task);
        CompletableFuture<String> stringCompletableFuture3 = CompletableFuture.supplyAsync(CompletableFutureTest::task);

//        stringCompletableFuture.thenAccept()
        System.out.println(UUID.randomUUID());

        String str1 = "hello";
        String base64Encoded1 = Base64.encodeBase64String(str1.getBytes());
        System.out.println(base64Encoded1);
        byte[] bytes1 = Base64.decodeBase64(base64Encoded1);
        System.out.println(new String(bytes1));

        String str = "hello";
        char[] base64Encoded = Hex.encodeHex(str.getBytes());
        byte[] bytes = Hex.decodeHex(base64Encoded);
        String str2 = new String(bytes);
        System.out.println(base64Encoded);
        System.out.println(str2);

    }

    static String task() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return "ok";
    }
}
