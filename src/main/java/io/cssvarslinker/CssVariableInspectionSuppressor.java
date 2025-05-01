package io.cssvarslinker;

import com.intellij.codeInspection.InspectionSuppressor;
import com.intellij.codeInspection.SuppressQuickFix;
import com.intellij.css.util.CssPsiUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.css.CssElement;
import com.intellij.psi.css.CssTermList;
import com.intellij.psi.css.CssFunction;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CssVariableInspectionSuppressor implements InspectionSuppressor {

    @Override
    public boolean isSuppressedFor(@NotNull PsiElement element, @NotNull String inspectionId) {
        System.out.println("🧯 Suppression check: " + inspectionId + " on " + element.getText() + " [" + element.getClass().getSimpleName() + "]");

        // Vérifier si l'élément contient une variable CSS
        boolean containsVariable = element.getText().contains("--");

        // Vérifier si nous sommes dans une fonction var()
        boolean isInVarFunction = isInVarFunction(element);

        // Liste des inspections à supprimer pour les variables CSS
        boolean isTargetedInspection =
                inspectionId.equals("CssUnknownProperty") ||
                        inspectionId.equals("CssUnknownCustomProperty") ||
                        inspectionId.equals("CssInvalidCustomProperty") ||
                        inspectionId.equals("CssInvalidFunction") ||
                        inspectionId.equals("CssInvalidPropertyValue") ||
                        inspectionId.equals("CssResolveError") ||
                        inspectionId.equals("unknown.css.custom.property") ||
                        inspectionId.contains("customproperty") ||
                        (inspectionId.contains("css") && inspectionId.contains("custom") && inspectionId.contains("property"));

        boolean shouldSuppress = containsVariable && isTargetedInspection;
        if (shouldSuppress) {
            System.out.println("✅ Suppressing inspection: " + inspectionId + " for " + element.getText());
        }

        return shouldSuppress;
    }

    private boolean isInVarFunction(PsiElement element) {
        // Vérifier si l'élément est dans une fonction var()
        CssFunction function = PsiTreeUtil.getParentOfType(element, CssFunction.class);
        if (function != null) {
            String functionName = function.getName();
            return "var".equals(functionName);
        }

        // Vérifier les parents pour trouver une fonction var()
        PsiElement parent = element.getParent();
        while (parent != null) {
            if (parent instanceof CssFunction && "var".equals(((CssFunction) parent).getName())) {
                return true;
            }
            if (parent.getText().startsWith("var(--")) {
                return true;
            }
            parent = parent.getParent();
        }

        return false;
    }

    @Override
    public SuppressQuickFix @NotNull [] getSuppressActions(@Nullable PsiElement element, @NotNull String inspectionId) {
        return SuppressQuickFix.EMPTY_ARRAY;
    }
}