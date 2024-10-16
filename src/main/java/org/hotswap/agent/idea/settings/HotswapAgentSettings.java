package org.hotswap.agent.idea.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.SettingsCategory;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "HotswapAgentSettings", storages = {@Storage("HotswapAgent.xml")}, category = SettingsCategory.PLUGINS)
public class HotswapAgentSettings implements PersistentStateComponent<HotswapAgentSettings> {
    private String agentPath;
    private String propertiesPath;

    public static HotswapAgentSettings getInstance() {
        return ApplicationManager.getApplication().getService(HotswapAgentSettings.class);
    }

    @Override
    public @Nullable HotswapAgentSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull HotswapAgentSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public String getAgentPath() {
        return agentPath;
    }

    public void setAgentPath(String agentPath) {
        this.agentPath = agentPath;
    }

    public String getPropertiesPath() {
        return propertiesPath;
    }

    public void setPropertiesPath(String propertiesPath) {
        this.propertiesPath = propertiesPath;
    }
}
