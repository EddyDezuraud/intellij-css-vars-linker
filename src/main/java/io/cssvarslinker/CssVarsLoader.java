package io.cssvarslinker;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.css.CssDeclaration;
import com.intellij.psi.css.CssRuleset;
import com.intellij.psi.css.CssStylesheet;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public final class CssVarsLoader {
    private static final Logger LOG = Logger.getInstance(CssVarsLoader.class);
    private static final Pattern CSS_VAR_PATTERN = Pattern.compile("--([\\w-]+)\\s*:\\s*([^;]+);");

    private final Project project;
    private final Map<String, String> cssVariables = new HashMap<>();
    private final Map<String, PsiElement> cssVariableDeclarations = new HashMap<>();
    private boolean isInitialized = false;

    public CssVarsLoader(Project project) {
        this.project = project;
    }

    public static CssVarsLoader getInstance(Project project) {
        CssVarsLoader service = project.getService(CssVarsLoader.class);
        if (!service.isInitialized) {
            service.initialize();
        }
        return service;
    }

    private void initialize() {
        LOG.info("Initializing CSS Variables Loader");

        Collection<VirtualFile> configFileCollection = FilenameIndex.getVirtualFilesByName(
                project,
                ".cssvarsconfig",
                GlobalSearchScope.projectScope(project)
        );
        VirtualFile[] configFiles = configFileCollection.toArray(new VirtualFile[0]);

        if (configFiles.length == 0) {
            LOG.warn("No .cssvarsconfig file found in project");
            isInitialized = true;
            return;
        }

        try {
            VirtualFile configFile = configFiles[0];
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(configFile.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;

                    VirtualFile cssFile = configFile.getParent().findFileByRelativePath(line);
                    if (cssFile != null) {
                        loadCssVarsFromFile(cssFile);
                    } else {
                        LOG.warn("CSS file not found: " + line);
                    }
                }
            }
        } catch (IOException e) {
            LOG.error("Error reading .cssvarsconfig", e);
        }

        isInitialized = true;
        LOG.info("Loaded " + cssVariables.size() + " CSS variables");
    }

    private void loadCssVarsFromFile(VirtualFile cssFile) {
        try {
            String content = new String(cssFile.contentsToByteArray());

            Matcher matcher = CSS_VAR_PATTERN.matcher(content);
            while (matcher.find()) {
                String varName = "--" + matcher.group(1);
                String varValue = matcher.group(2).trim();
                cssVariables.put(varName, varValue);

                PsiFile psiFile = PsiManager.getInstance(project).findFile(cssFile);
                if (psiFile != null) {
                    storeDeclarationFromPsiFile(psiFile, varName);
                }
            }

            LOG.info("Loaded variables from " + cssFile.getName() + ": " + cssVariables.size());

        } catch (IOException e) {
            LOG.error("Error reading CSS file: " + cssFile.getPath(), e);
        }
    }

    private void storeDeclarationFromPsiFile(PsiFile psiFile, String varName) {
        if (psiFile.getFileType().getName().contains("CSS")) {
            CssStylesheet stylesheet = PsiTreeUtil.findChildOfType(psiFile, CssStylesheet.class);
            if (stylesheet != null) {
                CssRuleset[] rulesets = PsiTreeUtil.getChildrenOfType(stylesheet, CssRuleset.class);
                if (rulesets != null) {
                    for (CssRuleset ruleset : rulesets) {
                        CssDeclaration[] declarations = PsiTreeUtil.getChildrenOfType(ruleset, CssDeclaration.class);
                        if (declarations != null) {
                            for (CssDeclaration declaration : declarations) {
                                String propertyName = declaration.getPropertyName();
                                if (propertyName != null && propertyName.equals(varName.substring(2))) {
                                    PsiElement value = declaration.getValue();
                                    if (value != null) {
                                        cssVariableDeclarations.put(varName, declaration);
                                        LOG.info("Stored declaration for " + varName + " = " + value.getText());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public Map<String, String> getAllVariables() {
        return cssVariables;
    }

    @Nullable
    public PsiElement findDeclaration(String varName) {
        return cssVariableDeclarations.get(varName);
    }

    public void reset() {
        LOG.info("Resetting CSS Variables Loader");
        cssVariables.clear();
        cssVariableDeclarations.clear();
        isInitialized = false;
        initialize();
    }
}