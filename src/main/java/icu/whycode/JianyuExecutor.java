package icu.whycode;

import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.execution.ui.RunnerLayoutUi;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class JianyuExecutor implements Disposable {

    private ConsoleView consoleView = null;

    private Project project = null;

    private Computable<Boolean> stopEnabled;

    public JianyuExecutor(Project project) {
        this.project = project;
        this.consoleView = createConsoleView(project);
    }

    public JianyuExecutor withReturn(Runnable returnAction) {
        this.rerunAction = returnAction;
        return this;
    }

    public JianyuExecutor withStop(Runnable stopAction, Computable<Boolean> stopEnabled) {
        this.stopAction = stopAction;
        this.stopEnabled = stopEnabled;
        return this;
    }

    @Override
    public void dispose() {
        Disposer.dispose(this);
    }

    private ConsoleView createConsoleView(Project project) {
        TextConsoleBuilder consoleBuilder = TextConsoleBuilderFactory.getInstance().createBuilder(project);
        ConsoleView console = consoleBuilder.getConsole();
        console.print("每当我想恋爱的时候智商都会降低一大截，好在我从来没有过一次真正意义上的恋爱。\n", ConsoleViewContentType.LOG_INFO_OUTPUT);
        console.print("每当我想恋爱的时候智商都会降低一大截，好在我从来没有过一次真正意义上的恋爱。\n", ConsoleViewContentType.NORMAL_OUTPUT);
        console.print("每当我想恋爱的时候智商都会降低一大截，好在我从来没有过一次真正意义上的恋爱。\n", ConsoleViewContentType.SYSTEM_OUTPUT);
        console.print("每当我想恋爱的时候智商都会降低一大截，好在我从来没有过一次真正意义上的恋爱。\n", ConsoleViewContentType.LOG_VERBOSE_OUTPUT);
        return console;
    }

    public void run() {
        if (project.isDisposed()) {
            return;
        }

        Executor executor = JianyuRunExecutor.getRunExecutorInstance();
        if (executor == null) {
            return;
        }

        final RunnerLayoutUi.Factory factory = RunnerLayoutUi.Factory.getInstance(project);
        RunnerLayoutUi layoutUi = factory.create("runnerId", "runnerTitle", "sessionName", project);

        final JPanel consolePanel = createConsolePanel(consoleView);

        // 设置面板信息
        RunContentDescriptor descriptor = new RunContentDescriptor(new RunProfile() {
            @Nullable
            @Override
            public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) throws ExecutionException {
                return null;
            }

            @NotNull
            @Override
            public String getName() {
                return "jianyu";
            }

            @Nullable
            @Override
            public Icon getIcon() {
                return null;
            }
        }, new DefaultExecutionResult(), layoutUi);
        descriptor.setExecutionId(System.nanoTime());

        final Content treeHoleContent = layoutUi.createContent("contentId", consolePanel, "treeHole", AllIcons.Debugger.Console, consolePanel);
        treeHoleContent.setCloseable(false);
        layoutUi.addContent(treeHoleContent);

        // 设置左边工具栏
        layoutUi.getOptions().setLeftToolbar(createActionToolbar(consolePanel, consoleView, layoutUi, descriptor, executor), "RunnerToolbar");


        Disposer.register(descriptor, this);

        Disposer.register(treeHoleContent, consoleView);

        ExecutionManager.getInstance(project).getContentManager().showRunContent(executor, descriptor);
    }

    private JPanel createConsolePanel(ConsoleView consoleView) {
        JPanel panel = new JPanel();
        panel.add(consoleView.getComponent());
        return panel;
    }

    private Runnable rerunAction;
    private Runnable stopAction;

    private ActionGroup createActionToolbar(JComponent consolePanel, ConsoleView consoleView, RunnerLayoutUi layoutUi, RunContentDescriptor descriptor, Executor executor) {
        final DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(new TreeHoleAction("tree hole", "tree hole", IconUtil.ICON, consoleView));
        actionGroup.add(new RerunAction(consolePanel, consoleView));
        actionGroup.add(new StopAction());
        return actionGroup;
    }

    private class RerunAction extends AnAction implements DumbAware {
        private final ConsoleView consoleView;

        public RerunAction(JComponent consolePanel, ConsoleView consoleView) {
            super("刷新树洞", "刷新树洞", AllIcons.Actions.Restart);
            this.consoleView = consoleView;
            registerCustomShortcutSet(CommonShortcuts.getRerun(), consolePanel);
        }

        @Override
        public void actionPerformed(AnActionEvent e) {
            Disposer.dispose(consoleView);
            rerunAction.run();
        }

        @Override
        public void update(AnActionEvent e) {
            e.getPresentation().setVisible(rerunAction != null);
            e.getPresentation().setIcon(AllIcons.Actions.Restart);
        }
    }

    private class StopAction extends AnAction implements DumbAware {
        public StopAction() {
            super("Stop", "Stop", AllIcons.Actions.Suspend);
        }

        @Override
        public void actionPerformed(AnActionEvent e) {
            stopAction.run();
        }

        @Override
        public void update(AnActionEvent e) {
            e.getPresentation().setVisible(stopAction != null);
            e.getPresentation().setEnabled(stopEnabled != null && stopEnabled.compute());
        }
    }
}
