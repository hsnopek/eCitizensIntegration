package hr.hsnopek.ecitizensintegration.domain.feature.nias.saml.utils;

import org.opensaml.xml.security.SecurityHelper;
import org.opensaml.xml.security.x509.BasicX509Credential;

import javax.security.auth.x500.X500Principal;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

public class CertificateHelper {
	public static X509Certificate getCertificateFromCER(String certificatePath) {
		try(FileInputStream fis = new FileInputStream(certificatePath)) {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			return (X509Certificate) cf.generateCertificate(fis);
		} catch (CertificateException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static BasicX509Credential getBasicX509CredentialFromPFX(String certificatePath, String password) throws UnrecoverableKeyException {
		try {
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			keyStore.load(new FileInputStream(certificatePath), password.toCharArray());
			Enumeration<String> keyAliases = keyStore.aliases();
			while (keyAliases.hasMoreElements()) {
				String currentAlias = keyAliases.nextElement();
				if (keyStore.isKeyEntry(currentAlias)) {
					Key key = keyStore.getKey(currentAlias, password.toCharArray());
					if (key instanceof PrivateKey) {
						X509Certificate cert = (X509Certificate) keyStore.getCertificate(currentAlias);
						return SecurityHelper.getSimpleCredential(cert, (PrivateKey) key);
					}
				}
			}
		} catch (KeyStoreException | IOException | CertificateException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static BasicX509Credential getBasicX509CredentialFromJKS(KeyStore keyStore, String alias, String password) throws UnrecoverableKeyException {
		try {
			Key key = keyStore.getKey(alias, password.toCharArray());
			X509Certificate cert = (X509Certificate) keyStore.getCertificate(alias);
			return SecurityHelper.getSimpleCredential(cert, (PrivateKey) key);
		} catch (KeyStoreException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getSubjectName(X509Certificate cert) {
		if(cert.getSubjectX500Principal() != null)
			return cert.getSubjectX500Principal().getName(X500Principal.RFC1779);
		else 
			return cert.getSubjectDN().getName();
	}
}
