package hr.hsnopek.ecitizensintegration.domain.feature.authorization.mapper;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import org.apache.commons.lang3.StringUtils;

public class AuthorizationNamespaceMapper extends NamespacePrefixMapper {

    private static final String DEFAULT_NAMESPACE_PREFIX = StringUtils.EMPTY;
    private static final String AUTHORIZATION_URI = "http://eovlastenja.fina.hr/authorizationdocument/v3";

    @Override
    public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
        if(AUTHORIZATION_URI.equals(namespaceUri)){
            return DEFAULT_NAMESPACE_PREFIX;
        }
        return "http://eovlastenja.fina.hr/authorizationdocument";
    }

    @Override
    public String[] getPreDeclaredNamespaceUris() {
        return new String[] { DEFAULT_NAMESPACE_PREFIX };
    }
}
