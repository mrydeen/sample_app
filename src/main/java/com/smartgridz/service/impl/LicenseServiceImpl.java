package com.smartgridz.service.impl;

import com.fasterxml.jackson.databind.node.POJONode;
import com.smartgridz.config.SystemDefaults;
import com.smartgridz.config.SystemInfoService;
import com.smartgridz.domain.ProductLicense;
import com.smartgridz.service.EmailService;
import com.smartgridz.service.LicenseService;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Service
public class LicenseServiceImpl implements LicenseService {

    private static final Logger LOG = LoggerFactory.getLogger(LicenseServiceImpl.class);

    private static ProductLicense PRODUCT_LICENSE;

    private static SystemInfoService systemInfoService;

    public LicenseServiceImpl(SystemInfoService systemInfoService) {
        this.systemInfoService = systemInfoService;
    }

    /**
     * This is just temp to create a product license when the system starts up.
     * @return
     */
    @PostConstruct
    public void detectProductLicense() {
        // Need to make sure that the public key and the license file exist
        // in the required directories.
        File licenseFile = new File(SystemDefaults.PRODUCT_LICENSE);
        if (!licenseFile.exists()) {
            LOG.error("License file does not exist, creating a default one");
            createDevProductLicense();
            LOG.error(PRODUCT_LICENSE.toString());
            return;
        }

        // There is a license file, is the public key there?
        File publicKeyFile = new File(SystemDefaults.PUBLIC_KEY);
        if (!publicKeyFile.exists()) {
            LOG.error("Public Key is not in required directory");
            // So we cannot read the license that is in the file since we
            // do not have a public key so create a cripple licensed
            createCrippledLicense();
            LOG.info(PRODUCT_LICENSE.toString());
            LOG.error("Creating Crippled License");
            return;
        }

        // So now we can decode the license file so get the public key
        PublicKey publicKey;
        try {
            publicKey = getPublicKey();
        } catch (Exception e) {
            LOG.error("Failed to retrieve the public key", e);
            createCrippledLicense();
            return;
        }

        // Now we can extract the license file data with the public key
        decodeLicenseKey(publicKey);

        // Now do some checking up front.
        //  o - if the node id does not match ours, then need to invalidate
        //      this license.
        if (!systemInfoService.getNodeUuid().equals(PRODUCT_LICENSE.getNodeId())) {
            // They do not match so invalidate the license.
            // Don't dump out the node information as they could copy and paste
            // it into the uuid location.
            LOG.error("The provided license is not for this node.");
            createCrippledLicense();
        }
    }

    /**
     * Using the public key, decide the license file and extract the
     * license information from it.
     *
     * @param publicKey to use to decode.
     */
    private void decodeLicenseKey(PublicKey publicKey) {
        try {
            // Need to read in the AES secret key which is the first part of the file.
            FileInputStream licenseFile = new FileInputStream(SystemDefaults.PRODUCT_LICENSE);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            byte[] encryptedBytes = new byte[256];
            licenseFile.read(encryptedBytes);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            SecretKeySpec secretKey = new SecretKeySpec(decryptedBytes, "AES");

            // Next in the file is the Initialization vector used to encrypt the actual
            // licensed data.
            byte[] iv = new byte[128 / 8];
            licenseFile.read(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // Next is the actual license information.
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            ByteArrayOutputStream rawLicense = new ByteArrayOutputStream();
            processFile(cipher, licenseFile, rawLicense);
            String licenseData = rawLicense.toString();

            JSONParser parser = new JSONParser();
            JSONObject license = (JSONObject) parser.parse(licenseData);

            // Now extract the information.
            PRODUCT_LICENSE = new ProductLicense();
            // TODO: make these strings contants
            PRODUCT_LICENSE.setNodeId((String) license.get("node_uuid"));
            PRODUCT_LICENSE.setProductLicenseKey((String) license.get("product_license_key"));

            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            PRODUCT_LICENSE.setStartDate(isoFormat.parse((String) license.get("start_date")));
            PRODUCT_LICENSE.setEndDate(isoFormat.parse((String) license.get("end_date")));

            // Now see which features are enabled.
            JSONObject features = (JSONObject) license.get("features");
            if (features.containsKey("cplex_licensed")) {
                PRODUCT_LICENSE.setIsCplexLicensed(true);
            }
            PRODUCT_LICENSE.setIsValidLicense(true);
        } catch (Exception e) {
            LOG.error("Failed to decode License Key", e);
            createCrippledLicense();
        }
    }

    private void processFile(Cipher ci, InputStream in, OutputStream out)
              throws IOException, IllegalBlockSizeException, BadPaddingException {
        byte[] ibuf = new byte[1024];
        int len;
        while ((len = in.read(ibuf)) != -1) {
            byte[] obuf = ci.update(ibuf, 0, len);
            if ( obuf != null ) out.write(obuf);
        }
        byte[] obuf = ci.doFinal();
        if ( obuf != null ) out.write(obuf);
    }

    /**
     * This method will decode the license key based on the public key
     * @return the public key.
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws IOException
     */
    private PublicKey getPublicKey()
            throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(SystemDefaults.PRODUCT_LICENSE));
        X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(ks);
    }

    /**
     * This will create a license that is only 2 days enabled and
     * CPLEX is not enabled.  Issue is that if the application is restarted
     * then it will reset back to 2 days.
     *
     * TODO: revisit
     */
    private void createCrippledLicense() {
        PRODUCT_LICENSE = new ProductLicense();
        PRODUCT_LICENSE.setStartDate(new Date());
        // Add 7 days to the license
        Calendar cal = Calendar.getInstance();
        cal.setTime(PRODUCT_LICENSE.getStartDate());
        cal.add(Calendar.DAY_OF_MONTH, 2);
        PRODUCT_LICENSE.setEndDate(cal.getTime());
    }

    /**
     * This should never be called in production, we need to disable this
     * somehow:
     * TODO: Disable this in production.
     */
    private void createDevProductLicense() {
        PRODUCT_LICENSE = new ProductLicense();
        PRODUCT_LICENSE.setStartDate(new Date());
        // Add 30 days to the license
        Calendar cal = Calendar.getInstance();
        cal.setTime(PRODUCT_LICENSE.getStartDate());
        cal.add(Calendar.MONTH, 1);
        PRODUCT_LICENSE.setEndDate(cal.getTime());

        // Setup Features.
        PRODUCT_LICENSE.setIsCplexLicensed(true);
        PRODUCT_LICENSE.setIsValidLicense(true);
    }

    /**
     * Return the product license information.
     */
    public ProductLicense getProductLicense() {
        return PRODUCT_LICENSE;
    }
}
