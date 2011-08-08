package io.file;

import java.io.InputStream;
import java.io.IOException;
//#if android
import org.bombusmod.BombusModActivity;
//#else
import midlet.BombusMod;
//#endif

public class InternalResource {
    public static InputStream getResourceAsStream(String resource) {
    InputStream in = null;

//#if android
    try {
        in = BombusModActivity.getInstance().getAssets().open(resource.substring(1));
    } catch (IOException e) {
    }
//#else
	in = BombusMod.getInstance().getClass().getResourceAsStream(resource);
//#endif

    return in;
    }
}
