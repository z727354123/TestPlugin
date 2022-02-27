package com.fizz.plugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;

public class MyAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {

        Mydialog dialog = new Mydialog();
        dialog.pack();
        dialog.setVisible(true);

    }
}
