package hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.abstracts;

import org.opensaml.saml2.core.RequestAbstractType;

public abstract class AbstractRequestToken extends AbstractToken{

	public abstract RequestAbstractType getRequest();
	
}
