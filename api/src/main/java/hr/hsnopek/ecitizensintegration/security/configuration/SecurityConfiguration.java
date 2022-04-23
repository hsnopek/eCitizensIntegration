package hr.hsnopek.ecitizensintegration.security.configuration;

import hr.hsnopek.ecitizensintegration.security.entrypoints.JwtAuthenticationEntryPoint;
import hr.hsnopek.ecitizensintegration.security.entrypoints.SimpleAccessDeniedHandler;
import hr.hsnopek.ecitizensintegration.security.filter.JWTAuthenticationFilter;
import hr.hsnopek.ecitizensintegration.security.providers.JwtAuthenticationProvider;
import hr.hsnopek.ecitizensintegration.security.util.JWTTokenUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.ExceptionTranslationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final JWTTokenUtil jwtTokenUtil;

    public SecurityConfiguration(JWTTokenUtil jwtTokenUtil1) {
        this.jwtTokenUtil = jwtTokenUtil1;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(jwtAuthenticationProvider());
    }

    @Bean
    public JwtAuthenticationProvider jwtAuthenticationProvider() {
        return new JwtAuthenticationProvider(jwtTokenUtil);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {

        final String[] SWAGGER_WHITELIST = {
                "/v2/api-docs",
                "/v3/api-docs",
                "/swagger-resources/**",
                "/swagger-ui/**",
        };

        final String[] STATIC_RESOURCES_WHITELIST = {
                "/static/**",
                "**.ico",
                "**.json"
        };

        web.ignoring()
                .antMatchers(SWAGGER_WHITELIST)
                .antMatchers("/nias/sign-in", "/nias/receive-authn-response")
                .antMatchers("/auth/**") // refresh token routes
                .antMatchers("/authorization/**", "/redirect-user**", "/auth/refresh-token/**")
                .antMatchers("/h2-console/**")
                .antMatchers("/okp/**")
                .antMatchers(STATIC_RESOURCES_WHITELIST);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .exceptionHandling()
                .accessDeniedHandler(new SimpleAccessDeniedHandler())
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/").permitAll()
                .antMatchers("/nias/**", "/h2-console/**","/authorization/**", "/redirect-user**").permitAll()
                .antMatchers("/user").hasRole("USER")
                .anyRequest().authenticated()
		        .and()
                .addFilterAfter(new JWTAuthenticationFilter(jwtTokenUtil, authenticationManagerBean()), ExceptionTranslationFilter.class);

        http.headers().frameOptions().disable();

    }


}
