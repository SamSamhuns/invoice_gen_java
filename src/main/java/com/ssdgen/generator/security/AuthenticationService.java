package com.ssdgen.generator.security;

import io.quarkus.security.identity.SecurityIdentity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.security.Principal;

@RequestScoped
public class AuthenticationService {

    public static final String UNAUTHENTIFIED_IDENTIFIER = "anonymous";
    public static final String SUPERUSER_ROLE = "sudoers";

    @Inject
    SecurityIdentity securityIdentity;

    public String getConnectedUser() {
        Principal user = securityIdentity.getPrincipal();
        String name = user != null ? user.getName() : UNAUTHENTIFIED_IDENTIFIER;
        return name;
    }

    public boolean isConnectedUserInRole(String role) {
        return securityIdentity.hasRole(role);
    }

    public boolean isSuperUserConnected() {
        Principal user = securityIdentity.getPrincipal();
        return user != null && securityIdentity.hasRole(SUPERUSER_ROLE);
    }

}
