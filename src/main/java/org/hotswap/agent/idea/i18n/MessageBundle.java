package org.hotswap.agent.idea.i18n;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public class MessageBundle {

    private static final @NonNls String BUNDLE = "messages.HotswapAgent";

    private static final DynamicBundle INSTANCE = new DynamicBundle(MessageBundle.class, BUNDLE);

    private MessageBundle() {

    }

    public static @NotNull @Nls String message(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key,
                                               Object @NotNull ... params) {
        return INSTANCE.getMessage(key, params);
    }
}
