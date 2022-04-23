package hr.hsnopek.ecitizensintegration.domain.feature.okp.configuration;

import hr.apis_it.umu._2013.services.gsbservice.GSBServicePortType;
import hr.apis_it.umu._2013.types.gsb.SendMessageRequest;
import hr.apis_it.umu._2013.types.gsb.SendMessageResponse;
import hr.hsnopek.ecitizensintegration.configuration.ApplicationProperties;
import korisnickipretinac.KorisnickiPretinacPorukaType;
import korisnickipretinacrest.OdgovorType;
import korisnickipretinacrest.UpitType;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.configuration.security.FiltersType;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.ws.soap.SOAPBinding;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class OKPConfiguration {

    // CXF ports

    @Bean
    public GSBServicePortType gsbService() {

        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        Map<String, Object> properties = new HashMap<>();

        // add this classes to JAXB context

        properties.put(
                "jaxb.additionalContextClasses",
                new Class[]{
                        SendMessageRequest.class,
                        SendMessageResponse.class,
                        KorisnickiPretinacPorukaType.class,
                        UpitType.class,
                        OdgovorType.class
                }
        );

        factory.setProperties(properties);
        factory.setServiceClass(GSBServicePortType.class);
        factory.setAddress(ApplicationProperties.OKP_SERVICE_URL);
        factory.setBindingId(SOAPBinding.SOAP11HTTP_BINDING);

        // logging
        factory.getInInterceptors().add(loggingInInterceptor());
        factory.getOutInterceptors().add(loggingOutInterceptor());

        GSBServicePortType port = (GSBServicePortType) factory.create();

        // adding namespace to soap envelope
        Map<String, String> nsMap = new HashMap<>();
        nsMap.put("gsb", "http://apis-it.hr/umu/2013/types/gsb");

        Client client = ClientProxy.getClient(port);
        client.getRequestContext().put("soap.env.ns.map", nsMap);

        return port;
    }

    // SSL configuration

    @Bean
    public LoggingInInterceptor loggingInInterceptor() {
        return new LoggingInInterceptor();
    }

    @Bean
    public LoggingOutInterceptor loggingOutInterceptor() {
        return new LoggingOutInterceptor();
    }

    @Bean
    public HTTPConduit httpConduit() throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        Client client = ClientProxy.getClient(gsbService());
        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();

        HTTPClientPolicy policy = new HTTPClientPolicy();
        policy.setConnectionTimeout(10000);
        policy.setReceiveTimeout(5000);
        policy.setAllowChunking(false);
        httpConduit.setClient(policy);

        httpConduit.setTlsClientParameters(tlsClientParameters());
        return httpConduit;
    }

    @Bean
    public TLSClientParameters tlsClientParameters() throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        TLSClientParameters tlsClientParameters =
                new TLSClientParameters();
        tlsClientParameters.setSecureSocketProtocol("TLS");
        // shouldn't be used in production
        tlsClientParameters.setDisableCNCheck(true);
        // this should use real trustStore instead of fake one
        tlsClientParameters.setTrustManagers(trustManagers());
        tlsClientParameters.setKeyManagers(keyManagers());
        tlsClientParameters.setCipherSuitesFilter(cipherSuitesFilter());
        return tlsClientParameters;
    }

    @Bean
    public TrustManager[] trustManagers() throws NoSuchAlgorithmException {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        try(InputStream is = new ClassPathResource(ApplicationProperties.APPLICATION_TRUSTSTORE_PATH).getInputStream()) {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(is, ApplicationProperties.APPLICATION_TRUSTSTORE_PASSWORD.toCharArray());
            trustManagerFactory.init(trustStore);
        } catch (IOException e) {
            throw new AssertionError(e);
        } catch (CertificateException | KeyStoreException e) {
            e.printStackTrace();
        }
        return trustManagerFactory.getTrustManagers();
    }

    @Bean
    public KeyManager[] keyManagers() throws NoSuchAlgorithmException, KeyStoreException,
            UnrecoverableKeyException {
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

        // if algorithm cannot find the right certificate from keystore, write your own implementation
        try(InputStream is = new ClassPathResource(ApplicationProperties.APPLICATION_KEYSTORE_PATH).getInputStream()) {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(is, ApplicationProperties.APPLICATION_KEYSTORE_PASSWORD.toCharArray());
            keyManagerFactory.init(keyStore, ApplicationProperties.APPLICATION_KEYSTORE_PASSWORD.toCharArray());
        } catch (IOException e) {
            throw new AssertionError(e);
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        return keyManagerFactory.getKeyManagers();
    }

    @Bean
    public FiltersType cipherSuitesFilter() {
        FiltersType filter = new FiltersType();
        final List<String> include = filter.getInclude();
        include.add(".*");
        include.add(".*_EXPORT_.*");
        include.add(".*_EXPORT1024_.*");
        include.add(".*_WITH_DES_.*");
        include.add(".*_WITH_AES_.*");
        include.add(".*_WITH_NULL_.*");
        include.add(".*_RSA_WITH_AES_.*");
        include.add(".*_DH_anon_.*");
        return filter;
    }

    public class HttpHeaderInterceptor extends AbstractPhaseInterceptor<Message> {

        public HttpHeaderInterceptor() {
            super(Phase.POST_LOGICAL);
        }

        public void handleMessage(Message message) {
            Map<String, List<String>> headers = new HashMap<String, List<String>>();
            message.put(Message.PROTOCOL_HEADERS, headers);
        }

    }
}
