package org.bombusmod.util;

import java.io.InputStream;

public interface AssetsLoader {
    InputStream getResourceAsStream(String resource);
}
