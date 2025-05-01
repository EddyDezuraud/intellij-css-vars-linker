package io.cssvarslinker;

import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

public class CssVariableReferenceProvider extends PsiReferenceProvider {
    @Override
    public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        System.out.println("🟢 getReferencesByElement → " + element.getText() + " (" + element.getClass().getSimpleName() + ")");

        String text = element.getText();
        if (!text.contains("--")) return PsiReference.EMPTY_ARRAY;

        return new PsiReference[]{
                new CssVariableReference(element)
        };
    }
}