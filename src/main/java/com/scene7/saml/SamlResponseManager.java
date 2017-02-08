package com.scene7.saml;

import java.util.Calendar;
import java.util.Set;

import com.onelogin.AccountSettings;
import com.onelogin.saml.Response;

public class SamlResponseManager {

    private static final SamlResponseManager instance = new SamlResponseManager();
    private static final CacheableClassDefinition cacheableDef = new CacheableClassDefinition();

    static {
        cacheableDef.setKeyClass(String.class);
        cacheableDef.setValueClass(ValidatedToken.class);
    }

    private SamlResponseManager() {
    }

    public static SamlResponseManager getInstance() {
        return instance;
    }

    public static CacheableClassDefinition getCacheableClassDefinition() {
        return cacheableDef;
    }

    public LoginUser getLoginUser(String base64SamlResponse) throws SamlException {
        return getLoginUser(buildResponse(base64SamlResponse), false);
    }

    public LoginUser getLoginUser(Response samlResponse) throws SamlException {
        return getLoginUser(samlResponse, false);
    }

    public LoginUser getApiUser(String base64SamlResponse) throws SamlException {
        return getLoginUser(buildResponse(base64SamlResponse), true);
    }

    private Response buildResponse(String base64SamlResponse) throws SamlException {
        try {
            AccountSettings accountSettings = new AccountSettings();
            accountSettings.setCertificate(ServletConfiguration.getProperty("okta.auth.cert"));

            Response samlResponse = new Response(accountSettings);
            samlResponse.loadXmlFromBase64(base64SamlResponse);
            samlResponse.parseResponse();
            samlResponse.setDestinationUrl(samlResponse.getParsedDestinationUrl());

            return samlResponse;
        } catch (SamlException se) {
            throw se;
        } catch (Exception e) {
            throw new SamlException(e);
        }
    }

    private LoginUser getLoginUser(Response samlResponse, boolean isApiRequest) throws SamlException {
        try {
            if (!samlResponse.validateResponse(!isApiRequest)) {
                throw new SamlException(new ResourceSubstitution("saml.response.error.invalid", new Object[] { samlResponse.getError() }));
            }

            if (isApiRequest) {
                // Check destination URL
                String destinationUrl = samlResponse.getParsedDestinationUrl();
                if (!StringOperations.isEmpty(destinationUrl)) {
                    if (!AuthUtils.isOktaDestinationAllowed(destinationUrl)) {
                        throw new SamlException(new ResourceSubstitution("saml.invalid.destination.url", new Object[] { destinationUrl }));
                    }
                }

                // Check audience URI
                Set<String> validAudiences = samlResponse.getParsedAudiences();
                if ((validAudiences != null) && !validAudiences.isEmpty()) {
                    boolean isValidAudience = false;
                    for (String currAudience : validAudiences) {
                        if (AuthUtils.isOktaAudienceAllowed(currAudience)) {
                            isValidAudience = true;
                            break;
                        }
                    }
                    if (!isValidAudience) {
                        throw new SamlException(new ResourceSubstitution("saml.invalid.audience.uri"));
                    }
                }

                // Check for session expiration
                Calendar sessionStart = samlResponse.getParsedIssueInstant();
                if (sessionStart != null) {
                    int timeoutMinutes = ServletConfiguration.getIntProperty("okta.session.timeout");
                    Calendar sessionEnd = (Calendar) sessionStart.clone();
                    sessionEnd.add(Calendar.MINUTE, timeoutMinutes);
                    Calendar now = Calendar.getInstance();
                    if (now.compareTo(sessionEnd) >= 0) {
                        throw new SamlException(new ResourceSubstitution("saml.error.session.timeout"));
                    }
                }
            }

            String email = samlResponse.getNameId();
            if (!AuthUtils.isOktaEmailAllowed(email)) {
                throw new SamlException(new ResourceSubstitution("saml.error.email.disallowed", new Object[] { email }));
            }

            LoginUserQuery userQuery = new LoginUserQuery();
            userQuery.setEmail(email);
            LoginUser user = LoginUserDAO.getInstance().load(userQuery);
            if (user == null) {
                throw new SamlException(new ResourceSubstitution("saml.user.load.error", new Object[] { email }));
            }

            if (!AuthUtils.isValidIpsAdminUser(user)) {
                throw new SamlException(new ResourceSubstitution("saml.user.not.ips.admin", new Object[] { email }));
            }

            return user;
        } catch (

                SamlException se)

        {
            throw se;
        } catch (

                Exception e)

        {
            throw new SamlException(e);
        }
    }

}
