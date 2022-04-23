package hr.hsnopek.ecitizensintegration.domain.feature.authorization.util;

import authorizationresponsev3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class RegistrationFormUtilImpl {
	
	private static final Logger logger = LoggerFactory.getLogger(RegistrationFormUtilImpl.class);
	private static final String NAMESPACE_URI = "http://eovlastenja.fina.hr/authorizationdocument/v3";
	private static final String LOCAL_PART = "ServiceResponse";

	public RegistrationFormUtilImpl() {
	}

	public ServiceResponseType createResponse(ServiceRequestType serviceRequest,
											  List<PermissionType> permissionList) {
		ServiceResponseType serviceResponse = new ServiceResponseType();
		serviceResponse.setForRequestId(serviceRequest.getId());
		ServiceDataType serviceDataType = new ServiceDataType();
		AuthorizationDataType auth = new AuthorizationDataType();
		auth.setPermissions(new PermissionsType());
		serviceDataType.setAuthorizationData(auth);
		serviceResponse.setServiceData(serviceDataType);
		serviceDataType.getAuthorizationData().getPermissions().getPermission().addAll(permissionList);
		serviceResponse.setId("_ServiceResponse");
		serviceResponse.setSignatures(new SignaturesType());
		return serviceResponse;
	}

	public authorizationrequestv3.ServiceRequestType unmarshalServiceRequest(String pServiceRequest) {
		JAXBElement<authorizationrequestv3.ServiceRequestType> root = null;
		try(InputStream is = new ByteArrayInputStream(pServiceRequest.getBytes(StandardCharsets.UTF_8))) {
			JAXBContext jaxbContext = JAXBContext.newInstance(authorizationrequestv3.ObjectFactory.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
	 
			root = jaxbUnmarshaller.unmarshal(new StreamSource(is), authorizationrequestv3.ServiceRequestType.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return root.getValue();
	}
	
	public String marshalServiceResponse(ServiceResponseType pResponse) {
		QName tQName = new QName(NAMESPACE_URI, LOCAL_PART);
		JAXBElement<ServiceResponseType> tElement = new JAXBElement<>(tQName, ServiceResponseType.class, pResponse);
		StringWriter sw = new StringWriter();
		JAXB.marshal(tElement, sw);
		logger.debug("Serijalizirani ServiceResponse: {}", sw.toString());
		return sw.toString();
	}

	
}
