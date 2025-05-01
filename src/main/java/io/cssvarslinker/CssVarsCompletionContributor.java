package io.cssvarslinker;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiFile;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class CssVarsCompletionContributor extends CompletionContributor {

    public CssVarsCompletionContributor() {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(), new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters,
                                          @NotNull ProcessingContext context,
                                          @NotNull CompletionResultSet result) {
                Project project = parameters.getPosition().getProject();
                Set<String> variables = CssVarsLoader.loadCssVariables(project);
                for (String var : variables) {
                    result.addElement(LookupElementBuilder.create("--" + var));
                }
            }
        });
    }
}