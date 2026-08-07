package com.zcunsoft.clklog.sysmgmt.cfg;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ConfigurationProperties(prefix = "clklog.sysmgmt")
@Data
public class AdminNameProperties {

    private Set<String> adminName = new HashSet<>(Arrays.asList("clklog", "admin"));

    public Set<String> getAdminName() {
        return adminName == null ? Collections.emptySet() : Collections.unmodifiableSet(adminName);
    }

    public void setAdminName(Set<String> adminName) {
        this.adminName = adminName == null ? new HashSet<>() : new HashSet<>(adminName);
    }

    public boolean isProtected(String userName) {
        return userName != null && getAdminName().contains(userName);
    }
}
