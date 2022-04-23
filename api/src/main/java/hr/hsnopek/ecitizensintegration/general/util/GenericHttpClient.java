package hr.hsnopek.ecitizensintegration.general.util;

import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import java.security.*;

public class GenericHttpClient {

    public GenericHttpClient() {
    }

    public CloseableHttpClient getHttpClient(KeyStore keyStore, String keyStorePassword, KeyStore trustStore, String contentType)
            throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        CloseableHttpClient closeableHttpClient = null;
        try{
            SSLContext sslContext = SSLContexts.custom()
                    .loadKeyMaterial(keyStore, keyStorePassword.toCharArray())
                    .loadTrustMaterial(trustStore, (certificate, authType) -> true).build();

            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create()
                    .register("https", sslConnectionSocketFactory)
                    .register("http", new PlainConnectionSocketFactory())
                    .build();

            HttpClientConnectionManager httpClientConnectionManager = new PoolingHttpClientConnectionManager(registry);
            HttpClientBuilder httpClientBuilder = HttpClients.custom().setConnectionManager(httpClientConnectionManager)
                    .addInterceptorFirst(new HttpRequestInterceptor() {
                        @Override
                        public void process(HttpRequest httpRequest, HttpContext httpContext) {
                            httpRequest.addHeader(HTTP.CONTENT_TYPE, contentType);
                        }
                    });
            closeableHttpClient = httpClientBuilder.build();
        } catch(Exception e){
            throw e;
        }
        return closeableHttpClient;
    }
}
