package com.than;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class UrlSafeBase64Encoder {
    /**
     * 对字符串进行URL安全的Base64编码，并去除填充字符
     * @param input 要编码的字符串
     * @return 编码后的字符串（无填充）
     */
    public static String encode(String input) {
        byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);

        // 使用URL安全的Base64编码器，不添加填充
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();

        // 编码并转换为字符串
        return encoder.encodeToString(inputBytes);
    }

    /**
     * 对URL安全的Base64编码字符串进行解码
     * @param encoded 编码后的字符串
     * @return 解码后的原始字符串
     */
    public static String decode(String encoded) {
        Base64.Decoder decoder = Base64.getUrlDecoder();

        byte[] decodedBytes = decoder.decode(encoded);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }
}
