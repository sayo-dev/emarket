package org.example.e_market.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SchemaResolver {

    private final JdbcTemplate jdbcTemplate;

    private static final String PUBLIC_SCHEMA = "public";

    @Cacheable(value = "vendorSchema", key = "#vendorId")
    public String resolveSchema(final String vendorId) {

        if (vendorId == null) {
            return PUBLIC_SCHEMA;
        }
        try {
            final String schemaName = "vendor_" + vendorId;
            log.debug("Vendor schema resolved {} for vendor {}", schemaName, vendorId);
            return schemaName;

        } catch (Exception e) {
            log.warn("Schema not found for vendor {}, using public schema", vendorId);
            return PUBLIC_SCHEMA;
        }

    }

}
