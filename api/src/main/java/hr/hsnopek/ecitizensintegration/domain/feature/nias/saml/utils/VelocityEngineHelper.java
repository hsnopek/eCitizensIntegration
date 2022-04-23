package hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.utils;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

public class VelocityEngineHelper {
	public static VelocityEngine getInstance() {
		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.ENCODING_DEFAULT, "UTF-8");
		//velocityEngine.setProperty(RuntimeConstants.OUTPUT_ENCODING, "UTF-8");
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		velocityEngine.setProperty("classpath.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		velocityEngine.init();
		return velocityEngine;
	}
}
