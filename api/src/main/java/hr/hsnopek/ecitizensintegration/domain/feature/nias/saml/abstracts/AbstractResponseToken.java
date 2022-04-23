package hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.abstracts;

import org.opensaml.saml2.core.StatusResponseType;

public abstract class AbstractResponseToken extends AbstractToken {
	
	public abstract StatusResponseType getResponse();
	
}
