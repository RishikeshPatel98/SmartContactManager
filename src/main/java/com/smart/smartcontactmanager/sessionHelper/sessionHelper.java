package com.smart.smartcontactmanager.sessionHelper;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class sessionHelper {
    public void removeMessageFromSession() {
        try {
            System.err.println("removing message from session");
            HttpSession session = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
            session.removeAttribute("message");
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
}

