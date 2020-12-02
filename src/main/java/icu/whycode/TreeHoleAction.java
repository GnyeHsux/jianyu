package icu.whycode;

import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class TreeHoleAction extends AnAction {


    private ConsoleView consoleView = null;

    public TreeHoleAction(@Nullable String text, @Nullable String description, @Nullable Icon icon, ConsoleView consoleView) {
        super(text, description, icon);
        this.consoleView = consoleView;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
//        Messages.showMessageDialog("树洞", "Custom Action", Messages.getInformationIcon());
        consoleView.print("凉瓜铺。\n", ConsoleViewContentType.NORMAL_OUTPUT);
    }
}
