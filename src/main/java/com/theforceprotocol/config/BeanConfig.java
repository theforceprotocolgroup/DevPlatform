package com.theforceprotocol.config;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class BeanConfig {

    @Bean
    public JsonRpcHttpClient client() throws Throwable {
        // 身份认证
        String cred = Base64.encodeBase64String(("username" + ":" + "password").getBytes());
        Map<String, String>  headers = new HashMap <>(1);
        headers.put("Authorization", "Basic " + cred);
        return new JsonRpcHttpClient(new URL("http://" + "ip" + ":" + "8332"), headers);//BTC
    }
}
