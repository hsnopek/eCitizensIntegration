package hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.abstracts;

import hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.MetadataInfo;
import org.opensaml.ws.transport.http.HTTPInTransport;

public abstract class AbstractToken {
	public MetadataInfo metadata;
	
	public HTTPInTransport httpIn;
}
