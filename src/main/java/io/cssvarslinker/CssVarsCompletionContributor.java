package io.cssvarslinker;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PatternCondition;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.css.CssFunction;
import com.intellij.psi.css.CssTermList;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class CssVarsCompletionContributor extends CompletionContributor {

    public CssVarsCompletionContributor() {
        // Pattern pour capturer la position où l'autocomplétion doit être active
        PsiElementPattern.Capture<PsiElement> varFunctionPattern = PlatformPatterns.psiElement()
                .with(new PatternCondition<PsiElement>("varFunctionPattern") {
                    @Override
                    public boolean accepts(@NotNull PsiElement element, ProcessingContext context) {
                        // Log pour diagnostiquer les appels
                        System.out.println("🔍 Checking completion for: " + element.getText() + " [" + element.getClass().getSimpleName() + "]");

                        // Vérifier si on est dans une fonction var()
                        CssFunction function = PsiTreeUtil.getParentOfType(element, CssFunction.class);
                        if (function != null && "var".equals(function.getName())) {
                            System.out.println("✅ In var() function");
                            return true;
                        }

                        // Pour capturer le cas où l'utilisateur tape juste "var("
                        String text = element.getText();
                        if (text.contains("var(")) {
                            System.out.println("✅ Text contains var(");
                            return true;
                        }

                        return false;
                    }
                });

        // Ajouter le provider pour le pattern
        extend(CompletionType.BASIC, varFunctionPattern, new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters,
                                          @NotNull ProcessingContext context,
                                          @NotNull CompletionResultSet result) {

                System.out.println("🎯 Adding completions for CSS variables");

                // Récupérer les variables CSS depuis CssVarsLoader
                Map<String, String> cssVars = CssVarsLoader.getInstance(parameters.getPosition().getProject())
                        .getAllVariables();

                // Ajouter chaque variable comme élément d'autocomplétion
                for (Map.Entry<String, String> entry : cssVars.entrySet()) {
                    String varName = entry.getKey();
                    String varValue = entry.getValue();

                    LookupElementBuilder element = LookupElementBuilder.create(varName)
                            .withPresentableText(varName)
                            .withTypeText(varValue, true);

                    result.addElement(element);
                }
            }
        });

        // Ajouter un second pattern pour compléter après "var("
        PsiElementPattern.Capture<PsiElement> insideVarPattern = PlatformPatterns.psiElement()
                .inside(PlatformPatterns.psiElement(CssTermList.class)
                        .withParent(PlatformPatterns.psiElement(CssFunction.class)
                                .with(new PatternCondition<>("isVarFunction") {
                                    @Override
                                    public boolean accepts(@NotNull CssFunction function, ProcessingContext context) {
                                        return "var".equals(function.getName());
                                    }
                                })));

        extend(CompletionType.BASIC, insideVarPattern, new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters,
                                          @NotNull ProcessingContext context,
                                          @NotNull CompletionResultSet result) {

                System.out.println("🎯 Adding completions inside var() function");

                // Récupérer les variables CSS depuis CssVarsLoader
                Map<String, String> cssVars = CssVarsLoader.getInstance(parameters.getPosition().getProject())
                        .getAllVariables();

                // Ajouter chaque variable comme élément d'autocomplétion
                for (Map.Entry<String, String> entry : cssVars.entrySet()) {
                    String varName = entry.getKey();
                    String varValue = entry.getValue();

                    LookupElementBuilder element = LookupElementBuilder.create(varName)
                            .withPresentableText(varName)
                            .withTypeText(varValue, true);

                    result.addElement(element);
                }
            }
        });
    }
}