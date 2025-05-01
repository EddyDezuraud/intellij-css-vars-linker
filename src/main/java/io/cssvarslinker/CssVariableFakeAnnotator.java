package io.cssvarslinker;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.css.CssFunction;
import com.intellij.psi.css.CssTerm;
import com.intellij.psi.css.CssTermList;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class CssVariableFakeAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        // Ne pas traiter les éléments qui ne sont pas pertinents
        if (!(element instanceof CssTerm) && !element.getText().contains("--")) {
            return;
        }

        // Vérifier si on est dans une fonction var()
        CssFunction function = PsiTreeUtil.getParentOfType(element, CssFunction.class);
        if (function == null || !"var".equals(function.getName())) {
            return;
        }

        // Extraire le nom de la variable
        String text = element.getText();
        String varName = extractVariableName(text);
        if (varName.isEmpty()) return;

        // Vérifier si la variable existe dans notre index
        Map<String, String> variables = CssVarsLoader.getInstance(element.getProject()).getAllVariables();
        if (variables.containsKey(varName)) {
            // Si la variable existe, supprimer l'erreur en ajoutant une fausse annotation INFO
            // qui masquera visuellement l'erreur d'inspection
            int start = text.indexOf(varName);
            if (start >= 0) {
                TextRange range = new TextRange(element.getTextRange().getStartOffset() + start,
                        element.getTextRange().getStartOffset() + start + varName.length());

                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .range(range)
                        .tooltip("CSS Variable: " + varName + " = " + variables.get(varName))
                        .create();

                System.out.println("✅ Added annotation for: " + varName);
            }
        }
    }

    private String extractVariableName(String text) {
        int start = text.indexOf("--");
        if (start == -1) return "";

        int end = text.length();
        for (int i = start + 2; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == ')' || c == ',' || c == ' ' || c == ';') {
                end = i;
                break;
            }
        }

        return text.substring(start, end);
    }
}