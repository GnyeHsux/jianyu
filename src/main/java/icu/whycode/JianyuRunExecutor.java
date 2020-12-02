package icu.whycode;

import com.intellij.execution.Executor;
import com.intellij.execution.ExecutorRegistry;
import com.intellij.openapi.util.NlsActions;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class JianyuRunExecutor extends Executor {

    public static final String TOOL_WINDOW_ID = "JianYu";

    @NotNull
    @Override
    public String getToolWindowId() {
        return TOOL_WINDOW_ID;
    }

    @NotNull
    @Override
    public Icon getToolWindowIcon() {
        return IconUtil.ICON;
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return IconUtil.ICON;
    }

    @Override
    public Icon getDisabledIcon() {
        return IconUtil.ICON;
    }

    @Override
    public @NlsActions.ActionDescription String getDescription() {
        return TOOL_WINDOW_ID;
    }

    @NotNull
    @Override
    public @NlsActions.ActionText String getActionName() {
        return TOOL_WINDOW_ID;
    }

    @NotNull
    @Override
    public String getId() {
        return StringConst.PLUGIN_ID;
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @NotNull
    @Override
    public String getStartActionText() {
        return TOOL_WINDOW_ID;
    }

    @Override
    public String getContextActionId() {
        return "custom context action id";
    }

    @Override
    public String getHelpId() {
        return TOOL_WINDOW_ID;
    }

    public static Executor getRunExecutorInstance() {
        return ExecutorRegistry.getInstance().getExecutorById(StringConst.PLUGIN_ID);
    }
}
