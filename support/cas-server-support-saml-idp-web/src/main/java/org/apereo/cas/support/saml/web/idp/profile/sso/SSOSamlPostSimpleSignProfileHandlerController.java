package org.apereo.cas.support.saml.web.idp.profile.sso;

import org.apereo.cas.authentication.AuthenticationSystemSupport;
import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.authentication.principal.ServiceFactory;
import org.apereo.cas.authentication.principal.WebApplicationService;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.support.saml.OpenSamlConfigBean;
import org.apereo.cas.support.saml.SamlIdPConstants;
import org.apereo.cas.support.saml.services.idp.metadata.cache.SamlRegisteredServiceCachingMetadataResolver;
import org.apereo.cas.support.saml.web.idp.profile.AbstractSamlProfileHandlerController;
import org.apereo.cas.support.saml.web.idp.profile.builders.SamlProfileObjectBuilder;
import org.apereo.cas.support.saml.web.idp.profile.builders.enc.SamlIdPObjectSigner;
import org.apereo.cas.support.saml.web.idp.profile.builders.enc.validate.SamlObjectSignatureValidator;
import org.apereo.cas.support.saml.web.idp.profile.sso.request.SSOSamlHttpRequestExtractor;

import lombok.val;
import org.opensaml.messaging.decoder.servlet.BaseHttpServletRequestXMLMessageDecoder;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Response;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * The {@link SSOSamlPostSimpleSignProfileHandlerController} is responsible for
 * handling profile requests for SAML2 Web SSO SimpleSign.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
public class SSOSamlPostSimpleSignProfileHandlerController extends AbstractSamlProfileHandlerController {
    private final SSOSamlHttpRequestExtractor samlHttpRequestExtractor;
    private final Map<HttpMethod, BaseHttpServletRequestXMLMessageDecoder> samlMessageDecoders;

    public SSOSamlPostSimpleSignProfileHandlerController(final SamlIdPObjectSigner samlObjectSigner,
                                                         final AuthenticationSystemSupport authenticationSystemSupport,
                                                         final ServicesManager servicesManager,
                                                         final ServiceFactory<WebApplicationService> webApplicationServiceFactory,
                                                         final SamlRegisteredServiceCachingMetadataResolver samlRegisteredServiceCachingMetadataResolver,
                                                         final OpenSamlConfigBean configBean,
                                                         final SamlProfileObjectBuilder<Response> responseBuilder,
                                                         final CasConfigurationProperties casProperties,
                                                         final SamlObjectSignatureValidator samlObjectSignatureValidator,
                                                         final SSOSamlHttpRequestExtractor samlHttpRequestExtractor,
                                                         final Service callbackService,
                                                         final Map<HttpMethod, BaseHttpServletRequestXMLMessageDecoder> samlMessageDecoders) {
        super(samlObjectSigner,
            authenticationSystemSupport,
            servicesManager,
            webApplicationServiceFactory,
            samlRegisteredServiceCachingMetadataResolver,
            configBean,
            responseBuilder,
            casProperties,
            samlObjectSignatureValidator,
            callbackService);
        this.samlHttpRequestExtractor = samlHttpRequestExtractor;
        this.samlMessageDecoders = samlMessageDecoders;
    }

    /**
     * Handle SSO POST profile request.
     *
     * @param response the response
     * @param request  the request
     * @throws Exception the exception
     */
    @GetMapping(path = SamlIdPConstants.ENDPOINT_SAML2_SSO_PROFILE_POST_SIMPLE_SIGN)
    protected void handleSaml2ProfileSsoRedirectRequest(final HttpServletResponse response,
                                                        final HttpServletRequest request) throws Exception {
        val decoder = this.samlMessageDecoders.get(HttpMethod.GET);
        handleSsoPostProfileRequest(response, request, decoder);
    }

    /**
     * Handle SSO POST profile request.
     *
     * @param response the response
     * @param request  the request
     * @throws Exception the exception
     */
    @PostMapping(path = SamlIdPConstants.ENDPOINT_SAML2_SSO_PROFILE_POST_SIMPLE_SIGN)
    protected void handleSaml2ProfileSsoPostRequest(final HttpServletResponse response,
                                                    final HttpServletRequest request) throws Exception {
        val decoder = this.samlMessageDecoders.get(HttpMethod.POST);
        handleSsoPostProfileRequest(response, request, decoder);
    }

    /**
     * Handle profile request.
     *
     * @param response the response
     * @param request  the request
     * @param decoder  the decoder
     * @throws Exception the exception
     */
    protected void handleSsoPostProfileRequest(final HttpServletResponse response,
                                               final HttpServletRequest request,
                                               final BaseHttpServletRequestXMLMessageDecoder decoder) throws Exception {
        val authnRequest = this.samlHttpRequestExtractor.extract(request, decoder, AuthnRequest.class);
        initiateAuthenticationRequest(authnRequest, response, request);
    }

}
