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
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.authority.AuthorityUtils;
import org.jasig.cas.client.validation.TicketValidator;

 
@Configuration
// @EnableWebSecurity
public class CasConfig extends WebSecurityConfigurerAdapter {

		@Autowired
		AuthenticationEntryPoint authenticationEntryPoint;
		@Autowired
		CasAuthenticationProvider authenticationProvider;
		@Autowired
		CasAuthenticationFilter casAuthenticationFilter;
		/**
		@Autowired
		SingleSignOutFilter singleSignOutFilter;
		@Autowired
		LogoutFilter logoutFilter;
		*/
		/**
		public CasConfig(){
				super(true); // to disable the defaults such as anonymous user
		}
		*/
		@Override
		protected void configure(HttpSecurity http) throws Exception {

				http
						.csrf().disable()
						.addFilter(casAuthenticationFilter)
						.anonymous().disable()						
						.authorizeRequests()
						.regexMatchers("/login") // ("/login", "/staff")
						.authenticated()
						.and()
						.authorizeRequests()
						// .regexMatchers("/")
						// .permitAll()
						.and()
						.httpBasic()
						.authenticationEntryPoint(authenticationEntryPoint);
				/*
						.and()
						.logout().logoutSuccessUrl("/logout")
						.and()
						.addFilterBefore(singleSignOutFilter, CasAuthenticationFilter.class)
						.addFilterBefore(logoutFilter, LogoutFilter.class);
				*/
				/*
				http.authorizeRequests()
						.antMatchers( "/login","/staff*") 
						.authenticated() 
						.and()
						.exceptionHandling() 
						.authenticationEntryPoint(authenticationEntryPoint)
						.and()
						.authorizeRequests()
						.antMatchers("/")
						.permitAll()
						.and()
						.httpBasic()
						// .authenticationEntryPoint(authenticationEntryPoint)
						// .and()
						.logout().logoutSuccessUrl("/logout")
						.and()
						.addFilterBefore(singleSignOutFilter, CasAuthenticationFilter.class)
						.addFilterBefore(logoutFilter, LogoutFilter.class);
				*/
		}
		@Override
		protected void configure(AuthenticationManagerBuilder auth)
				throws Exception {
				auth.authenticationProvider(authenticationProvider);
		}

		@Override
		protected AuthenticationManager authenticationManager() throws Exception {
				return new ProviderManager(
																	 Arrays.asList(authenticationProvider));
		}

}
