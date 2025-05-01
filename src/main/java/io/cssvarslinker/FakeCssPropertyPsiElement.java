package io.cssvarslinker;

import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.FakePsiElement;
import org.jetbrains.annotations.NotNull;

public class FakeCssPropertyPsiElement extends FakePsiElement {
    private final Project project;
    private final String name;

    public FakeCssPropertyPsiElement(Project project, String name) {
        this.project = project;
        this.name = name;
    }

    @Override
    public PsiElement getParent() {
        return null;
    }

    @Override
    public PsiManager getManager() {
        return PsiManager.getInstance(project);
    }

    @Override
    public @NotNull Language getLanguage() {
        return Language.findLanguageByID("CSS");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "FakeCssPropertyPsiElement(" + name + ")";
    }
}