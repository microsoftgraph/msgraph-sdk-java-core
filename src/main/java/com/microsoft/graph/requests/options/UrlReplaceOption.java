package com.microsoft.graph.requests.options;

import com.microsoft.kiota.RequestOption;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class UrlReplaceOption implements RequestOption {

    private Map<String, String> replacementPairs;
    private boolean enabled;

    public UrlReplaceOption() {
        this(new HashMap<>());
    }
    public UrlReplaceOption(@Nonnull Map<String, String> replacementPairs) {
        this(true, replacementPairs);
    }
    public UrlReplaceOption(boolean enabled, @Nonnull Map<String, String> replacementPairs) {
        this.enabled = enabled;
        this.replacementPairs = new HashMap<>(replacementPairs);
    }
    public Map<String, String> getReplacementPairs() {
        return new HashMap<>(replacementPairs);
    }
    public void setReplacementPairs(@Nonnull Map<String, String> replacementPairs) {
        this.replacementPairs = new HashMap<>(replacementPairs);
    }
    public void enable() {
        this.enabled = true;
    }
    public void disable() {
        this.enabled = false;
    }
    @NotNull
    @Override
    public <T extends RequestOption> Class<T> getType() {
        return (Class<T>) UrlReplaceOption.class;
    }
}
