package org.bombusmod;

import org.microemu.microedition.ImplementationInitialization;
import java.util.Map;

/**
 * @author Totktonada
 */

public class BombusModInitialization implements ImplementationInitialization {

    /*
     * (non-Javadoc)
     *
     * @see org.microemu.microedition.ImplementationInitialization#registerImplementation()
     */
    public void registerImplementation(Map parameters) {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.microemu.microedition.ImplementationInitialization#notifyMIDletStart()
     */
    public void notifyMIDletStart() {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.microemu.microedition.ImplementationInitialization#notifyMIDletDestroyed()
     */
    public void notifyMIDletDestroyed() {
        BombusModActivity.getInstance().finish();
    }

}
