package io.cssvarslinker;

import com.intellij.codeInsight.completion.*;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.ProcessingContext;
import com.intellij.util.indexing.StubIndex;
import org.jetbrains.annotations.NotNull;

public class CssVarsCompletionContributor extends CompletionContributor {
    public CssVarsCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(PsiElement.class),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet result) {
                        PsiElement element = parameters.getPosition();
                        if (element.getText().contains("var(--")) {
                            Project project = element.getProject();
                            GlobalSearchScope scope = GlobalSearchScope.allScope(project);
                            StubIndex.getInstance().processAllKeys(CssVarsIndex.KEY, key -> {
                                if (key.startsWith("--")) {
                                    result.addElement(LookupElementBuilder.create(key));
                                }
                                return true;
                            }, scope, null);
                        }
                    }
                });
    }
}