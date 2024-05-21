package com.fairandsmart.generator.health;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.flywaydb.core.Flyway;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Liveness
@ApplicationScoped
public class FlywayHealthCheck implements HealthCheck {

    @Inject
    Flyway flyway;

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.up("db.version." + flyway.info().current().getVersion().toString());
    }
}
