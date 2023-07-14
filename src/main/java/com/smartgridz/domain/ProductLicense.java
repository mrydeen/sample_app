package com.smartgridz.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * This class contains all the information about the product license.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductLicense {

    /**
     * The node id that is associated with this license.  It should
     * match the UUID on this product node.
     */
    private String nodeId;

    /**
     * This is the product license key (UUID).  This will be used by the
     * company to track the key to the node Id
     */
    private String productLicenseKey;

    /**
     * The start date of this license.
     */
    private Date startDate;

    /**
     * The end date of this license.
     */
    private Date endDate;

    /**
     * This product has multiple features and are enabled via the license file.
     */
    /** ******************************************************************** */
    /**              FEATURES                                                */
    /** ******************************************************************** */
    private Boolean isITroopsEnabled = false;

    private Boolean isCplexLicensed = false;

    private Boolean isValidLicense = false;

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("Product License\n-------------------\n");
        sb.append("  Node/Product UUID  : " + nodeId).append("\n");
        sb.append("  Product License Key: " + productLicenseKey).append("\n");
        sb.append("  Start Date         : " + startDate.toString()).append("\n");
        sb.append("  End Date           : " + endDate.toString()).append("\n");
        sb.append("  ITroops Enabled    : " + isITroopsEnabled).append("\n");
        sb.append("  CPlex Enabled      : " + isCplexLicensed).append("\n");
        sb.append("  Is Valid License   : " + isValidLicense).append("\n");
        return sb.toString();
    }

    /**
     * Return the number of days left in the license.
     * @return number of days left
     */
    public Long getNumberOfDaysLeftInLicense() {
        Long numberOfDays = 0L;
       if (startDate != null && endDate != null) {
           long diffInMillies = Math.abs(startDate.getTime() - endDate.getTime());
           numberOfDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
       }
       return numberOfDays;
    }
}
