package com.huiling;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author fei
 */
@Data
@ConfigurationProperties("corp-wx")
public class CorpWxProperties {

    @Value("${corpID}")
    private String corpID;

    @Value("${token}")
    private String token;

    @Value("${encodingAESKey}")
    private String encodingAESKey;

    @Value("${agentId}")
    private String agentId;

    @Value("${secret}")
    private String secret;


}
