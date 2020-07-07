package in.bloomington.incident.configuration;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import java.util.Properties;
import java.util.Arrays;
// import java.util.EventListener;
import javax.servlet.http.HttpSessionEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.context.annotation.Primary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.context.event.EventListener;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.authentication.AuthenticationProvider;

import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.authority.AuthorityUtils;
import org.jasig.cas.client.validation.TicketValidator;
// import org.jasig.cas.client.validation.Cas30ServiceTicketValidator;
import org.jasig.cas.client.validation.Cas30ServiceTicketValidator;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.springframework.stereotype.Component;

 
@Component
public class CasComponent{

		
    private Environment env;
		
    @Value("${cas.login.url}") // https://bloomington.in.gov/cas/login
    private String casLoginUrl;
		
    @Value("${cas.logout.url}") // https://bloomington.in.gov/cas/logout
    private String casLogoutUrl;
		
    @Value("${cas.url}") // https://bloomington.in.gov/cas
    private String casUrl;
		
		@Value("${server.servlet.context-path}")
    private String hostPath;
		
		@Value("${app.server.url}") // https://outlaw.bloomington.in.gov		
    private String serverUrl;
		
		@Autowired
		CasUserDetailsService casUserDetailService;
		
		@Bean
		public CasAuthenticationFilter casAuthenticationFilter(ServiceProperties sP)
				throws Exception {
				CasAuthenticationFilter filter = new CasAuthenticationFilter();
				filter.setServiceProperties(sP);
				filter.setAuthenticationManager(authenticationManager());
				return filter;
		}
		
		
		// @Override
		@Bean
		protected AuthenticationManager authenticationManager() throws Exception {
				return new ProviderManager(
																	 Arrays.asList(authenticationProvider()));
		}
		//
		// for logout
		//
		@Bean
		public SecurityContextLogoutHandler securityContextLogoutHandler() {
				return new SecurityContextLogoutHandler();
		}

		@Bean
		public LogoutFilter logoutFilter() {
				LogoutFilter logoutFilter = new LogoutFilter(casLoginUrl,
																										 securityContextLogoutHandler());
				logoutFilter.setFilterProcessesUrl("/logout");
				return logoutFilter;
		}
		@Bean
		public SingleSignOutFilter singleSignOutFilter() {
				SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
				// singleSignOutFilter.setCasServerUrlPrefix("https://localhost:6443/cas");
				singleSignOutFilter.setIgnoreInitConfiguration(true);
				return singleSignOutFilter;
		}
		@EventListener
		public SingleSignOutHttpSessionListener
				singleSignOutHttpSessionListener(HttpSessionEvent event) {
				return new SingleSignOutHttpSessionListener();
		}
		
		@Bean
		public ServiceProperties serviceProperties() {
				ServiceProperties serviceProperties = new ServiceProperties();
				// serviceProperties.setService("http://localhost:9000/login/cas");
				// String url = serverUrl+hostPath+"login";
				// System.err.println(" usrl "+url);
				serviceProperties.setService(serverUrl+hostPath+"/login");
				serviceProperties.setSendRenew(false);
				return serviceProperties;
		}
		@Bean
		@Primary
		public AuthenticationEntryPoint authenticationEntryPoint(
																														 ServiceProperties sP) {
 
				CasAuthenticationEntryPoint entryPoint
						= new CasAuthenticationEntryPoint();
				entryPoint.setLoginUrl(casLoginUrl);
				entryPoint.setServiceProperties(sP);
				return entryPoint;
		}
		@Bean
		public TicketValidator ticketValidator() {
				return new Cas30ServiceTicketValidator(casUrl);
		}
		@Bean
		public CasAuthenticationProvider authenticationProvider() {
				// public CasAuthenticationProvider casAuthenticationProvider() { 
				CasAuthenticationProvider provider = new CasAuthenticationProvider();
				provider.setServiceProperties(serviceProperties());
				provider.setTicketValidator(ticketValidator());
				provider.setAuthenticationUserDetailsService(casUserDetailService);
				/**
				provider.setUserDetailsService(s ->
																			 new User("username", "",
																								true, true, true, true,
																								AuthorityUtils.createAuthorityList("ROLE_USER")));
				*/
				provider.setKey("CAS_PROVIDER_FOR_INCIDENTS");
				return provider;
		}		

}
