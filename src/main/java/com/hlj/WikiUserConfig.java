package com.hlj;

import java.util.HashMap;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
/**
 * 参考： http://stackoverflow.com/questions/24917194/spring-boot-inject-map-from-application-yml
 * @author zhuguowei
 *
 */
@Component
@ConfigurationProperties(prefix="server")
@Data
public class WikiUserConfig {
	private HashMap<String, String> wikiuser;
}
