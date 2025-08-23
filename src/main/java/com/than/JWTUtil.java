package com.than;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Than
 * @package: com.than.jwt
 * @className: JWTUtil
 * @description: JWT工具
 * @date: 2023/8/25 16:42
 */
public class JWTUtil {

    private static final String SIGNATURE = "nosmCs%KrJ3*8W7y#*Vs6G9IqCT!$YcWuVDwuUdXX9kaDn7WJW";
    private static final long MONTH = 2592000000L;

    /**
     * 生成token
     *
     * @param map 传入payload
     * @return 返回token
     */
    public static String getToken(Map<String, String> map) {
        JWTCreator.Builder builder = JWT.create();
        map.forEach(builder::withClaim);
        builder.withExpiresAt(TimeUtil.getOffsetData(30));
        return builder.sign(Algorithm.HMAC256(SIGNATURE));
    }

    /**
     * 验证token
     *
     * @param token
     */
    public static void verify(String token) {
        JWT.require(Algorithm.HMAC256(SIGNATURE)).build().verify(token);
    }

    /**
     * 获取token中payload
     *
     * @param token
     * @return
     */
    public static DecodedJWT getTokenPayload(String token) {
        return JWT.require(Algorithm.HMAC256(SIGNATURE)).build().verify(token);
    }

    /**
     * <p>刷新token</p>
     *
     * @param token token
     * @return 满足条件时返回新token, 否则返回原token
     */
    public static String refreshTheToken(String token) {
        verify(token);
        Map<String, Claim> claims = getTokenPayload(token).getClaims();
        HashMap<String, String> map = removeExp(claims);
        if (isAbleToRefresh(claims.get("exp").toString())) {
            return getToken(map);
        }
        return token;
    }
    /**
     * <p>判断token是否应该刷新</p>
     * @param exp token原来的时间戳
     * @return 是否刷新
     */
    private static boolean isAbleToRefresh(String exp) {
        long time = Long.parseLong(exp);
        return (time - TimeUtil.getTime()) <= (MONTH / 2);
    }
    /**
     * <p>去除时间戳,因为直接得到的map的remove功能是抽象的,且不能强转为非抽象的其他map</p>
     *
     * @param map 参数
     * @return 去除时间戳后的参数
     */
    private static HashMap<String, String> removeExp(Map<String, Claim> map) {
        HashMap<String, String> claims = new HashMap<>();
        map.forEach((s, claim) -> {
            if (!s.equals("exp")) {
                claims.put(s, claim.asString());
            }
        });
        return claims;
    }
}
