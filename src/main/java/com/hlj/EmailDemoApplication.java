package com.hlj;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * @author zhuguowei
 *
 */
@SpringBootApplication
public class EmailDemoApplication implements CommandLineRunner {

	private static Logger logger = LoggerFactory.getLogger(EmailDemoApplication.class);
	private static final String DEFAULT_ENCODING = "utf-8";
	@Autowired
	private JavaMailSender javaMailService;
	@Autowired
	private Configuration freemarkerConfiguration;
	@Autowired
	private WikiUserConfig wikiUserConfig;
	@Value("${email.count.to}")
	private String countMsgTo; // 统计订餐人数邮件接收人
	@Value("${email.from}")
	private String sender;
	@Value("${count.url}")
	private String countUrl;

	public static void main(String[] args) {
		SpringApplication.run(EmailDemoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		logger.info(Arrays.toString(args));
		logger.info(wikiUserConfig.getWikiuser().toString());
		if(args.length==0){
			logger.warn("please specify which operation you want? plus1 or count?");
			return;
		}
		if ("plus1".equals(args[0])) // 发送加一链接
			sendPlus1HtmlMessage(wikiUserConfig);
		else if ("count".equals(args[0])) { // 发送订餐人数统计链接
			RestTemplate template = new RestTemplate();
			ResponseEntity<List> response = template.getForEntity(countUrl, List.class);
			response.getBody().stream().forEach(e -> logger.info(e.toString()));
			sendCountHtmlMessage(response.getBody());
		}
	}
	public interface SendMessageAction{
		void action(MimeMessage msg, MimeMessageHelper helper) throws MessagingException;
	}
	private void sendMessage(SendMessageAction action) {
		try {
			final MimeMessage msg = javaMailService.createMimeMessage();
			final MimeMessageHelper helper = new MimeMessageHelper(msg, true, DEFAULT_ENCODING);
			helper.setFrom(sender);
			action.action(msg, helper);
		} catch (MessagingException e) {
			logger.error("构造邮件失败", e);
		} catch (Exception e) {
			logger.error("发送邮件失败", e);
		}
	}
	private void sendCountHtmlMessage(List<Reservation> body) {
		sendMessage(new SendMessageAction(){
			@Override
			public void action(MimeMessage msg, MimeMessageHelper helper) throws MessagingException {
				logger.info("send count mail to {}", countMsgTo);
				helper.setTo(countMsgTo);
				helper.setSubject("今日订餐人数统计");
				String content = generateCountContent(body);
				helper.setText(content, true);
				javaMailService.send(msg);				
			}
		});
		
	}

	private void sendPlus1HtmlMessage(WikiUserConfig config) {
		sendMessage(new SendMessageAction(){
			@Override
			public void action(MimeMessage msg, MimeMessageHelper helper) throws MessagingException {
				for (String name : config.getWikiuser().keySet()) {
					logger.info("send plus1 mail to {}", name);
					helper.setTo(config.getWikiuser().get(name));
					helper.setSubject("订餐加一");
					String content = generatePlus1Content(name);
					helper.setText(content, true);
					javaMailService.send(msg);
				}		
			}
		});
	}

	private String generatePlus1Content(String name) throws MessagingException {
		Map<String, String> context = Collections.singletonMap("name", name);
		String templateName = "plus1MailTemplate.ftl";
		return generateContent(context, templateName);
	}

	private String generateCountContent(List<Reservation> list) throws MessagingException {
		Map<String, List> context = Collections.singletonMap("users", list);
		String templateName = "countMailTemplate.ftl";
		return generateContent(context, templateName);
	}

	/**
	 * 使用Freemarker生成html格式内容.
	 */
	private String generateContent(Map context, String templateName) throws MessagingException {

		try {
			Template template = freemarkerConfiguration.getTemplate(templateName, DEFAULT_ENCODING);
			return FreeMarkerTemplateUtils.processTemplateIntoString(template, context);
		} catch (IOException e) {
			logger.error("生成邮件内容失败, FreeMarker模板不存在", e);
			throw new MessagingException("FreeMarker模板不存在", e);
		} catch (TemplateException e) {
			logger.error("生成邮件内容失败, FreeMarker处理失败", e);
			throw new MessagingException("FreeMarker处理失败", e);
		}
	}

}
