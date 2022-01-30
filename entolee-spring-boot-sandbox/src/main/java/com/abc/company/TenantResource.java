package com.abc.company;

import java.util.UUID;

public interface TenantResource {

    UUID getId();

    UUID getTenantId();

    String getName();
}
