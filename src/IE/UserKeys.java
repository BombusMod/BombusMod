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

/**
*
* @author Totktonada
*/
public class UserKeys {

    public UserKeys(String path, int direction, boolean fromResource) {
        UserKeyExec uexec = UserKeyExec.getInstance();

        if (direction == 0) {
            uexec.keysList = loadFromFile(path, fromResource);
            rmsUpdate(uexec.keysList);
        } else {
            exportData(uexec.keysList, path);
        }
    }

    public static Vector loadFromStorage() {
        final Vector keysList = new Vector();
        DataInputStream is = NvStorage.ReadFileRecord(UserKey.storage, 0);

        int size = 0;
        try {
            size = is.readInt();
            for (int i = 0; i < size; i++) {
                UserKey u = UserKey.createFromDataInputStream(is);
                keysList.addElement(u);
            }
        } catch (Exception e) {
            return null;
        }

        return keysList;
    }

    public static Vector loadFromFile(String path, boolean fromResource) {
        final Vector keysList = new Vector();

        Vector[] table = null;
        if (!fromResource) {
            FileIO f = FileIO.createConnection(path);
            try {
                InputStream in = f.openInputStream();
                table = new StringLoader().stringLoader(in, 3);
                f.close();
            } catch (IOException e) { 
//#ifdef DEBUG
//#                 e.printStackTrace();
//#endif
            }
        } else {
            table = new StringLoader().stringLoader(path, 3);
        }

        for (int i = 0; i < table[0].size(); i++) {
            keysList.addElement(UserKey.createFromStrings(
                    (String) table[0].elementAt(i),
                    (String) table[1].elementAt(i),
                    (String) table[2].elementAt(i)));
        }

        return keysList;
    }

    public static void rmsUpdate(Vector keysList) {
        DataOutputStream outputStream = NvStorage.CreateDataOutputStream();

        int size = keysList.size();
        try {
            outputStream.writeInt(size);
        } catch (Exception e) {
            return;
        }

        for (int i = 0; i < size; i++) {
            ((UserKey) keysList.elementAt(i)).saveMyToDataOutputStream(outputStream);
        }

        NvStorage.writeFileRecord(outputStream, UserKey.storage, 0, true);
    }

    private static void exportData(Vector keysList, String path) {
        StringBuffer keyScheme = new StringBuffer("//UserKeys");

        for (int i = 0; i < keysList.size(); i++) {
            keyScheme.append("\n")
                     .append(((UserKey) keysList.elementAt(i)).toLine());
            }

        FileIO file = FileIO.createConnection(path + "userkeys_" + Time.localDate() + ".txt");
        file.fileWrite(keyScheme.toString().getBytes());
    }
}
//#endif
