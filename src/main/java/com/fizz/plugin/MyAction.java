package com.fizz.plugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;

public class MyAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        JBPopup message = JBPopupFactory.getInstance().createMessage("hello World!");
        message.showInBestPositionFor(event.getDataContext());
        System.out.println("---");
    }
}
