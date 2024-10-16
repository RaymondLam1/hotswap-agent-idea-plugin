package org.hotswap.agent.idea.configuration;

import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.ui.NestedGroupFragment;
import com.intellij.execution.ui.SettingsEditorFragment;

import java.util.ArrayList;
import java.util.List;

import static org.hotswap.agent.idea.constant.Constants.HOTSWAP_AGENT;

public class HotswapAgentFragment<T extends RunConfigurationBase<?>> extends NestedGroupFragment<T> {
    private final RunConfigurationBase<?> configuration;

    public HotswapAgentFragment(RunConfigurationBase<?> configuration) {
        super(HOTSWAP_AGENT, HOTSWAP_AGENT, HOTSWAP_AGENT, p -> false);
        this.configuration = configuration;
    }

    @Override
    protected List<SettingsEditorFragment<T, ?>> createChildren() {
        HotswapAgentConfiguration config = HotswapAgentConfiguration.getOrCreate(configuration);

        List<SettingsEditorFragment<T, ?>> fragments = new ArrayList<>();
        SettingsEditorFragment<T, ?> enableFlag =
                SettingsEditorFragment.createTag(
                        HOTSWAP_AGENT,
                        HOTSWAP_AGENT,
                        HOTSWAP_AGENT,
                        c -> config.isEnabled(),
                        (c, value) -> config.setEnabled(value));
        fragments.add(enableFlag);

        return fragments;
    }
}
