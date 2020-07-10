package in.bloomington.incident.configuration;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import java.util.Properties;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Map;
import java.util.Collection;
// import java.util.EventListener;
import javax.servlet.http.HttpSessionEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
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
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.Authentication;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.jasig.cas.client.validation.TicketValidator;
import org.jasig.cas.client.validation.Cas30ServiceTicketValidator;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.springframework.stereotype.Component;

 
@Component
public class CasUserDetailsService implements AuthenticationUserDetailsService {

		@Override
		public UserDetails loadUserDetails(Authentication authentication) throws UsernameNotFoundException {
        CasAssertionAuthenticationToken casAssertionAuthenticationToken = (CasAssertionAuthenticationToken) authentication;
        AttributePrincipal principal = casAssertionAuthenticationToken.getAssertion().getPrincipal();
				try{
						Map attributes = principal.getAttributes();
						System.err.println(" prin attr "+attributes);						
						// String uname = (String) attributes.get("username");
						String email = (String) attributes.get("mail");
						String username = (String) attributes.get("sAMAccountName");
						String employeeId = (String) attributes.get("employeeId");
        // String username = authentication.getName();
						System.err.println(" *** ");
						System.err.println(" *** username "+username);
						System.err.println(" *** email "+email);
						System.err.println(" *** ");				
						Collection<SimpleGrantedAuthority> collection = new ArrayList<SimpleGrantedAuthority>();
						collection.add(new SimpleGrantedAuthority(email));
						collection.add(new SimpleGrantedAuthority(employeeId));				
						return new User(username, "", collection);
				}catch(Exception ex){
						System.err.println(" user exp "+ex);
				}
				return null;
    }
		
}
