package com.than.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.lang.NonNull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.xml.transform.Result;
import java.util.HashMap;
import java.util.Map;


@Component
public class VerifyInterceptor implements HandlerInterceptor {

    @Value("${verify}")
    private String verify;

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        Map<String, Object> map = new HashMap<>();
        //令牌建议是放在请求头中，获取请求头中令牌
        String verify = request.getHeader("verify");
        if (verify == null||verify.isEmpty()||!verify.equals(this.verify)){
            map.put("state", false);//设置状态
            //将map转化成json，response使用的是Jackson
            String json = new ObjectMapper().writeValueAsString(map);
            response.setStatus(502);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print(json);
            return false;
        }else  {
            map.put("state", true);
            return true;
        }
    }
}
