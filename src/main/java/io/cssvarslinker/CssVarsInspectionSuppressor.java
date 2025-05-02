package io.cssvarslinker;

import com.intellij.codeInspection.InspectionSuppressor;
import com.intellij.codeInspection.SuppressQuickFix;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.css.CssDeclaration;
import com.intellij.util.indexing.StubIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CssVarsInspectionSuppressor implements InspectionSuppressor {
    @Override
    public boolean isSuppressedFor(@NotNull PsiElement element, @NotNull String toolId) {
        if (!toolId.equals("CssUnknownProperty") || !(element instanceof CssDeclaration)) {
            return false;
        }
        String propertyName = ((CssDeclaration) element).getPropertyName();
        if (propertyName == null || !propertyName.startsWith("--")) {
            return false;
        }
        Project project = element.getProject();
        return StubIndex.getInstance()
                .getAllKeys(CssVarsIndex.KEY, GlobalSearchScope.allScope(project))
                .contains(propertyName);
    }

    @NotNull
    @Override
    public SuppressQuickFix[] getSuppressActions(@Nullable PsiElement element, @NotNull String toolId) {
        return SuppressQuickFix.EMPTY_ARRAY;
    }
}