package io.cssvarslinker;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.css.CssDeclaration;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CssVarsIndexer extends FileBasedIndexExtension<String, Void> {
    public static final ID<String, Void> NAME = CssVarsIndex.KEY;

    @NotNull
    @Override
    public ID<String, Void> getName() {
        return NAME;
    }

    @NotNull
    @Override
    public DataIndexer<String, Void, FileContent> getIndexer() {
        return inputData -> {
            Map<String, Void> result = new HashMap<>();
            PsiFile psiFile = PsiManager.getInstance(inputData.getProject())
                    .findFile(inputData.getFile());
            if (psiFile != null) {
                psiFile.accept(new com.intellij.psi.PsiRecursiveElementVisitor() {
                    @Override
                    public void visitElement(@NotNull com.intellij.psi.PsiElement element) {
                        if (element instanceof CssDeclaration) {
                            String propertyName = ((CssDeclaration) element).getPropertyName();
                            if (propertyName != null && propertyName.startsWith("--")) {
                                result.put(propertyName, null);
                            }
                        }
/integration/explore?sort=best_match&term=PsiRecursiveElementVisitor                        super.visitElement(element);
                    }
                });
            }
            return result;
        };
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return EnumeratorStringDescriptor.INSTANCE;
    }

    @NotNull
    @Override
    public DataExternalizer<Void> getValueExternalizer() {
        return ScalarIndexExtension.VOID_DATA_EXTERNALIZER;
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return file -> {
            if (file.getExtension() == null || !file.getExtension().equalsIgnoreCase("css")) {
                return false;
            }
            Project project = com.intellij.openapi.project.ProjectManager.getInstance().getOpenProjects()[0];
            if (project == null) {
                return false;
            }
            List<VirtualFile> cssFiles = CssVarsConfigReader.getCssFiles(project);
            return cssFiles.contains(file);
        };
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }
}