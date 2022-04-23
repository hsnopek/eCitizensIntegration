package hr.hsnopek.ecitizensintegration.security.authenticationtokens;

import java.util.Collection;

import hr.hsnopek.ecitizensintegration.security.providers.JwtAuthenticationProvider;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8441647194432178255L;
	
	private final Object principal;
	private String jwtToken;
	private Collection<GrantedAuthority> authorities;

	/**
	 * Use this constructor to return fully authenticated token in {@link JwtAuthenticationProvider}.
	 *
	 * @param principal UserPrincipal object created from JWT
	 * @param authorities Authories parsed from JWT
	 */
    public JwtAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities){
        super(authorities);
		this.principal = principal;
		this.authorities = (Collection<GrantedAuthority>) authorities;
    }

	/**
	 * This constructor can be safely used by any code that wishes to create a
	 * <code>JwtAuthenticationToken</code>, as the {@link #isAuthenticated()}
	 * will return <code>false</code>.
	 * @param jwtToken JWT Bearer token
	 * 
	 */
    public JwtAuthenticationToken(String jwtToken){
    	super(null);
		this.principal = null;
        this.authorities = null;
		this.jwtToken = jwtToken;
        super.setDetails(null);
		setAuthenticated(false);
    }

    @Override
    public Object getPrincipal() {
		return this.jwtToken == null ? this.principal : jwtToken;
    }

	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		return super.getAuthorities();
	}

	@Override
	public String getName() {
		return super.getName();
	}

	@Override
	public boolean isAuthenticated() {
		return super.isAuthenticated();
	}

	@Override
	public Object getCredentials() {
		return null;
	}
	@Override
	public Object getDetails() {
		return super.getDetails();
	}
}