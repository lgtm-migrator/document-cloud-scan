package com.dynamsoft.twaindirect.rest;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AsyncRestClient implements Callback {
	private static Logger logger = LoggerFactory.getLogger(AsyncRestClient.class.getName());

	private OkHttpClient httpClient;
	private Long timeoutMillisec = null;

	public void setTimeoutMillisec(Long timeoutMillisec) {
		this.timeoutMillisec = timeoutMillisec;
	}

	public void open(boolean boHttps) {
		OkHttpClient.Builder clientBuilder = (new OkHttpClient()).newBuilder();
		if (this.timeoutMillisec != null) {
			clientBuilder.connectTimeout(this.timeoutMillisec.longValue(), TimeUnit.MILLISECONDS);
			clientBuilder.callTimeout(this.timeoutMillisec.longValue(), TimeUnit.MILLISECONDS);
			clientBuilder.readTimeout(this.timeoutMillisec.longValue(), TimeUnit.MILLISECONDS);
		}

		if (boHttps) {
			setSSLOption(clientBuilder);
		}

		this.httpClient = clientBuilder.build();
	}

	private void setSSLOption(OkHttpClient.Builder clientBuilder) {
		class MyX509TrustManager implements X509TrustManager {
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}
		}
		;
		try {
			SSLContext sslContext = SSLContext.getInstance("SSL");

			sslContext.init(null, new TrustManager[] { new MyX509TrustManager() }, new SecureRandom());
			clientBuilder.sslSocketFactory(sslContext.getSocketFactory(), new MyX509TrustManager());

			clientBuilder.hostnameVerifier(new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});
		} catch (Exception ex) {
			logger.error("failed :", ex);
		}
	}

	public void post(Request request) {
		post(request, null);
	}

	public void post(Request request, Long timeoutMillisec) {
		request(request, timeoutMillisec);
	}

	public void get(Request request) {
		get(request, null);
	}

	public void get(Request request, Long timeoutMillisec) {
		request(request, timeoutMillisec);
	}

	private void request(Request request, Long timeoutMillisec) {
		logger.info(request.url().toString());
		if (this.httpClient == null) {
			open(request.isHttps());
		}
		OkHttpClient.Builder clientBuilder = this.httpClient.newBuilder();
		if (timeoutMillisec != null) {
			clientBuilder.connectTimeout(timeoutMillisec.longValue(), TimeUnit.MILLISECONDS);
			clientBuilder.callTimeout(timeoutMillisec.longValue(), TimeUnit.MILLISECONDS);
			clientBuilder.readTimeout(timeoutMillisec.longValue(), TimeUnit.MILLISECONDS);
		}
		OkHttpClient newClient = clientBuilder.build();
		newClient.newCall(request).enqueue(this);
	}
}
