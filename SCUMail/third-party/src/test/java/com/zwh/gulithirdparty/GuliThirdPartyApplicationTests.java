package com.zwh.gulithirdparty;

import com.zwh.gulithirdparty.component.SmsComponent;
import com.zwh.gulithirdparty.util.HttpUtils;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class GuliThirdPartyApplicationTests {

	@Autowired
	private SmsComponent smsComponent;

	@Test
	void contextLoads() {
		String host = "https://dfsns.market.alicloudapi.com";
		String path = "/data/send_sms";
		String method = "POST";
		String appcode = "dcd7b12a8ce64480b72e1d8f79e55110";
		Map<String, String> headers = new HashMap<String, String>();
		//最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
		headers.put("Authorization", "APPCODE " + appcode);
		//根据API的要求，定义相对应的Content-Type
		headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		Map<String, String> querys = new HashMap<String, String>();
		Map<String, String> bodys = new HashMap<String, String>();
		bodys.put("content", "code:喵喵喵");
		bodys.put("phone_number", "18238482264");
		bodys.put("template_id", "TPL_0000");

		try {
			/**
			 * 重要提示如下:
			 * HttpUtils请从
			 * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
			 * 下载
			 *
			 * 相应的依赖请参照
			 * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
			 */
			HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
			System.out.println(response.toString());
			//获取response的body
			//System.out.println(EntityUtils.toString(response.getEntity()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSendCode(){
		smsComponent.sendSmsCode("18238482264","123456");
	}

}
