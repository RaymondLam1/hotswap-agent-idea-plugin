package org.hotswap.agent.idea.configurable

import com.intellij.openapi.options.Configurable
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import org.hotswap.agent.idea.settings.HotswapAgentSettings
import javax.swing.JComponent

class HotswapAgentProjectConfigurable : Configurable {

    private var agentPathField: JBTextField = JBTextField(HotswapAgentSettings.getInstance().agentPath)

    private var propertiesPathField: JBTextField = JBTextField(HotswapAgentSettings.getInstance().propertiesPath)

    override fun createComponent(): JComponent? {
        return panel {
            row("hotswap-agent.jar:") {
                cell(agentPathField).align(AlignX.FILL).comment("Path to hotswap-agent.jar")
            }
            row("hotswap-agent.properties:") {
                cell(propertiesPathField).align(AlignX.FILL).comment("Path to hotswap-agent.properties")
            }
        }
    }

    override fun isModified(): Boolean {
        return !agentPathField.text.equals(HotswapAgentSettings.getInstance().agentPath);
    }

    override fun apply() {
        HotswapAgentSettings.getInstance().agentPath = agentPathField.text;
    }

    override fun getDisplayName(): String {
        return "Hotswap Agent";
    }
}