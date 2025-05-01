package io.cssvarslinker;

import com.intellij.patterns.PatternCondition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.css.CssFunction;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

public class CssFunctionClassNameCondition extends PatternCondition<PsiElement> {
    public CssFunctionClassNameCondition() {
        super("isInVarFunction");
    }

    @Override
    public boolean accepts(@NotNull PsiElement element, ProcessingContext context) {
        // Chercher une fonction parente
        CssFunction function = PsiTreeUtil.getParentOfType(element, CssFunction.class);
        if (function != null) {
            String name = function.getName();
            boolean isVar = "var".equals(name);
            System.out.println("🔍 CssFunctionClassNameCondition - Function: " + name + ", isVar: " + isVar);
            return isVar;
        }

        return false;
    }
}