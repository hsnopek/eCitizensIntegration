package hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.utils;

import org.opensaml.Configuration;
import org.opensaml.xml.XMLObjectBuilderFactory;

import javax.xml.namespace.QName;

public class SamlUtil<T>{

    public static <T> T buildSAMLObjectWithDefaultName(final Class<T> clazz) throws NoSuchFieldException, IllegalAccessException {
        XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();

        QName defaultElementName = (QName)clazz.getDeclaredField("DEFAULT_ELEMENT_NAME").get(null);

        return (T)builderFactory.getBuilder(defaultElementName).buildObject(defaultElementName);
    }

}
