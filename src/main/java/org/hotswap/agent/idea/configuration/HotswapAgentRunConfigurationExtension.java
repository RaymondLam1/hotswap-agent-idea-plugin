package org.hotswap.agent.idea.configuration;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.JavaRunConfigurationBase;
import com.intellij.execution.RunConfigurationExtension;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.util.JavaParametersUtil;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.lang.JavaVersion;
import org.hotswap.agent.idea.i18n.MessageBundle;
import org.hotswap.agent.idea.settings.HotswapAgentSettings;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class HotswapAgentRunConfigurationExtension extends RunConfigurationExtension {
    private static final Logger logger = Logger.getInstance(HotswapAgentRunConfigurationExtension.class);

    @Override
    public <T extends RunConfigurationBase<?>> void updateJavaParameters(@NotNull T configuration, @NotNull JavaParameters params, @Nullable RunnerSettings runnerSettings) throws ExecutionException {
        HotswapAgentConfiguration settings = HotswapAgentConfiguration.getOrCreate(configuration);
        if (!settings.isEnabled()) {
            return;
        }

        JavaRunConfigurationBase config = (JavaRunConfigurationBase) configuration;
        Sdk dcevmJDK = findDcevmJDK(config);
        if (dcevmJDK == null) {
            NotificationGroupManager.getInstance().getNotificationGroup("HotswapAgent").createNotification(
                    MessageBundle.message("illegal_DCEVM_JDK_title"), MessageBundle.message("illegal_DCEVM_JDK_content"),
                    NotificationType.WARNING).notify(configuration.getProject());
            logger.warn("Fail to find dcevm jdk for run configuration [ " + configuration.getName() + "]");
            return;
        }

        config.setAlternativeJrePath(dcevmJDK.getHomePath());
        config.setAlternativeJrePathEnabled(true);

        updateJavaParameters(params, dcevmJDK);
    }

    @Override
    protected <P extends RunConfigurationBase<?>> List<SettingsEditor<P>> createFragments(@NotNull P configuration) {
        return Collections.singletonList(new HotswapAgentFragment<>(configuration));
    }

    @Override
    public boolean isApplicableFor(RunConfigurationBase<?> configuration) {
        return configuration instanceof JavaRunConfigurationBase;
    }

    @Override
    protected @Nullable @NlsContexts.TabTitle String getEditorTitle() {
        return "HotswapAgent";
    }

    @Override
    protected void writeExternal(@NotNull RunConfigurationBase<?> runConfiguration, @NotNull Element element) {
        HotswapAgentConfiguration settings = HotswapAgentConfiguration.getOrCreate(runConfiguration);
        element.setAttribute("enabled", Boolean.toString(settings.isEnabled()));
    }

    @Override
    protected void readExternal(@NotNull RunConfigurationBase<?> runConfiguration, @NotNull Element element) {
        HotswapAgentConfiguration settings = HotswapAgentConfiguration.getOrCreate(runConfiguration);
        settings.setEnabled(Boolean.parseBoolean(element.getAttributeValue("enabled")));
    }

    protected String getSerializationId() {
        return "HotswapAgent";
    }

    private Sdk findDcevmJDK(JavaRunConfigurationBase configuration) {
        String jrePath = findJDK(configuration);
        if (StringUtil.isEmpty(jrePath)) {
            logger.warn("Fail to find alternative jre path for configuration: " + configuration.getName());
            return null;
        }

        if (StringUtil.isNotEmpty(jrePath) && StringUtil.containsIgnoreCase(jrePath, "dcevm")) {
            try {
                return JavaParametersUtil.createProjectJdk(configuration.getProject(), jrePath);
            } catch (Throwable t) {
                logger.warn("Fail to create dcevm jdk for configuration: " + configuration.getName());
            }
        }

        return null;
    }

    private static String findJDK(JavaRunConfigurationBase configuration) {
        // alternative jre path
        String jrePath = configuration.getAlternativeJrePath();
        if (StringUtil.isNotEmpty(jrePath)) {
            return jrePath;
        }

        // module sdk
        Module module = configuration.getConfigurationModule().getModule();
        if (module != null) {
            Sdk sdk = ModuleRootManager.getInstance(module).getSdk();
            if (sdk != null) {
                jrePath = sdk.getHomePath();
            }
        }
        if (StringUtil.isNotEmpty(jrePath)) {
            return jrePath;
        }

        // project sdk
        Sdk sdk = ProjectRootManager.getInstance(configuration.getProject()).getProjectSdk();
        if (sdk != null) {
            jrePath = sdk.getHomePath();
        }
        return jrePath;
    }

    private void updateJavaParameters(@NotNull JavaParameters params, @NotNull Sdk dcevmJDK) {
        ParametersList vmParams = params.getVMParametersList();
        vmParams.add("-XX+AllowEnhancedClassRedefinition");

        JavaVersion version = JavaVersion.tryParse(dcevmJDK.getVersionString());
        if (version != null && version.isAtLeast(11)) {
            vmParams.addParametersString("--add-opens java.base/java.net=ALL-UNNAMED");
            vmParams.addParametersString("--add-opens java.base/jdk.internal.loader=ALL-UNNAMED");
            vmParams.add("-XX:HotswapAgent=external");
        }

        String agentPath = HotswapAgentSettings.getInstance().getAgentPath();
        String propertiesPath = HotswapAgentSettings.getInstance().getPropertiesPath();
        vmParams.add(String.format("-javaagent:%s=propertiesFilePath=%s", agentPath, propertiesPath));
    }
}
