/**
 * MicroEmulator Copyright (C) 2008 Bartek Teodorczyk <barteo@barteo.net>
 *
 * It is licensed under the following two licenses as alternatives: 1. GNU
 * Lesser General Public License (the "LGPL") version 2.1 or any newer version
 * 2. Apache License (the "AL") Version 2.0
 *
 * You may not use this file except in compliance with at least one of the above
 * two licenses.
 *
 * You may obtain a copy of the LGPL at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 * You may obtain a copy of the AL at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the LGPL
 * or the AL for the specific language governing permissions and limitations.
 *
 * @version $Id: AndroidTextBoxUI.java 2450 2010-12-07 13:55:14Z
 * barteo@gmail.com $
 */
package org.microemu.android.device.ui;

import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.Command;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import org.bombusmod.BombusModActivity;
import org.microemu.DisplayAccess;
import org.microemu.MIDletBridge;
import org.microemu.device.InputMethod;
import org.microemu.device.ui.CommandUI;
import org.microemu.device.ui.TextBoxUI;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.widget.AppCompatEditText;

import java.util.Vector;

public class AndroidTextBoxUI extends AndroidDisplayableUI implements TextBoxUI {

    private EditText editor;
    private TableLayout commandGrid;

    public AndroidTextBoxUI(final BombusModActivity activity, final TextBox textBox) {
        super(activity, textBox, true);
        activity.post(new Runnable() {
            public void run() {
                view = createMainView();        
            }
        });
    }

    private TextBox getTextBox() {
        return (TextBox) displayable;
    }

    @Override
    public void invalidate() {
        activity.post(new Runnable() {
            public void run() {
                activity.setTitle(displayable.getTitle());
                activity.supportInvalidateOptionsMenu();
            }
        });
    }


    private void turnKeyboard(final EditText textEditor, final boolean on) {
        activity.post(new Runnable() {
            @Override
            public void run() {
                if (on) {
                    Configuration conf = Resources.getSystem().getConfiguration();
                    if (conf.hardKeyboardHidden != Configuration.HARDKEYBOARDHIDDEN_NO) {
                        getInputMethodManager().showSoftInput(textEditor,
                                InputMethodManager.SHOW_FORCED | InputMethodManager.SHOW_IMPLICIT);
                    }
                } else {
                    //getInputMethodManager().hideSoftInputFromWindow(textEditor.getWindowToken(),
                    //        InputMethodManager.HIDE_IMPLICIT_ONLY | InputMethodManager.HIDE_NOT_ALWAYS);
                    getInputMethodManager().hideSoftInputFromWindow(activity.getContentView().getWindowToken(), 0);
                }
            }
        });
    }

