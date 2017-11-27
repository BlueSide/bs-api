package nl.blueside.sp_api;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http .HttpSession;
import com.microsoft.aad.adal4j.AuthenticationException;

public class AuthInterceptor extends HandlerInterceptorAdapter
{

    public AuthInterceptor()
    {
        super();        
    }

    /**
     * Executed before actual handler is executed
     **/
    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
        String authString = request.getHeader("Authorization");
        String targetSite = request.getHeader(Settings.TARGET_SITE);
        String targetRoot = request.getHeader(Settings.TARGET_ROOT);

        if(authString == null || authString.isEmpty())
        {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "The Authorization header is missing");
            return false;
        }
        
        if(targetSite == null || targetSite.isEmpty())
        {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "The " + Settings.TARGET_SITE + "header is missing.");
            return false;
        }
        
        if(targetRoot == null || targetRoot.isEmpty())
        {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "The " + Settings.TARGET_ROOT + "header is missing.");
            return false;
        }
        
        BasicAuthorization authCredentials = new BasicAuthorization(authString);

        // Authenticate
        try
        {
            SPContext context = SPContext.registerCredentials(targetRoot, targetSite, Settings.applicationId,
                                                authCredentials.username, authCredentials.password);
        
            HttpSession session = request.getSession();
            session.setAttribute(Settings.SP_CONTEXT, context);
        }
        catch(AuthenticationException ae)
        {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, ae.getMessage());
            return false;
        }
        catch(Exception e)
        {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            return false;
        }

        return true;
    }
}
