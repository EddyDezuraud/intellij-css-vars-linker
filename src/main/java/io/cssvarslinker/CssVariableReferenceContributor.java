package io.cssvarslinker;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import org.jetbrains.annotations.NotNull;

public class CssVariableReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(
                PlatformPatterns.psiElement()
                        .withText(StandardPatterns.string().contains("--"))
                        .withParent(StandardPatterns.instanceOf(PsiElement.class)
                                .with(new CssFunctionClassNameCondition())),
                new CssVariableReferenceProvider()
        );
    }
}