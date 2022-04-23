package hr.hsnopek.ecitizensintegration.domain.feature.authorization.model;

import java.util.List;

public class RegistrationFormSubmitParams {
    private String serviceRequestId;

    private String fromEntityFirstName;
    private String fromEntityLastName;
    private String fromEntityOib;
    private String fromEntityLegalName;

    private String forEntityIps;
    private String forEntityLegalName;

    private String toEntityFirstName;
    private String toEntityLastName;
    private String toEntityOib;
    private String toEntityLegalName;
    private String responseUrl;
    private String cancelUrl;
    private List<SimplePermissionType> permissions;

    public RegistrationFormSubmitParams() {
    }

    public String getServiceRequestId() {
        return serviceRequestId;
    }

    public void setServiceRequestId(String serviceRequestId) {
        this.serviceRequestId = serviceRequestId;
    }

    public String getFromEntityFirstName() {
        return fromEntityFirstName;
    }

    public void setFromEntityFirstName(String fromEntityFirstName) {
        this.fromEntityFirstName = fromEntityFirstName;
    }

    public String getFromEntityLastName() {
        return fromEntityLastName;
    }

    public void setFromEntityLastName(String fromEntityLastName) {
        this.fromEntityLastName = fromEntityLastName;
    }

    public String getFromEntityOib() {
        return fromEntityOib;
    }

    public void setFromEntityOib(String fromEntityOib) {
        this.fromEntityOib = fromEntityOib;
    }

    public String getFromEntityLegalName() {
        return fromEntityLegalName;
    }

    public void setFromEntityLegalName(String fromEntityLegalName) {
        this.fromEntityLegalName = fromEntityLegalName;
    }

    public String getForEntityIps() {
        return forEntityIps;
    }

    public void setForEntityIps(String forEntityIps) {
        this.forEntityIps = forEntityIps;
    }

    public String getForEntityLegalName() {
        return forEntityLegalName;
    }

    public void setForEntityLegalName(String forEntityLegalName) {
        this.forEntityLegalName = forEntityLegalName;
    }

    public String getToEntityFirstName() {
        return toEntityFirstName;
    }

    public void setToEntityFirstName(String toEntityFirstName) {
        this.toEntityFirstName = toEntityFirstName;
    }

    public String getToEntityLastName() {
        return toEntityLastName;
    }

    public void setToEntityLastName(String toEntityLastName) {
        this.toEntityLastName = toEntityLastName;
    }

    public String getToEntityOib() {
        return toEntityOib;
    }

    public void setToEntityOib(String toEntityOib) {
        this.toEntityOib = toEntityOib;
    }

    public String getToEntityLegalName() {
        return toEntityLegalName;
    }

    public void setToEntityLegalName(String toEntityLegalName) {
        this.toEntityLegalName = toEntityLegalName;
    }

    public String getResponseUrl() {
        return responseUrl;
    }

    public void setResponseUrl(String responseUrl) {
        this.responseUrl = responseUrl;
    }

    public String getCancelUrl() {
        return cancelUrl;
    }

    public void setCancelUrl(String cancelUrl) {
        this.cancelUrl = cancelUrl;
    }

    public List<SimplePermissionType> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<SimplePermissionType> permissions) {
        this.permissions = permissions;
    }
}
