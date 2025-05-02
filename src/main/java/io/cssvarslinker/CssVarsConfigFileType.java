package io.cssvarslinker;

import com.intellij.icons.AllIcons;
import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.fileTypes.PlainTextLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CssVarsConfigFileType extends LanguageFileType {
    public static final CssVarsConfigFileType INSTANCE = new CssVarsConfigFileType();

    private CssVarsConfigFileType() {
        super(PlainTextLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "CSS Vars Config";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Configuration file for CSS Vars Linker plugin";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "cssvarsconfig";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return AllIcons.FileTypes.Config;
    }
}