package hr.hsnopek.ecitizensintegration.controller;

import authorizationrequestv3.ServiceRequestType;
import hr.hsnopek.ecitizensintegration.domain.feature.authorization.facade.AuthorizationFacade;
import hr.hsnopek.ecitizensintegration.domain.feature.authorization.service.AuthorizationService;
import hr.hsnopek.ecitizensintegration.configuration.ApplicationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;

@Controller
@RequestMapping("/authorization")
public class AuthorizationController {

    private final AuthorizationFacade authorizationFacade;
    private final AuthorizationService authorizationService;

    public AuthorizationController(AuthorizationFacade authorizationFacade, AuthorizationService authorizationService) {
        this.authorizationFacade = authorizationFacade;
        this.authorizationService = authorizationService;
    }

    @PostMapping(path="/getFormData")
    public ModelAndView getRegistrationForm(@RequestParam("ServiceRequest") String b64Param,
                                            @RequestParam("ResponseUrl") String responseUrl,
                                            @RequestParam("CancelUrl") String cancelUrl)
            throws IOException {

        // create service request
        ServiceRequestType serviceRequest = authorizationService.createServiceRequest(b64Param);

        // map data model for frontend to read
        String b64FormParams = authorizationService
                .createServiceRequestFormSubmitFrontendParams(responseUrl, cancelUrl, serviceRequest);

        // redirect request to frontend to process instead of thymeleaf
        String redirectUrl = String.format("%s/registration-form-entry", ApplicationProperties.FRONTEND_URL);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("submitForm");
        modelAndView.addObject("b64FormParams", b64FormParams);
        modelAndView.addObject("location", redirectUrl);
        return modelAndView;
    }

    @PostMapping("/submit")
    public ModelAndView submitRegistrationForm(
            @RequestParam("serviceRequestId") String serviceRequestId,
            @RequestParam("responseUrl") String responseUrl,
            @RequestParam("b64permissions") String b64permissions) throws DatatypeConfigurationException, IOException {

        // read permissions that were sent by frontend user and create service response for authorization service
        String b64ServiceResponse = authorizationService.createServiceResponse(serviceRequestId, b64permissions);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("location", responseUrl);
        modelAndView.addObject("serviceResponse", b64ServiceResponse);
        modelAndView.setViewName("submitResponse");

        return modelAndView;
    }

    @GetMapping("/actAs")
    public void actAs(@RequestParam(value = "ForPersonOib", required = false) String forPersonOib,
                      @RequestParam(value = "ForLegalIps", required = false) String forLegalIps,
                      @RequestParam(value = "ForLegalIzvor_reg", required = false) Integer forLegalIzvor,
                      @RequestParam(value = "ToLegalIps", required = false) String toLegalIps,
                      @RequestParam(value = "ToLegalIzvor_reg", required = false) Integer toLegalIzvor,
                      @RequestParam(value = "ident", required = true) String ident,
                      HttpServletRequest httpServletRequest,
                      HttpServletResponse httpServletResponse) throws Exception {

        // send request to authorization service with parameters sent by navigation bar
        // map results to new access token and set it in cookie
        authorizationFacade.actAsEntity(forPersonOib, forLegalIps, forLegalIzvor, toLegalIps, toLegalIzvor, ident,
                httpServletRequest, httpServletResponse);
    }
}
