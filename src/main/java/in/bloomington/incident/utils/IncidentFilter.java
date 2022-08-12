package in.bloomington.incident.utils;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class IncidentFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) 
        throws IOException, ServletException {
	HttpServletResponse response = (HttpServletResponse) resp;
	response.addHeader("X-Frame-Options", "DENY");    
	chain.doFilter(req, resp);
    }
    @Override
    public void destroy() {

    }    
    @Override
    public void init(FilterConfig arg0) throws ServletException {

    }
    
}
