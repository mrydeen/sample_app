package com.smartgridz.service;

import com.smartgridz.domain.ProductLicense;
import org.springframework.stereotype.Service;

/**
 * Provide access to the products licensing key and information.
 */
public interface LicenseService {

    /**
     * Get the current license object.
     */
    ProductLicense getProductLicense();
}
