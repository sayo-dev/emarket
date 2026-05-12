package org.example.e_market.config;

import org.apache.logging.log4j.ThreadContext;

public class VendorContext {

    private static final ThreadLocal<String> CURRENT_VENDOR = new ThreadLocal<>();
    private static final ThreadLocal<String> CURRENT_SCHEMA = new ThreadLocal<>();

    public static void setVendor(String vendorId) {
        CURRENT_VENDOR.set(vendorId);
    }

    public static String getVendor() {
        return CURRENT_VENDOR.get();
    }

    public static void setSchema(String schema) {
        CURRENT_SCHEMA.set(schema);
    }

    public static String getSchema() {
        return CURRENT_SCHEMA.get();
    }

    public static void clear() {
        CURRENT_VENDOR.remove();
        CURRENT_SCHEMA.remove();
    }
}
