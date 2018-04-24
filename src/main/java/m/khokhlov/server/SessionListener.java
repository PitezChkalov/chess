package m.khokhlov.server;

import ca.watier.echesscommon.sessions.Player;
import ca.watier.echesscommon.utils.Constants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;


@Component
public class SessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
        HttpSession session = httpSessionEvent.getSession();
        session.setAttribute(Constants.PLAYER, new Player());
    }


    //Methods should not be empty
    @java.lang.SuppressWarnings("squid:S1186")
    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
    }
}
