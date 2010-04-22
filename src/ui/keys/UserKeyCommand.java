/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui.keys;

/**
 *
 * @author Totktonada
 */
public class UserKeyCommand {
    public final int command_id;
    public final String description;

    public UserKeyCommand(int command_id, String description) {
        this.command_id = command_id;
        this.description = description;
    }

    public boolean equals(Object obj) {
        if (obj instanceof UserKeyCommand) {
            UserKeyCommand cmd = (UserKeyCommand) obj;
            return (command_id == cmd.command_id);
        }
        return false;
    }
}
