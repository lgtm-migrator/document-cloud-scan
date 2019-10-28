package com.dynamsoft.could;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import com.dynamsoft.DLogger;
import com.dynamsoft.could.entity.TCTokens;

public abstract class TwainCloudClientBase {

	protected final static int SUCCESS_CODE = 200;
	protected final static int DELETE_SUCCESS_CODE = 204;
	
	protected final static String UAER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.87 Safari/537.36";
	protected final static String Accept = "application/json;charset=utf-8"; // "text/plain;charset=utf-8"
	
	protected TCTokens authorizationToken;
	protected WebServiceUrlProvider urlProvider;
	protected ICloudCallback cloudCallback;
	
	
	protected TwainCloudClientBase(String httpServerUrl, ICloudCallback cloudCallback) {
		this.urlProvider = WebServiceUrlProvider.getInstance(httpServerUrl);
		this.cloudCallback = cloudCallback;
	}

	private HttpGet _get = null;

	protected String httpGet(String url)
    {
		String result = null;
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        try{
            client = HttpClients.createDefault();
            _get = new HttpGet(url);
            
            _get.setProtocolVersion(HttpVersion.HTTP_1_1);
            _get.setHeader(new BasicHeader("Accept", Accept));
            _get.setHeader(new BasicHeader("User-Agent", UAER_AGENT));

            if (null != this.authorizationToken && !this.authorizationToken.token.isEmpty())
            {
            	DLogger.println("authorization:" + this.authorizationToken.token);
            	_get.setHeader(new BasicHeader("authorization", this.authorizationToken.token));
            } else {

            	DLogger.println("authorization: (none)");
            }
            
            response = client.execute(_get);
            int statusCode = response.getStatusLine().getStatusCode();
            if (SUCCESS_CODE == statusCode){
                result = EntityUtils.toString(response.getEntity(),"UTF-8");
            }
            response.close();
            _get = null;
            
        } catch (ClientProtocolException e) {
    		cloudCallback.onError(e.getMessage());
		} catch (IOException e1) {
    		cloudCallback.onError(e1.getMessage());
		}
        
        try {
        	if(null != client) {
				client.close();
		        client = null;
        	}
		} catch (IOException e) {
    		cloudCallback.onError(e.getMessage());
		}
        return result;
    }

    protected String httpPostJson(String url, String strPostData)
    {
    	return httpPost(url, "application/json; charset=utf-8", new StringEntity(strPostData, "utf-8"));
    }
    
    protected String httpPost(String url, String strPostData)
    {
    	return httpPost(url, "application/x-www-form-urlencoded; charset=utf-8", new StringEntity(strPostData, "utf-8"));
    }
    
    protected String httpPostFile(String url, byte[] data)
    {
    	try {

            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.addBinaryBody("RemoteFile", data, ContentType.DEFAULT_BINARY, "RemoteFile.pdf");
            
        	return httpPost(url, null, entityBuilder.build());
    		
    	}catch(Exception e) {
    		e.printStackTrace();
    		return "";
    	}
    }

    protected String httpDelete(String url, String strContentType)
    {
        String result = null;
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        try{
        	do {
	            client = HttpClients.createDefault();
	            HttpDelete httpDelete = new HttpDelete(url);
	            httpDelete.setProtocolVersion(HttpVersion.HTTP_1_1);
	            
	            if(null != strContentType) {
	            	httpDelete.setHeader(new BasicHeader("Content-Type", strContentType));
	            }
	            
				httpDelete.setHeader(new BasicHeader("Accept", Accept));					// text/plain
	            httpDelete.setHeader(new BasicHeader("User-Agent", UAER_AGENT));
	
	            if (null != this.authorizationToken && !this.authorizationToken.token.isEmpty())
	            {
	            	DLogger.println("authorization:" + this.authorizationToken.token);
	            	httpDelete.setHeader(new BasicHeader("authorization", this.authorizationToken.token));
	            } else {
	
	            	DLogger.println("authorization: (none)");
	            }

	            response = client.execute(httpDelete);
	            int statusCode = response.getStatusLine().getStatusCode();
	            
	            if ( DELETE_SUCCESS_CODE == statusCode || SUCCESS_CODE == statusCode){
	            	if(null == response.getEntity())
	            		result = "";
	            	else
	            		result = EntityUtils.toString(response.getEntity(),"UTF-8");
	            } else {
	            	
	            	if(null != cloudCallback) {
	
	                	String err = String.format("Http Server Return: %d", statusCode);
	            		cloudCallback.onError(err);
	            	}          	
	            }
	            
	            response.close();
	            
        	}while(false);
        	
        } catch (ClientProtocolException e) {
    		cloudCallback.onError(e.getMessage());
		} catch (IOException e1) {
    		cloudCallback.onError(e1.getMessage());
		}

        try {
        	if(null != client) {
				client.close();
		        client = null;
        	}
		} catch (IOException e) {
    		cloudCallback.onError(e.getMessage());
		}
        return result;
    }
    

