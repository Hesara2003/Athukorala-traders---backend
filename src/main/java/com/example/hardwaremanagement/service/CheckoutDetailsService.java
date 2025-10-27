package com.example.hardwaremanagement.service;

import com.example.hardwaremanagement.dto.CheckoutDetailsRequest;
import com.example.hardwaremanagement.dto.CheckoutDetailsResponse;
import com.example.hardwaremanagement.model.CheckoutDetails;
import com.example.hardwaremanagement.repository.CheckoutDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class CheckoutDetailsService {

    @Autowired
    private CheckoutDetailsRepository repository;

    public CheckoutDetailsResponse saveDetails(CheckoutDetailsRequest request) {
        CheckoutDetails details = resolveExistingRecord(request);
        boolean isNew = details.getId() == null;

        details.setCustomerId(request.getCustomerId());
        details.setShippingSameAsBilling(Boolean.TRUE.equals(request.getShippingSameAsBilling()));
        details.setBilling(mapBilling(request.getBilling()));
        details.setShipping(mapShipping(request.getShipping(), request.getBilling(), details.isShippingSameAsBilling()));

        LocalDateTime now = LocalDateTime.now();
        if (isNew) {
            details.setCreatedAt(now);
        }
        details.setUpdatedAt(now);

        CheckoutDetails saved = repository.save(details);
        return mapResponse(saved);
    }

    private CheckoutDetails resolveExistingRecord(CheckoutDetailsRequest request) {
        if (StringUtils.hasText(request.getId())) {
            return repository.findById(request.getId()).orElseGet(CheckoutDetails::new);
        }

        if (StringUtils.hasText(request.getCustomerId())) {
            return repository.findFirstByCustomerIdOrderByUpdatedAtDesc(request.getCustomerId())
                    .orElseGet(CheckoutDetails::new);
        }
        return new CheckoutDetails();
    }

    private CheckoutDetails.BillingInfo mapBilling(CheckoutDetailsRequest.BillingInfo source) {
        if (source == null) {
            return null;
        }
        CheckoutDetails.BillingInfo billing = new CheckoutDetails.BillingInfo();
        billing.setFirstName(emptyToNull(source.getFirstName()));
        billing.setLastName(emptyToNull(source.getLastName()));
        billing.setCompany(emptyToNull(source.getCompany()));
        billing.setEmail(emptyToNull(source.getEmail()));
        billing.setPhone(emptyToNull(source.getPhone()));
        billing.setAddress(emptyToNull(source.getAddress()));
        billing.setCity(emptyToNull(source.getCity()));
        billing.setPostal(emptyToNull(source.getPostal()));
        billing.setCountry(emptyToNull(source.getCountry()));
        return billing;
    }

    private CheckoutDetails.ShippingInfo mapShipping(CheckoutDetailsRequest.ShippingInfo source,
                                                      CheckoutDetailsRequest.BillingInfo billing,
                                                      boolean shippingSameAsBilling) {
        if (shippingSameAsBilling) {
            return deriveShippingFromBilling(billing);
        }
        if (source == null) {
            return null;
        }
        CheckoutDetails.ShippingInfo shipping = new CheckoutDetails.ShippingInfo();
        shipping.setContact(emptyToNull(source.getContact()));
        shipping.setPhone(emptyToNull(source.getPhone()));
        shipping.setAddress(emptyToNull(source.getAddress()));
        shipping.setCity(emptyToNull(source.getCity()));
        shipping.setPostal(emptyToNull(source.getPostal()));
        shipping.setCountry(emptyToNull(source.getCountry()));
        shipping.setInstructions(emptyToNull(source.getInstructions()));
        return shipping;
    }

    private CheckoutDetails.ShippingInfo deriveShippingFromBilling(CheckoutDetailsRequest.BillingInfo billing) {
        if (billing == null) {
            return null;
        }
        CheckoutDetails.ShippingInfo shipping = new CheckoutDetails.ShippingInfo();
        String firstName = emptyToNull(billing.getFirstName());
        String lastName = emptyToNull(billing.getLastName());
        String contact = null;
        if (firstName != null || lastName != null) {
            contact = String.format("%s %s",
                    firstName != null ? firstName : "",
                    lastName != null ? lastName : "").trim();
        }
        shipping.setContact(StringUtils.hasText(contact) ? contact : null);
        shipping.setPhone(emptyToNull(billing.getPhone()));
        shipping.setAddress(emptyToNull(billing.getAddress()));
        shipping.setCity(emptyToNull(billing.getCity()));
        shipping.setPostal(emptyToNull(billing.getPostal()));
        shipping.setCountry(emptyToNull(billing.getCountry()));
        shipping.setInstructions(null);
        return shipping;
    }

    private CheckoutDetailsResponse mapResponse(CheckoutDetails saved) {
        CheckoutDetailsRequest.BillingInfo billing = null;
        if (saved.getBilling() != null) {
            billing = new CheckoutDetailsRequest.BillingInfo();
            billing.setFirstName(saved.getBilling().getFirstName());
            billing.setLastName(saved.getBilling().getLastName());
            billing.setCompany(saved.getBilling().getCompany());
            billing.setEmail(saved.getBilling().getEmail());
            billing.setPhone(saved.getBilling().getPhone());
            billing.setAddress(saved.getBilling().getAddress());
            billing.setCity(saved.getBilling().getCity());
            billing.setPostal(saved.getBilling().getPostal());
            billing.setCountry(saved.getBilling().getCountry());
        }

        CheckoutDetailsRequest.ShippingInfo shipping = null;
        if (saved.getShipping() != null) {
            shipping = new CheckoutDetailsRequest.ShippingInfo();
            shipping.setContact(saved.getShipping().getContact());
            shipping.setPhone(saved.getShipping().getPhone());
            shipping.setAddress(saved.getShipping().getAddress());
            shipping.setCity(saved.getShipping().getCity());
            shipping.setPostal(saved.getShipping().getPostal());
            shipping.setCountry(saved.getShipping().getCountry());
            shipping.setInstructions(saved.getShipping().getInstructions());
        }

        return new CheckoutDetailsResponse(
                saved.getId(),
                saved.getCustomerId(),
                billing,
                shipping,
                saved.isShippingSameAsBilling(),
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
    }

    private String emptyToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
