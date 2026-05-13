package org.example.e_market.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.e_market.entities.vendor.Vendor;
import org.example.e_market.exceptions.CustomBadRequestException;
import org.example.e_market.services.ProvisioningService;
import org.flywaydb.core.Flyway;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProvisioningServiceImpl implements ProvisioningService {

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    @Override
    public void provisionVendor(final Vendor vendor) {

        final String schemaName = "vendor_" + vendor.getId();

        try {
            log.info("Provisioning vendor: {}  (schema: {})", vendor.getBusinessName(), schemaName);

            //create schema
            createSchema(schemaName);
            log.info("Created schema {} successfully", schemaName);

            //run flyway migration for schema
            runVendorMigration(schemaName);

            //initialize default data
        } catch (Exception e) {

            try {
                dropSchema(schemaName);
            } catch (Exception ex) {
                log.error("Errpr dropping schema,", e);
                log.error("Error dropping schema, {}", schemaName);
            }

            throw new CustomBadRequestException("Failed to provision vendor");

        }
    }


    private void runVendorMigration(String schemaName) {
        log.info("Running vendor migration for schema: {}", schemaName);

        final Flyway vendorFlyway = Flyway.configure()
                .dataSource(dataSource)
                .schemas(schemaName)
                .locations("classpath:db/migration/vendor")
                .validateOnMigrate(true)
                .baselineOnMigrate(true)
                .cleanDisabled(true)
                .load();

        log.info("Vendor flyway migration started");
        vendorFlyway.migrate();
        log.info("Vendor flyway migration completed");

    }

    private void dropSchema(String schemaName) {
        final String sql = String.format("DROP SCHEMA IF EXISTS \"%s\"", schemaName);
        jdbcTemplate.execute(sql);
    }

    private void createSchema(String schemaName) {
        final String sql = String.format("CREATE SCHEMA IF NOT EXISTS \"%s\"", schemaName);
        jdbcTemplate.execute(sql);
    }
}
