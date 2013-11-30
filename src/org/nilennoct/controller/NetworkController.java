package org.nilennoct.controller;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.*;
import org.apache.http.message.BasicHeader;
import org.nilennoct.model.EncryptNameValuePair;
import org.w3c.dom.Document;

import java.net.SocketTimeoutException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Neo
 * Date: 13-9-5
 * Time: 上午12:02
 */
public class NetworkController {
	private static NetworkController nc = null;
	private static final UIController uc = UIController.getInstance();

	private final String hostport = "game.ma.mobimon.com.tw:10001";
	private final String DefaultUserAgent = "Million/1.0.2 (iPad; iPad2,1; 6.1) ";
	public static final String baseKey = "rBwj1MIAivVN222b";

	private final int connectionTimeout = 15000;
	private final int readTimeout = 30000;

	public String proxyHost = "127.0.0.1";
	public int proxyPort = 8087;
	public boolean usingProxy = false;

	private final RequestConfig requestConfig;
	private final ArrayList<BasicHeader> defaultHeaders = new ArrayList<BasicHeader>();
	private final RedirectStrategy redirectStrategy = new LaxRedirectStrategy();

	private NetworkController() {
		requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(connectionTimeout)
				.setSocketTimeout(readTimeout)
				.setRedirectsEnabled(true).build();

		defaultHeaders.add(new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded"));
		defaultHeaders.add(new BasicHeader(HttpHeaders.ACCEPT_LANGUAGE, "zh-cn"));
		defaultHeaders.add(new BasicHeader(HttpHeaders.ACCEPT, "*/*"));
		defaultHeaders.add(new BasicHeader(HttpHeaders.CONNECTION, "keep-alive"));
		defaultHeaders.add(new BasicHeader(HttpHeaders.USER_AGENT, DefaultUserAgent));
	}

	public static NetworkController getInstance() {
		if (nc == null) {
			nc = new NetworkController();
		}

		return nc;
	}

	public synchronized Document connect(CloseableHttpClient client, String urlPart) throws Exception {
		HttpPost method = new HttpPost("http://" + hostport + urlPart);
		CloseableHttpResponse response = client.execute(method);

		Document doc = XMLParser.parseXML(response.getEntity().getContent());

		response.close();

		return doc;
	}

	synchronized Document connect(CloseableHttpClient client, String urlPart, List<NameValuePair> parameters) throws Exception {
		HttpPost method = new HttpPost("http://" + hostport + urlPart);

		method.setEntity(new UrlEncodedFormEntity(parameters));

		CloseableHttpResponse response = client.execute(method);

		Document doc = XMLParser.parseXML(response.getEntity().getContent());

		response.close();

		return doc;
	}

	boolean checkCode(Document doc) {
		int code = XMLParser.getErrorCode(doc);

		if (code != 0) {
			String msg = XMLParser.getNodeValue(doc, "message");
			uc.logInThread("Err" + code + ": " + msg);
			return false;
		}

		return true;
	}

	public void register(String name, String password, String invitor, String session_id) {
		HttpClientBuilder builder = HttpClients.custom();
		if (usingProxy) {
			HttpHost proxy = new HttpHost(proxyHost, proxyPort);
			builder = builder.setProxy(proxy);
		}

		CookieStore cookieStore = new BasicCookieStore();
		CloseableHttpClient client = builder
				.setDefaultRequestConfig(requestConfig)
				.setDefaultCookieStore(cookieStore)
				.setDefaultHeaders(defaultHeaders)
				.setRedirectStrategy(redirectStrategy)
				.build();

		try {
			if (session_id.equals("")) {
				session_id = regist(client, name, password, invitor);
			}
			System.out.println("session_id: " + session_id);
			if (session_id != null) {
				saveCharacter(client, name);
				boolean success = false;
				int count = 0;
				while ( ! success && count < 3) {
					++count;
					nextTutorial(client, "1000", session_id);
					nextTutorial(client, "7000", session_id);
					success = nextTutorial(client, "8000", session_id);
				}
				if (success) {
					uc.log("User \"" + name + "\" has been registered.");
				}
				else {
					uc.log("User \"" + name + "\" failed to register.");
				}
			}

			client.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	String regist(CloseableHttpClient client, String name, String password, String invitor) throws Exception {
		Random random = new SecureRandom();
		String guid = (random.nextInt(899999) + 100000) + "d1c903cdb5d0aab2725b7803db" + (random.nextInt(899999) + 100000);

		List<NameValuePair> parameters = new ArrayList<NameValuePair>(5);
		parameters.add(new EncryptNameValuePair("login_id", name));
		parameters.add(new EncryptNameValuePair("password", password));
		parameters.add(new EncryptNameValuePair("invitation_id", invitor));
		parameters.add(new EncryptNameValuePair("platform", "2"));
		parameters.add(new EncryptNameValuePair("param", guid));
		System.out.println("/connect/app/regist");
		String session_id = null;
		try {
			Document doc = connect(client, "/connect/app/regist", parameters);

			if ( ! checkCode(doc)) {
				return session_id;
			}

			session_id = XMLParser.getNodeValue(doc, "session_id");
		}
		catch (SocketTimeoutException e) {
			System.out.println(e);
			return regist(client, name, password, invitor);
		}

		return session_id;
	}

	boolean saveCharacter(CloseableHttpClient client, String name) throws Exception {
		List<NameValuePair> parameters = new ArrayList<NameValuePair>(2);
		parameters.add(new EncryptNameValuePair("name", name));
		parameters.add(new EncryptNameValuePair("country", "1"));
		System.out.println("/connect/app/tutorial/save_character");

		try {
			Document doc = connect(client, "/connect/app/tutorial/save_character", parameters);
			if ( ! checkCode(doc)) {
				return false;
			}
		}
		catch (SocketTimeoutException e) {
			System.out.println(e);
			return saveCharacter(client, name);
		}

		return true;
	}

	boolean nextTutorial(CloseableHttpClient client, String step, String session_id) throws Exception {
		List<NameValuePair> parameters = new ArrayList<NameValuePair>(2);
		parameters.add(new EncryptNameValuePair("step", step));
		parameters.add(new EncryptNameValuePair("S", session_id));
		System.out.println("/connect/app/tutorial/next/" + step);
		try {
			Document doc = connect(client, "/connect/app/tutorial/next", parameters);
			if (doc != null && !checkCode(doc)) {
				return false;
			}
		}
		catch (SocketTimeoutException e) {
			System.out.println(e);
			return nextTutorial(client, step, session_id);
		}

		return true;
	}
}
