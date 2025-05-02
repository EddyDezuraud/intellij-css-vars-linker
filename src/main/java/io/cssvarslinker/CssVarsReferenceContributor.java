package io.cssvarslinker;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

public class CssVarsReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(
                PlatformPatterns.psiElement(PsiElement.class),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element,
                                                                 @NotNull ProcessingContext context) {
                        String text = element.getText();
                        if (text != null && text.startsWith("--")) {
                            return new PsiReference[]{new CssVarsReference(element)};
                        }
                        return PsiReference.EMPTY_ARRAY;
                    }
                });
    }
}