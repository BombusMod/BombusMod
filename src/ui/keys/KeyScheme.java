package ui.keys;

//#ifdef USER_KEYS
import java.util.Vector;

/**
 *
 * @author Totktonada
 */
public class KeyScheme {
    private Vector keysList;

    public KeyScheme() {
        keysList = new Vector();
        UserKey modificatorKey = new UserKey();
        modificatorKey.command_id = -1;
        modificatorKey.modificator = false;
        keysList.addElement(modificatorKey);
    }

    public KeyScheme(KeyScheme keyScheme) {
//        this();
        copyFrom(keyScheme);
    }

    public int getSize() {
        return keysList.size() - 1;
    }

    public int getFullSize() {
        return keysList.size();
    }

    public void setModificatorCode(int key) {
        getModificator().key = key;
    }

    public UserKey getModificator() {
        return (UserKey) keysList.elementAt(0);
    }

    public void addKey(UserKey u) {
        keysList.addElement(u);
    }

    public Vector getKeysList() {
        Vector newList = new Vector();
        int size = getFullSize();
        for (int i = 1; i < size; i++) {
            newList.addElement(new UserKey((UserKey) keysList.elementAt(i)));
        }
        return newList;
    }

    public Vector getFullKeysList() {
        return keysList;
    }

/* Currently not used
    public void removeFromKeysList(int index) {
        keysList.removeElementAt(index + 1);
    }
*/

    public void removeFromFullKeysList(int index) {
        keysList.removeElementAt(index);
    }

    private void copyFrom(KeyScheme keyScheme) {
        keysList = new Vector();

        Vector keysList0 = keyScheme.getFullKeysList();
        int size = keyScheme.getFullSize();
        for (int i = 0; i < size; i++) {
            addKey(new UserKey((UserKey) keysList0.elementAt(i)));
        }
    }
}
//#endif
