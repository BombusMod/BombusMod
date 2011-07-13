package IE;

//#ifdef USER_KEYS
import ui.Time;
import io.NvStorage;
import java.io.DataInputStream;
import io.file.FileIO;
import java.io.IOException;
import util.StringLoader;
import java.io.InputStream;
import java.io.DataOutputStream;
import java.util.Vector;
import ui.keys.UserKeyExec;
import ui.keys.UserKey;
import ui.keys.KeyScheme;

/**
*
* @author Totktonada
*/
public class UserKeys {

    public UserKeys(String path, int direction, boolean fromResource) {
        UserKeyExec uexec = UserKeyExec.getInstance();

        if (direction == 0) {
            uexec.keyScheme = loadFromFile(path, fromResource);
            rmsUpdate(uexec.keyScheme);
        } else {
            exportData(uexec.keyScheme, path);
        }
    }

    public static KeyScheme loadFromStorage() {
        final KeyScheme keyScheme = new KeyScheme();
        DataInputStream is = NvStorage.ReadFileRecord(UserKey.storage, 0);

        try {
            int size = is.readInt();
            keyScheme.setModificatorCode(is.readInt());
            for (int i = 0; i < size; i++) {
                keyScheme.addKey(UserKey.createFromDataInputStream(is));
            }
        } catch (Exception e) {
            return null;
        }

        return keyScheme;
    }

    public static KeyScheme loadFromFile(String path, boolean fromResource) {
        final KeyScheme keyScheme = new KeyScheme();

        Vector[] table = null;
        if (!fromResource) {
            FileIO f = FileIO.createConnection(path);
            try {
                InputStream in = f.openInputStream();
                table = new StringLoader().stringLoader(in, 2);
                f.close();
            } catch (IOException e) { 
//#ifdef DEBUG
//#                 e.printStackTrace();
//#endif
            }
        } else {
            table = new StringLoader().stringLoader(path, 2);
        }

        if (table == null) {
            return keyScheme;
        }

        UserKey modificatorKey = UserKey.createFromStrings(
                    (String) table[0].elementAt(0),
                    (String) table[1].elementAt(0));

        keyScheme.setModificatorCode(modificatorKey.key);

        for (int i = 1; i < table[0].size(); i++) {
            keyScheme.addKey(UserKey.createFromStrings(
                    (String) table[0].elementAt(i),
                    (String) table[1].elementAt(i)));
        }

        return keyScheme;
    }

    public static void rmsUpdate(KeyScheme keyScheme) {
        DataOutputStream outputStream = NvStorage.CreateDataOutputStream();

        int size = keyScheme.getSize();
        try {
            outputStream.writeInt(size);
            outputStream.writeInt(keyScheme.getModificator().key);
        } catch (Exception e) {
            return;
        }

        Vector keysList = keyScheme.getKeysList();
        for (int i = 0; i < size; i++) {
            ((UserKey) keysList.elementAt(i)).saveMyToDataOutputStream(outputStream);
        }

        NvStorage.writeFileRecord(outputStream, UserKey.storage, 0, true);
    }

    private static void exportData(KeyScheme keyScheme, String path) {
        StringBuffer str = new StringBuffer("//UserKeys");
/*
        str.append("\n")
           .append(keyScheme.getModificator().toLine())
           .append((char) 0x09)
           .append("// Modificator");
*/
        Vector keysList = keyScheme.getFullKeysList();
        int fullSize = keyScheme.getFullSize();
        for (int i = 0; i < fullSize; i++) {
            str.append("\n")
               .append(((UserKey) keysList.elementAt(i)).toLine());
        }

        FileIO file = FileIO.createConnection(path + "userkeys_" + Time.localDate() + ".txt");
        file.fileWrite(str.toString().getBytes());
    }
}
//#endif
