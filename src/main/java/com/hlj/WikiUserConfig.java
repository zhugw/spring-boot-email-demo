package com.hlj;

import java.util.HashMap;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
/**
 * 参考： http://stackoverflow.com/questions/24917194/spring-boot-inject-map-from-application-yml
 * @author zhuguowei
 *
 */
@Component
@ConfigurationProperties(prefix="server")
public class WikiUserConfig {
	private HashMap<String, String> wikiuser;

	public HashMap<String, String> getWikiuser() {
		return wikiuser;
	}

	public void setWikiuser(HashMap<String, String> wikiuser) {
		this.wikiuser = wikiuser;
	}
	
	
}
