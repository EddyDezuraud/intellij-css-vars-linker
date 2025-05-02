package io.cssvarslinker;

import com.intellij.psi.PsiElement;
import com.intellij.util.indexing.ID;
import com.intellij.util.indexing.StringStubIndexExtension;
import org.jetbrains.annotations.NotNull;

public class CssVarsIndex extends StringStubIndexExtension<Void> {
    public static final ID<String, Void> KEY = ID.create("css.vars.index");

    @NotNull
    @Override
    public ID<String, Void> getName() {
        return KEY;
    }
}