package hr.hsnopek.ecitizensintegration.domain.feature.authorization.client;

import hr.hsnopek.ecitizensintegration.configuration.ApplicationProperties;
import hr.hsnopek.ecitizensintegration.general.util.GenericHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;
import roauthorizationapischema.ObjectFactory;
import roauthorizationapischema.SignedAuthorizationUnionPermissionResponseType;

import javax.xml.bind.*;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;

@Service
public class AuthorizationServiceClient {

    private final KeyStore appKeyStore;
    private final KeyStore appTrustStore;

    public AuthorizationServiceClient(KeyStore appKeyStore, KeyStore appTrustStore) {
        this.appKeyStore = appKeyStore;
        this.appTrustStore = appTrustStore;
    }

    public SignedAuthorizationUnionPermissionResponseType sendAuthorizationRequest(String request)
            throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, IOException,
            KeyManagementException {

        HttpPost httpPost = new HttpPost(ApplicationProperties.AUTHORIZATION_SERVICE_AUTHORIZATION_DATA_URL);
        if(StringUtils.isNotBlank(request)){
            StringEntity stringEntity = new StringEntity(request);
            httpPost.setEntity(stringEntity);
        }
        GenericHttpClient genericHttpClient = new GenericHttpClient();
        CloseableHttpClient httpClient = genericHttpClient.getHttpClient(
                appKeyStore,
                ApplicationProperties.APPLICATION_KEYSTORE_PASSWORD,
                appTrustStore,
                String.valueOf(ContentType.APPLICATION_XML)
        );

        CloseableHttpResponse closeableHttpResponse = httpClient.execute(httpPost);
        HttpEntity entity = closeableHttpResponse.getEntity();
        if(entity != null){
            return unmarshall(EntityUtils.toString(entity));
        }
        return null;
    }

    public SignedAuthorizationUnionPermissionResponseType unmarshall(String xml) throws IOException {
        SignedAuthorizationUnionPermissionResponseType response = null;
        try(InputStream is = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))){
            Source source = new StreamSource(is);
            JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class.getPackage().getName());
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            JAXBElement<SignedAuthorizationUnionPermissionResponseType> root =
                    jaxbUnmarshaller.unmarshal(source, SignedAuthorizationUnionPermissionResponseType.class);
            response = root.getValue();
        } catch (JAXBException e){
            e.printStackTrace();
        }
        return response;
    }
}
