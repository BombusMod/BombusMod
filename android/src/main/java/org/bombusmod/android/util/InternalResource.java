package org.bombusmod.android.util;

import java.io.InputStream;
//#if android
import org.bombusmod.BombusModActivity;
import org.bombusmod.util.AssetsLoader;

import java.io.IOException;
//#else
//# import midlet.BombusMod;
//#endif

/**
 *
 * @author Totktonada
 */
public class InternalResource implements AssetsLoader {
    public InputStream getResourceAsStream(String resource) {
        InputStream in = null;

//#if android
        try {
            in = BombusModActivity.getInstance().getAssets().open(resource.substring(1));
        } catch (IOException e) {}
//#else
//#     	in = BombusMod.getInstance().getClass().getResourceAsStream(resource);
//#endif

        return in;
    }
}
