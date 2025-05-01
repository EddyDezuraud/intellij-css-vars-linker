package io.cssvarslinker;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CssVariableReference extends PsiReferenceBase<PsiElement> {
    private final String variableName;

    public CssVariableReference(@NotNull PsiElement element) {
        super(element);
        this.variableName = extractVariableName(element.getText());
    }

    private String extractVariableName(String text) {
        // Extraire le nom de la variable CSS (--xxx)
        int start = text.indexOf("--");
        if (start == -1) return "";

        // Déterminer la fin du nom de la variable
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

    @Override
    public @Nullable PsiElement resolve() {
        if (variableName.isEmpty()) return null;

        System.out.println("🔍 Resolving CSS variable: " + variableName);

        // Utiliser CssVarsLoader pour trouver la déclaration de la variable
        PsiElement declaration = CssVarsLoader.getInstance(getElement().getProject())
                .findDeclaration(variableName);

        if (declaration != null) {
            System.out.println("✅ Found declaration for: " + variableName);
            return declaration;
        }

        System.out.println("❌ Could not find declaration for: " + variableName);
        return null;
    }

    @Override
    public TextRange getRangeInElement() {
        String text = getElement().getText();
        int start = text.indexOf("--");
        if (start == -1) return TextRange.EMPTY_RANGE;

        int end = start + variableName.length();
        return new TextRange(start, end);
    }

    @Override
    public boolean isSoft() {
        // Return false to make IntelliJ show an error when the reference can't be resolved
        // Return true to make IntelliJ ignore unresolved references
        return false;
    }
}