package icu.whycode;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class JianyuAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        runExecutor(e.getProject());

    }

    public void runExecutor(Project project) {
        if (project == null) {
            return;
        }
        JianyuExecutor executor = new JianyuExecutor(project);
        // 设置restart和stop
        executor.withReturn(() -> runExecutor(project)).withStop(() -> ConfigUtil.setRunning(project,true), () ->
                ConfigUtil.getRunning(project));
        executor.run();
    }


}
