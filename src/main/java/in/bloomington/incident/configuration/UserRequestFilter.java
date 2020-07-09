package in.bloomington.incident.configuration;

import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.context.SecurityContextHolder;
import org.jasig.cas.client.authentication.AttributePrincipal;
import in.bloomington.incident.model.User;

@Component
@Order(-2147483648)
public class UserRequestFilter implements Filter {

	@Override
	public void destroy() {
		// ...
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		//
	}

	@Override
	public void doFilter(ServletRequest request, 
											 ServletResponse response,
											 FilterChain chain)
			throws IOException, ServletException {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if(auth != null){
					String username = auth.getName();
					Object credentials = auth.getCredentials();
					System.err.println(" *** from sec username "+username);					
			}
			else{
					System.err.println(" *** auth is null ");					
			}
			HttpServletRequest req = (HttpServletRequest) request;
			Enumeration<String> parameterNames = req.getParameterNames();
			while (parameterNames.hasMoreElements()) {
 					String paramName = parameterNames.nextElement();
					String[] paramValues = req.getParameterValues(paramName);
					for (int i = 0; i < paramValues.length; i++) {
							String paramValue = paramValues[i];
							System.err.println(paramName+" : "+paramValue);
					}
			}
			String uname = req.getParameter("username");
			System.err.println(" ** req username "+uname);
			HttpSession session = req.getSession(false);
			String uri = req.getRequestURI();
			System.err.println(" *** in filter "+uri);
			try{
					if(uri.indexOf(".js") > -1 ||
						 uri.indexOf(".jpg") > -1 ||						 
						 uri.indexOf(".css") > -1){
							chain.doFilter(request, response);
					}
					else if(uri.indexOf("login") > -1){
							Enumeration<String> headerNames = req.getHeaderNames();
							if (headerNames != null) {
									while (headerNames.hasMoreElements()) {
											System.out.println(" *** in filter Header: " + req.getHeader(headerNames.nextElement()));
									}
							}							
							// AttributePrincipal principal = null;
							Object principal = req.getUserPrincipal();
							String remoteUser = req.getRemoteUser();
							System.err.println(" ** remote "+remoteUser);
							Authentication authentication = null;
							if (principal instanceof Authentication) {
									authentication = (Authentication) req.getUserPrincipal();
									Object user = authentication.getPrincipal();
									System.err.println(" ** user in filter "+user.toString());
							}
							else if(principal instanceof AttributePrincipal){
									String username = ((AttributePrincipal)principal).getName();
									System.err.println(" **** in filter username "+username);
							}
							else if(principal == null){
									System.err.println(" **** principal is null ");
							}
							chain.doFilter(request, response);
					}
					else if(session != null){
							User user = (User)session.getAttribute("user");
							if(user != null){
									chain.doFilter(request, response);
							}
							else{
									request.getRequestDispatcher("/templates/welcome.html")
											.forward(request, response);
							}
					}
					else{
							chain.doFilter(request, response);
					}
			} catch (Exception ex) {
					request.setAttribute("message", ex);
					System.err.println(" *** user filter "+ex);
					request.getRequestDispatcher("/welcome.html")
							.forward(request, response);
			}
			
	}

}
