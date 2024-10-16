package org.hotswap.agent.idea.configuration;

import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.openapi.util.Key;

public class HotswapAgentConfiguration {
    private static final Key<HotswapAgentConfiguration> KEY = Key.create("HotswapAgent");
    private boolean enabled = false;

    public static HotswapAgentConfiguration getOrCreate(RunConfigurationBase<?> runConfiguration) {
        HotswapAgentConfiguration config = runConfiguration.getCopyableUserData(KEY);
        if (config == null) {
            config = new HotswapAgentConfiguration();
        }
        runConfiguration.putCopyableUserData(KEY, config);
        return config;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "HotswapAgentConfiguration{" +
                "enabled=" + enabled +
                '}';
    }
}
