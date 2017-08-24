package com.baosight.bsfc4.mb.pj.util;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.springframework.web.util.HtmlUtils;

import com.baosight.iplat4j.core.ei.EiConstant;
import com.baosight.iplat4j.core.ei.EiInfo;
import com.baosight.iplat4j.logging.Logger;
import com.baosight.iplat4j.logging.LoggerFactory;
@SuppressWarnings("unchecked")
public class TestEiServicr {
	private static Logger logger = LoggerFactory.getLogger(TestEiServicr.class);
	
	public static void main(String[] args) {
		TestEiServicr.getInstance().sendPostIos(new HashMap());
	}
	protected static TestEiServicr postIos = new TestEiServicr();

	public static TestEiServicr getInstance() {
		return postIos;
	}

	public boolean sendPostIos(Map map) {
		EiInfo info = new EiInfo();
		info.set(EiConstant.serviceName, "MM00");
		info.set(EiConstant.methodName, "send");
		// / 设置参数
		HttpClient clt = new HttpClient();
		String SERVICE_TOKEN = "service";
		String METHOD_TOKEN = "method";
		String RESPONSE_ENCODING = "UTF-8";

//		int SO_TIMEOUT = 60000;
		int CONNECTION_TIMEOUT = 5000;

		clt.setConnectionTimeout(CONNECTION_TIMEOUT);
		clt.setHttpConnectionManager(new MultiThreadedHttpConnectionManager());
		/** 设置连接超时 * */
		//clt.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECTION_TIMEOUT);
		/** 设置连接后业务执行时间超时 * */
//		clt.getHttpConnectionManager().getParams().setSoTimeout(SO_TIMEOUT);
//		PostMethod method = new UTF8PostMethod("http://cgtest.baofinance.com/bfct/EiService");
		PostMethod method = new UTF8PostMethod("http://10.46.5.117:80/bfdm/EiService");
//		PostMethod method = new UTF8PostMethod("http://10.46.160.12:9080/bsfc/EiService");
//		String u = "http://10.46.20.2:9081/bsfc/EiService";
//		PostMethod method = new UTF8PostMethod(u);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
		BufferedImage bufferedImage;
		try {
			bufferedImage = ImageIO.read(new File("E:/test/test/t0110bc42140a866a5a.jpg"));
			ImageIO.write(bufferedImage, "jpg", baos);    
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        byte[] bytes = baos.toByteArray();    

		String fileS = new sun.misc.BASE64Encoder().encodeBuffer(bytes).trim();;
		NameValuePair[] params = {
				new NameValuePair(SERVICE_TOKEN, "MBPJ010"),
				new NameValuePair(METHOD_TOKEN, "uploadImage"),
//				piid:PROCESSINSTANCEID	wiid:WORKITEM
//				new NameValuePair("eiinfo", "{\"msgKey\":\"\",\"attr\":{\"EUQOP_ID\":\"0000000000\",\"\":\"\",\"piid\": \"16991\",\"wiid\": \"93045\",\"projectName\":\"CM\",\"TOKEN_USERID\":\"130049\",\"serviceName\":\"MBBA010\",\"CALL_SRC\":\"api\",\"methodName\":\"approve\",\"caction\":\"1\" },\"name\":\"\",\"detailMsg\":\"\",\"msg\":\"\",\"status\":\"0\",\"descName\":\"\",\"blocks\":{\"result\":{\"attr\":{\"limit\":\"10\",\"offset\":\"0\"},\"meta\":{\"attr\":{},\"columns\":[],\"desc\":\"\"},\"rows\":[]}}}")};
				new NameValuePair("eiinfo", "{\"msgKey\":\"\",\"attr\":{\"EUQOP_ID\":\"0000000000\",\"\":\"\",\"projectName\":\"CM\",\"TOKEN_USERID\":\"130049\",\"CALL_SRC\":\"api\",\"paperimage\":\"" + fileS + "\",\"caction\":\"1\" },\"name\":\"\",\"detailMsg\":\"\",\"msg\":\"\",\"status\":\"0\",\"descName\":\"\",\"blocks\":{\"result\":{\"attr\":{\"limit\":\"10\",\"offset\":\"0\"},\"meta\":{\"attr\":{},\"columns\":[],\"desc\":\"\"},\"rows\":[]}}}")};
		System.out.println("### Start...");
		method.setRequestBody(params);
		try {
			clt.executeMethod(method);
			if (method.getStatusCode() == HttpStatus.SC_OK) {
				InputStream inputStr = method.getResponseBodyAsStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(
						inputStr, RESPONSE_ENCODING));
				String resultJson = doPostString(reader);
				resultJson = HtmlUtils.htmlUnescape(resultJson);
				
				System.out.println("resultJson:" + resultJson);
				JSONObject jsonObject = JSONObject.fromObject(resultJson);
				Map resultMap = (Map)JSONObject.toBean(jsonObject, Map.class);
				
				if(("-1").equals(String.valueOf(resultMap.get("status")))){
					logger.info("WORKITEMID:"+map.get("WORKITEMID")+";ios send status error:"+resultMap.get("msg"));
					return false;
				}else if(("1").equals(String.valueOf(resultMap.get("status")))){
					logger.info("WORKITEMID:"+map.get("WORKITEMID")+";ios send status:"+resultMap.get("msg"));
				}
				return true;
			} else {
				logger.info("WORKITEMID:"+map.get("WORKITEMID")+";ios post error");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("WORKITEMID:"+map.get("WORKITEMID")+";postIos error:"+e.getMessage());
			// TODO: handle exception
			return false;
		}
	}

	// =====================================================
	private static String doPostString(BufferedReader reader) {
		StringBuffer responseStr = new StringBuffer();
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				responseStr.append(line);
			}

			if (null == responseStr || "".equals(responseStr))
				responseStr.append("");

			reader.close();
			// Debug output
			//System.out.println(reader);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseStr.toString();
	}

	// ===================================================================
	public static class UTF8PostMethod extends PostMethod {
		public UTF8PostMethod(String url) {
			super(url);
		}

		@Override
		public String getRequestCharSet() {
			return "utf-8";
		}
	}

}
