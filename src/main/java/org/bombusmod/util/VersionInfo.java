package org.bombusmod.util;

public interface VersionInfo {
    String NAME = "BombusMod";
    String BOMBUS_SITE_URL = "http://github.com/BombusMod";

    String getVersionNumber();

    default String getName() {
        return NAME;
    }
    default String getUrl() {
        return BOMBUS_SITE_URL;
    }
}
