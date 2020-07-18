package com.qdport.integrated.service.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * HttpClient工具，get请求重试3次，post请求不重试
 * </p>
 *
 * @author wangxiaolong
 * @since 2019-10-31
 */
@Slf4j
public class HttpClientUtils {

	private static CloseableHttpClient httpClient = null;
	private static CloseableHttpClient httpRetryClient = null;
	/**
	 * ms毫秒,从池中获取链接超时时间
	 */
	private static final int CONNECTION_REQUEST_TIMEOUT = 5000;
	/**
	 * ms毫秒,建立链接超时时间
	 */
	private static final int CONNECT_TIMEOUT = 5000;
	/**
	 * ms毫秒,读取超时时间
	 */
	private static final int SOCKET_TIMEOUT = 10000;
	/**
	 * 最大总并发,很重要的参数
	 */
	private static final int MAX_TOTAL = 500;
	/**
	 * 每路并发,很重要的参数
	 */
	private static final int MAX_PER_ROUTE = 100;
	/**
	 * 重试次数
	 */
	private static final int RETRY_NUM = 3;

	static {
		// 设置连接池
		ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
		LayeredConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(createIgnoreVerifySsl(),
				createHostnameVerifier());
		Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", plainsf).register("https", sslsf).build();
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
		// 将最大连接数增加
		cm.setMaxTotal(MAX_TOTAL);
		// 将每个路由基础的连接增加
		cm.setDefaultMaxPerRoute(MAX_PER_ROUTE);
		CookieStore cookieStore = new BasicCookieStore();
		BasicClientCookie cookie = new BasicClientCookie("sessionID", "######");
		cookie.setDomain("#####");
		cookie.setPath("/");
		cookieStore.addCookie(cookie);
		// 请求重试处理
		HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
			@Override
			public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
				// 如果超过重试次数，就放弃
				if (executionCount >= RETRY_NUM) {
					return false;
				}
				// 如果服务器丢掉了连接，那么就重试
				if (exception instanceof NoHttpResponseException) {
					log.warn("重试... ... {}", executionCount);
					return true;
				}
				// 超时
				if (exception instanceof InterruptedIOException) {
					log.warn("链接超时重试... ... {}", executionCount);
					return true;
				}
				// 不要重试SSL握手异常
				if (exception instanceof SSLHandshakeException) {
					return false;
				}
				// 目标服务器不可达
				if (exception instanceof UnknownHostException) {
					return false;
				}
				// 连接被拒绝
				if (exception instanceof ConnectTimeoutException) {
					return false;
				}
				// SSL握手异常
				if (exception instanceof SSLException) {
					return false;
				}

				HttpClientContext clientContext = HttpClientContext.adapt(context);
				HttpRequest request = clientContext.getRequest();
				// 如果请求是幂等的，就再次尝试
				if (!(request instanceof HttpEntityEnclosingRequest)) {
					return true;
				}
				return false;
			}
		};
		// 配置请求的超时设置
		RequestConfig requestRetryConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD_STRICT)
				.setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT).setConnectTimeout(CONNECT_TIMEOUT)
				.setSocketTimeout(SOCKET_TIMEOUT).build();
		RequestConfig requestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD_STRICT).build();
		// post请求使用（无超时时间、不需要重试）
		httpClient = HttpClients.custom().setConnectionManager(cm).setDefaultCookieStore(cookieStore)
				.setDefaultRequestConfig(requestConfig).build();
		// get请求使用（有超时时间、重试3次）
		httpRetryClient = HttpClients.custom().setConnectionManager(cm).setDefaultCookieStore(cookieStore)
				.setDefaultRequestConfig(requestRetryConfig).setRetryHandler(httpRequestRetryHandler).build();
	}

	public static String sendGet(String url) {
		CloseableHttpResponse response = null;
		HttpGet httpGet = null;
		try {
			httpGet = new HttpGet(url);
			response = httpRetryClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			String result = EntityUtils.toString(entity, "utf-8");
			if (response.getStatusLine().getStatusCode() == HttpStatus.HTTP_OK) {
				return result;
			} else {
				log.error("服务器异常，状态码：{}，信息：{}", response.getStatusLine().getStatusCode(), result);
			}
		} catch (Exception e) {
			log.error("httpClient异常:", e);
		} finally {
			if (response != null) {
				try {
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					log.info("", e);
				} finally {
					try {
						response.close();
					} catch (IOException e) {
						log.info("", e);
					}
				}
			}
			httpGet.releaseConnection();
		}
		return null;
	}

	public static String sendPost(String url, Map<String, String> headers, String json) {
		CloseableHttpResponse response = null;
		HttpPost httpPost = null;
		try {
			httpPost = new HttpPost(url);
			if (json != null) {
				// 解决中文乱码问题
				StringEntity stringEntity = new StringEntity(json, "utf-8");
				httpPost.setEntity(stringEntity);
			}
			httpPost.setHeaders(builderHeader(headers));
			response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			String result = EntityUtils.toString(entity, "utf-8");
			if (response.getStatusLine().getStatusCode() == HttpStatus.HTTP_OK) {
				return result;
			} else {
				log.error("服务器异常，状态码：{}，信息：{}", response.getStatusLine().getStatusCode(), result);
			}
		} catch (Exception e) {
			log.error("httpClient异常:", e);
		} finally {
			if (response != null) {
				try {
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					log.info("", e);
				} finally {
					try {
						response.close();
					} catch (IOException e) {
						log.info("", e);
					}
				}
			}
			httpPost.releaseConnection();
		}
		return null;
	}

	public static String sendGet(String url, Map<String, Object> params) {
		url += getParams(params, true);
		return sendGet(url);
	}

	public static <T> T sendGet(String url, Class<T> clazz) {
		String resultString = sendGet(url);
		if (StrUtil.isEmptyOrUndefined(resultString)) {
			return null;
		}
		return JSON.parseObject(resultString, clazz);
	}

	public static <T> T sendGet(String url, Map<String, Object> params, Class<T> clazz) {
		String resultString = sendGet(url, params);
		if (StrUtil.isEmptyOrUndefined(resultString)) {
			return null;
		}
		return JSON.parseObject(resultString, clazz);
	}

	public static String sendPost(String url, Map<String, Object> json) {
		Map<String, String> headers = new HashMap<>();
		return sendPost(url, headers, json);
	}

	public static String sendPost(String url, Map<String, String> headers, Map<String, Object> json) {
		return sendPost(url, headers, json == null ? null : JSON.toJSONString(json));
	}

	public static <T> T sendPost(String url, Map<String, Object> json, Class<T> clazz) {
		Map<String, String> headers = new HashMap<>();
		String resultString = sendPost(url, headers, json);
		if (StrUtil.isEmptyOrUndefined(resultString)) {
			return null;
		}
		return JSON.parseObject(resultString, clazz);
	}

	public static <T> T sendPost(String url, Map<String, String> headers, Map<String, Object> json, Class<T> clazz) {
		String resultString = sendPost(url, headers, json);
		if (StrUtil.isEmptyOrUndefined(resultString)) {
			return null;
		}
		return JSON.parseObject(resultString, clazz);
	}

	/**
	 * 参数连接
	 *
	 * @param params
	 * @return
	 */
	private static String getParams(Map<String, Object> params, boolean urlFlag) {
		StringBuffer param = new StringBuffer();
		int i = 0;
		for (String key : params.keySet()) {
			if (i == 0 && urlFlag) {
				param.append("?");
			} else if (i != 0) {
				param.append("&");
			}
			param.append(key).append("=").append(params.get(key));
			i++;
		}
		return param.toString();

	}

	/**
	 * 组装头部
	 *
	 * @param map 头部map信息
	 * @return
	 */
	public static Header[] builderHeader(Map<String, String> map) {
		if (map.size() == 0) {
			map = createJsonHeader();
		}
		Header[] headers = new BasicHeader[map.size()];
		int i = 0;
		for (String str : map.keySet()) {
			headers[i] = new BasicHeader(str, map.get(str));
			i++;
		}
		return headers;
	}

	private static SSLContext createIgnoreVerifySsl() {
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			// 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
			X509TrustManager trustManager = new X509TrustManager() {
				@Override
				public void checkClientTrusted(X509Certificate[] certificates, String paramString)
						throws CertificateException {
				}

				@Override
				public void checkServerTrusted(X509Certificate[] ax509certificate, String paramString)
						throws CertificateException {
				}

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			sc.init(null, new TrustManager[] { trustManager }, new SecureRandom());
			return sc;
		} catch (NoSuchAlgorithmException | KeyManagementException e) {
			log.info("", e);
		}
		return null;
	}

	private static HostnameVerifier createHostnameVerifier() {
		HostnameVerifier hv = new HostnameVerifier() {
			@Override
			public boolean verify(String urlHostName, SSLSession session) {
				return urlHostName.equals(session.getPeerHost());
			}
		};
		return hv;
	}

	private static Map<String, String> createJsonHeader() {
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");
		return headers;
	}

}
