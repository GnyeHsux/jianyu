package icu.whycode.test;

import com.intellij.execution.Executor;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.execution.ui.RunnerLayoutUi;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import icu.whycode.IconUtil;
import icu.whycode.JianyuExecutor;
import icu.whycode.TreeHoleAction;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class MyToolWindowFactory implements ToolWindowFactory {

    /**
     * Create the tool window content.
     *
     * @param project    current project
     * @param toolWindow current tool window
     */
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
//        JPanel myToolWindow = new JPanel();
//        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
//        Content content = contentFactory.createContent(myToolWindow, "", false);
//        toolWindow.getContentManager().addContent(content);


        ConsoleView consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
        Content content = toolWindow.getContentManager().getFactory().createContent(consoleView.getComponent(), "Tree hole", false);
        toolWindow.getContentManager().addContent(content);

        RunnerLayoutUi layoutUi = RunnerLayoutUi.Factory.getInstance(project).create("runnerId", "runnerTitle", "sessionName", project);
        layoutUi.createContent("contentId", consoleView.getComponent(), "treeHole", AllIcons.Debugger.Console, consoleView.getComponent());
        layoutUi.addContent(content);
        layoutUi.getOptions().setLeftToolbar(createActionToolbar(consoleView), "RunnerToolbar");

        consoleView.print("分享一点本人的小变态心思。\n" +
                "我对面坐着一个超漂亮的音乐老师。\n" +
                "其他老师桌子上都是参考资料，音乐老师桌子上是一面镜子。\n" +
                "很多时候可以看见音乐老师在照镜子，涂口红。\n" +
                "之前人家第一次涂口红的时候，我呆呆的盯着人家看了。\n" +
                "“你看着我干嘛啊？”\n" +
                "我只记得我当时脸就红了，特别不意思了。\n" +
                "说实话，其实超想看的\n" +
                "啊啊啊啊啊啊啊啊啊啊啊啊啊\n" +
                "\n", ConsoleViewContentType.NORMAL_OUTPUT);
    }

    private ActionGroup createActionToolbar(ConsoleView consoleView) {
        final DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(new TreeHoleAction("tree hole", "tree hole", IconUtil.ICON, consoleView));
        return actionGroup;
    }
}