    private HttpPost post = null;
    protected String httpPost(String url, String strContentType, HttpEntity entity)
    {
        String result = null;
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        try{
        	do {
	            client = HttpClients.createDefault();
	            post = new HttpPost(url);
	            post.setEntity(entity);
	            post.setProtocolVersion(HttpVersion.HTTP_1_1);
	            
	            if(null != strContentType) {
	            	post.setHeader(new BasicHeader("Content-Type", strContentType));
	            }
	            
				post.setHeader(new BasicHeader("Accept", Accept));					// text/plain
	            post.setHeader(new BasicHeader("User-Agent", UAER_AGENT));
	
	            if (null != this.authorizationToken && !this.authorizationToken.token.isEmpty())
	            {
	            	DLogger.println("authorization:" + this.authorizationToken.token);
	            	post.setHeader(new BasicHeader("authorization", this.authorizationToken.token));
	            } else {
	
	            	DLogger.println("authorization: (none)");
	            }

	            response = client.execute(post);
	            int statusCode = response.getStatusLine().getStatusCode();
	            
	            if (SUCCESS_CODE == statusCode){
	            	if(null == response.getEntity())
	            		result = "";
	            	else
	            		result = EntityUtils.toString(response.getEntity(),"UTF-8");
	            } else {
	            	
	            	if(null != cloudCallback) {
	
	                	String err = String.format("Http Server Return: %d", statusCode);
	            		cloudCallback.onError(err);
	            	}          	
	            }
	            
	            response.close();
	            
        	}while(false);
        	
        } catch (ClientProtocolException e) {
    		cloudCallback.onError(e.getMessage());
		} catch (IOException e1) {
			if(null == post) {
				// cancelled
	    		cloudCallback.onError("User cancelled.");
			} else {
	    		cloudCallback.onError(e1.getMessage());
			}
		}

		post = null;
        try {
        	if(null != client) {
				client.close();
		        client = null;
        	}
		} catch (IOException e) {
    		cloudCallback.onError(e.getMessage());
		}
        return result;
    }
    
    public void cancelPost()
    {
    	if(null != post) {
    		post.abort();
    		post = null;
    	}
    }

    public void cancelGet()
    {
    	if(null != _get) {
    		_get.abort();
    		_get = null;
    	}
    }
    
    protected class SSLClient extends DefaultHttpClient{
        public SSLClient() throws Exception{
            super();
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {
					@Override
					public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
						// TODO Auto-generated method stub
						
					}
					@Override
					public void checkServerTrusted(X509Certificate[] chain, String authType)
							throws CertificateException {
						// TODO Auto-generated method stub
						
					}
					@Override
					public X509Certificate[] getAcceptedIssuers() {
						// TODO Auto-generated method stub
						return null;
					}
            };
            ctx.init(null, new TrustManager[]{tm}, null);
            SSLSocketFactory ssf = new SSLSocketFactory(ctx,SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            ClientConnectionManager ccm = this.getConnectionManager();
            SchemeRegistry sr = ccm.getSchemeRegistry();
            sr.register(new Scheme("https", 443, ssf));
        }
    }

}
