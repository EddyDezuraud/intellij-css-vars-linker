package io.cssvarslinker;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.StubIndex;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CssVarsReference extends PsiReferenceBase<PsiElement> {
    public CssVarsReference(@NotNull PsiElement element) {
        super(element);
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        Project project = getElement().getProject();
        String varName = getElement().getText();
        List<ResolveResult> results = new ArrayList<>();
        StubIndex.getInstance().processElements(
                CssVarsIndex.KEY,
                varName,
                project,
                GlobalSearchScope.allScope(project),
                PsiElement.class,
                element -> {
                    results.add(new PsiElementResolveResult(element));
                    return true;
                });
        return results.toArray(new ResolveResult[0]);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        Project project = getElement().getProject();
        List<Object> variants = new ArrayList<>();
        StubIndex.getInstance().processAllKeys(
                CssVarsIndex.KEY,
                key -> {
                    if (key.startsWith("--")) {
                        variants.add(LookupElementBuilder.create(key));
                    }
                    return true;
                },
                GlobalSearchScope.allScope(project),
                null);
        return variants.toArray();
    }
}