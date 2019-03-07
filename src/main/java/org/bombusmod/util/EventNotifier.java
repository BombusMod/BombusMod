package org.bombusmod.util;

public interface EventNotifier {
    void startNotify(String soundMediaType,
                     String soundFileName,
                     int sndVolume,
                     int vibraLength);
}
