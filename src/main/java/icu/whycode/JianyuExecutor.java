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
import com.intellij.ui.content.Content;
import icu.whycode.entity.TreeHole;
import icu.whycode.website.JianYuTreeHolePageProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.pipeline.Pipeline;

import javax.swing.*;
import java.util.List;

public class JianyuExecutor implements Disposable {

    private ConsoleView consoleView = null;

    private Project project = null;

    public JianyuExecutor(Project project) {
        this.project = project;
        this.consoleView = createConsoleView(project);
    }

    public JianyuExecutor withReturn(Runnable returnAction) {
        this.rerunAction = returnAction;
        return this;
    }

    public JianyuExecutor withStop(Runnable stopAction, Computable<Boolean> stopEnabled) {
        this.nextAction = stopAction;
        return this;
    }

    @Override
    public void dispose() {
        Disposer.dispose(this);
    }

    private int listIndex = 0;
    private List<TreeHole> treeHoleList;
    private String nextPageUrl = "";
    private Spider spider;

    private ConsoleView createConsoleView(Project project) {
        TextConsoleBuilder consoleBuilder = TextConsoleBuilderFactory.getInstance().createBuilder(project);
        ConsoleView console = consoleBuilder.getConsole();
        // 刷新煎蛋树洞，输出第一个树洞
//        Spider.create(new JianYuTreeHolePageProcessor()).addUrl("https://jandan.net/treehole").addPipeline(new ConsolePipeline()).run();
        listIndex = 0;
        console.print("请求煎鱼树洞中。。。", ConsoleViewContentType.LOG_WARNING_OUTPUT);
        spider = Spider.create(new JianYuTreeHolePageProcessor()).addUrl("https://jandan.net/treehole")
                .addPipeline((ResultItems resultItems, Task task) -> {
                    nextPageUrl = resultItems.get("nextPageUrl");
                    treeHoleList = resultItems.get("treeHoleList");
                    if (treeHoleList.size() > 0) {
                        console.clear();
                        console.print("洞主：" + treeHoleList.get(0).getAuthor() + "\n", ConsoleViewContentType.LOG_WARNING_OUTPUT);
                        console.print("内容：" + treeHoleList.get(0).getText(), ConsoleViewContentType.NORMAL_OUTPUT);
                    }
                }).thread(1);
        spider.start();
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

        final JComponent consolePanel = createConsolePanel(consoleView);

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

    private JComponent createConsolePanel(ConsoleView consoleView) {
        JPanel panel = new JPanel();
        panel.add(consoleView.getComponent());
        return consoleView.getComponent();
    }


    private Runnable rerunAction;
    private Runnable nextAction;

    private ActionGroup createActionToolbar(JComponent consolePanel, ConsoleView consoleView, RunnerLayoutUi layoutUi, RunContentDescriptor descriptor, Executor executor) {
        final DefaultActionGroup actionGroup = new DefaultActionGroup();
//        actionGroup.add(new TreeHoleAction("tree hole", "tree hole", IconUtil.ICON, consoleView));
        actionGroup.add(new RerunAction(consolePanel, consoleView));
        NextAction next = new NextAction(consoleView);
        actionGroup.add(next);
        BackAction backAction = new BackAction(consoleView);
        actionGroup.add(backAction);
        return actionGroup;
    }

    private class RerunAction extends AnAction implements DumbAware {
        private final ConsoleView consoleView;

        public RerunAction(JComponent consolePanel, ConsoleView consoleView) {
            super("Refresh", "刷新树洞", AllIcons.Actions.Restart);
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

    private class NextAction extends AnAction implements DumbAware {
        private final ConsoleView consoleView;

        public NextAction(ConsoleView consoleView) {
            super("Next", "下一条", AllIcons.Actions.NextOccurence);
            this.consoleView = consoleView;
        }

        @Override
        public void actionPerformed(AnActionEvent e) {
            nextAction.run();
            // 输出下一条
            if (listIndex + 1 <= treeHoleList.size() - 1) {
                listIndex++;
                consolePrint(treeHoleList.get(listIndex));
            } else {
                // 加载下一页
                consoleWarmPrint("加载下一页中。。。");
                spider.stop();
                spider = Spider.create(new JianYuTreeHolePageProcessor()).addUrl("https:" + nextPageUrl)
                        .addPipeline((ResultItems resultItems, Task task) -> {
                            nextPageUrl = resultItems.get("nextPageUrl");
                            treeHoleList.addAll(resultItems.get("treeHoleList"));
                            actionPerformed(e);
                        }).thread(1);
                spider.start();
            }
        }

        @Override
        public void update(AnActionEvent e) {
            if (treeHoleList != null) {
                e.getPresentation().setEnabled(listIndex + 1 <= treeHoleList.size());
            }
        }
    }

    private class BackAction extends AnAction implements DumbAware {
        private final ConsoleView consoleView;

        public BackAction(ConsoleView consoleView) {
            super("Back", "上一条", AllIcons.Actions.Back);
            this.consoleView = consoleView;
        }

        @Override
        public void actionPerformed(AnActionEvent e) {
            nextAction.run();
            // 输出上一条
            if (listIndex - 1 >= 0) {
                listIndex--;
                consolePrint(treeHoleList.get(listIndex));
            } else {
                consoleWarmPrint("前面没有了哦...");
            }
        }

        @Override
        public void update(AnActionEvent e) {
            e.getPresentation().setEnabled(listIndex - 1 >= 0);
        }
    }

    private void consolePrint(TreeHole treeHole) {
        consoleView.clear();
        consoleView.print("洞主：" + treeHole.getAuthor() + "\n", ConsoleViewContentType.LOG_WARNING_OUTPUT);
        consoleView.print("内容：" + treeHole.getText(), ConsoleViewContentType.NORMAL_OUTPUT);
    }

    private void consoleWarmPrint(String msg) {
        consoleView.clear();
        consoleView.print(msg, ConsoleViewContentType.NORMAL_OUTPUT);
    }
}
