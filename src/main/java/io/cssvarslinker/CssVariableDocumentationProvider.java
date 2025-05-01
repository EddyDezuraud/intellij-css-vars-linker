package io.cssvarslinker;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class CssVariableDocumentationProvider extends AbstractDocumentationProvider {

    @Override
    public @Nullable String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        if (originalElement == null) return null;

        String text = originalElement.getText();
        if (!text.contains("--")) return null;

        // Extraire le nom de la variable
        String varName = extractVariableName(text);
        if (varName.isEmpty()) return null;

        // Récupérer la valeur de la variable
        Map<String, String> variables = CssVarsLoader.getInstance(element.getProject()).getAllVariables();
        String value = variables.get(varName);

        if (value != null) {
            return generateDocumentation(varName, value);
        }

        return null;
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

    private String generateDocumentation(String varName, String value) {
        return "<div class='definition'>" +
                "<pre>" + varName + ": " + value + ";</pre>" +
                "</div>" +
                "<div class='content'>" +
                "<p>CSS Custom Property définie dans les fichiers externes.</p>" +
                "</div>";
    }
}