    private EditText createEditor(final BombusModActivity activity, final TextBox textBox) {
        final EditText editor = new AppCompatEditText(activity) {
            @Override
            protected void onWindowVisibilityChanged(int visibility) {
                if (View.VISIBLE != visibility) {
                    turnKeyboard(this, false);
                }
                super.onWindowVisibilityChanged(visibility);
                if (View.VISIBLE == visibility) {
                    turnKeyboard(this, true);
                }
            }
        };
        editor.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editor.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    turnKeyboard(editor, false);
                    return true;
                }
                return false;
            }
        });

        int constraints = getTextBox().getConstraints();
        if ((constraints & TextField.CONSTRAINT_MASK) == TextField.URL) {
            editor.setSingleLine(true);
        } else if ((constraints & TextField.CONSTRAINT_MASK) == TextField.NUMERIC) {
            editor.setSingleLine(true);
            editor.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else if ((constraints & TextField.CONSTRAINT_MASK) == TextField.DECIMAL) {
            editor.setSingleLine(true);
            editor.setInputType(
                    InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        } else if ((constraints & TextField.CONSTRAINT_MASK) == TextField.PHONENUMBER) {
            editor.setSingleLine(true);
            editor.setInputType(InputType.TYPE_CLASS_PHONE);
        } else {
            editor.setSingleLine(false);
            editor.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            editor.setInputType(editor.getInputType() | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        }
        if ((constraints & TextField.PASSWORD) != 0) {
            editor.setTransformationMethod(PasswordTransformationMethod.getInstance());
            editor.setTypeface(Typeface.MONOSPACE);
        }
        editor.addTextChangedListener(new TextWatcher() {
            private String previousText;

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousText = s.toString();
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() <= getTextBox().getMaxSize()
                        && InputMethod.validate(s.toString(), getTextBox().getConstraints())) {
                } else {
                    editor.setText(previousText);
                    editor.setSelection(start);
                }
            }
        });
    }

    @Override
    public void showNotify() {
        activity.post(new Runnable() {
            public void run() {
                if (view == null) {
                    EditText old = editor;
                    LayoutInflater inf = activity.getLayoutInflater();
                    view = inf.inflate(R.layout.editor, null, false);
                    editor = (EditText) view.findViewById(R.id.text);
                    editor.setText(old.getText().toString());
                    editor.setSelection(old.getSelectionEnd());
                    commandGrid = (TableLayout) view.findViewById(R.id.commandBar);
                    updateTextConstraints();
                    initCommands();
                }
                final TextBox textBox = getTextBox();
                int constraints = textBox.getConstraints();
                if ((constraints & TextField.CONSTRAINT_MASK) == TextField.URL) {
                    editor.setSingleLine(true);
                } else if ((constraints & TextField.CONSTRAINT_MASK) == TextField.NUMERIC) {
                    editor.setSingleLine(true);
                    editor.setInputType(InputType.TYPE_CLASS_NUMBER);
                } else if ((constraints & TextField.CONSTRAINT_MASK) == TextField.DECIMAL) {
                    editor.setSingleLine(true);
                    editor.setInputType(
                            InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                } else if ((constraints & TextField.CONSTRAINT_MASK) == TextField.PHONENUMBER) {
                    editor.setSingleLine(true);
                    editor.setInputType(InputType.TYPE_CLASS_PHONE);
                } else {
                    editor.setSingleLine(false);
                    editor.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                    editor.setInputType(editor.getInputType() | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                }
                if ((constraints & TextField.PASSWORD) != 0) {
                    editor.setSingleLine(true);
                    editor.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    editor.setTypeface(Typeface.MONOSPACE);
                }
                activity.setContentView(view);
                activity.supportInvalidateOptionsMenu();
                view.requestLayout();
                view.requestFocus();
            }
        });

    }

    @Override
    public void hideNotify() {
        activity.post(new Runnable() {
            @Override
            public void run() {
                view = null;
            }
        });
    }

    private InputMethodManager getInputMethodManager() {
        return (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    //
    // TextBoxUI
    //
    public int getCaretPosition() {
        return editor.getSelectionStart();
    }

    public String getString() {
        final String[] getStringTransfer = new String[1];
        if (activity.isActivityThread()) {
            getStringTransfer[0] = editor.getText().toString();
        } else {
            getStringTransfer[0] = null;
            activity.post(new Runnable() {
                public void run() {
                    synchronized (AndroidTextBoxUI.this) {
                        getStringTransfer[0] = editor.getText().toString();
                        AndroidTextBoxUI.this.notify();
                    }
                }
            });

            synchronized (AndroidTextBoxUI.this) {
                if (getStringTransfer[0] == null) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return getStringTransfer[0];
    }

    public void setString(final String text) {
        activity.post(new Runnable() {
            public void run() {
                editor.setText(text);
                if (text != null) {
                    editor.setSelection(text.length());
                }
            }
        });
    }

    public void insert(final String text, final int position) {
        activity.post(new Runnable() {
            public void run() {
                String newtext = getString();
                if (position > 0) {
                    newtext = newtext.substring(0, position) + text + newtext.substring(position);
                } else {
                    newtext = text + newtext;
                }
                editor.setText(newtext);
                editor.setSelection(position + text.length());
            }
        });
    }

    public void delete(final int offset, final int length) {
        activity.post(new Runnable() {
            public void run() {
                String newtext = getString();
                if (offset > 0) {
                    newtext = newtext.substring(0, offset) + newtext.substring(offset + length);
                } else {
                    newtext = newtext.substring(length);
                }
                editor.setText(newtext);
                editor.setSelection(offset);
            }
        });
    }

    @Override
    public void addCommandUI(CommandUI cmd) {
        super.addCommandUI(cmd);
        initCommands();
    }

    @Override
    public void removeCommandUI(CommandUI cmd) {
        super.removeCommandUI(cmd);
        initCommands();
    }

    private void initCommands() {
        TableLayout table = commandGrid;
        table.setStretchAllColumns(true);
        table.removeAllViews();
        LinearLayout row = createRow(table);

        Vector<AndroidCommandUI> commands = getCommandsUI();
        for (AndroidCommandUI c : commands) {
            if (c.getCommand().getCommandType() == Command.ITEM) {
                if (3 == row.getChildCount()) {
                    row = createRow(table);
                }
                addToRow(row, createButton(row.getContext(), c));
            }
        }

        for (AndroidCommandUI c : commands) {
            if (c.getCommand().getCommandType() == Command.OK) {
                if (3 == row.getChildCount()) {
                    row = createRow(table);
                }
                addToRow(row, createButton(row.getContext(), c));
            }
        }
        invalidate();
    }

    private LinearLayout createRow(TableLayout table) {
        LinearLayout row = new LinearLayout(table.getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        table.addView(row, new TableRow.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.FILL_PARENT, 1.0f));
        return row;
    }

    private void addToRow(TableRow row, View view) {
        row.addView(view, new TableRow.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.FILL_PARENT, 1.0f));
    }

    private void addToRow(LinearLayout row, View view) {
        row.addView(view, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT, 1.0f));
    }

    private Button createButton(Context context, final AndroidCommandUI c) {
        Button button = new Button(context);
        button.setText(c.getCommand().getLabel());
        button.setCompoundDrawables(c.getDrawable(), null, null, null);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                execCommand(c);
            }
        });
        return button;
    }

    private boolean execCommand(CommandUI c) {
        try {
            final DisplayAccess da = MIDletBridge.getMIDletAccess().getDisplayAccess();
            AndroidDisplayableUI ui = (AndroidDisplayableUI) da.getDisplayableUI(da.getCurrent());
            da.commandAction(c.getCommand(), da.getCurrent());
            return true;
        } catch (Exception e) {
            // Exception when:
            // MIDletBridge.getMIDletAccess() is null
            // MIDletBridge.getMIDletAccess().getDisplayAccess() is null
            // da.getDisplayableUI(da.getCurrent()) is null
            // Exception in commandAction
            return false;
        }
    }
}
