package io.cssvarslinker;

import com.intellij.lang.css.CSSLanguage;
import com.intellij.lang.css.psi.CssDeclaration;
import com.intellij.lang.css.psi.CssFile;
import com.intellij.lang.css.psi.CssRuleset;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.css.CssCustomProperty;
import com.intellij.psi.css.CssCustomPropertyProvider;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class CssExternalVarsProvider implements CssCustomPropertyProvider {

    private static final String CONFIG_PATH = ".cssvarsconfig";

    @Override
    public @NotNull List<CssCustomProperty> getCustomProperties(@NotNull Project project) {
        List<CssCustomProperty> properties = new ArrayList<>();

        File configFile = new File(project.getBasePath(), CONFIG_PATH);
        if (!configFile.exists()) return properties;

        try {
            List<String> lines = Files.readAllLines(configFile.toPath());
            for (String relativePath : lines) {
                if (relativePath.trim().isEmpty()) continue;

                File cssFile = new File(project.getBasePath(), relativePath.trim());
                VirtualFile vFile = LocalFileSystem.getInstance().findFileByIoFile(cssFile);
                if (vFile == null) continue;

                PsiFile psiFile = PsiManager.getInstance(project).findFile(vFile);
                if (!(psiFile instanceof CssFile)) continue;

                CssRuleset[] rulesets = ((CssFile) psiFile).getRulesets();
                for (CssRuleset ruleset : rulesets) {
                    for (CssDeclaration decl : ruleset.getDeclarations()) {
                        String propName = decl.getPropertyName();
                        if (propName != null && propName.startsWith("--")) {
                            properties.add(new CssCustomProperty(propName, decl.getValue(), CSSLanguage.INSTANCE));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return properties;
    }
}