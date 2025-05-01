package io.cssvarslinker;

import com.intellij.openapi.project.Project;

import java.io.File;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CssVarsLoader {

    private static final Pattern CSS_VAR_PATTERN = Pattern.compile("--([a-zA-Z0-9-_]+)\\s*:\\s*[^;]+;");

    public static Set<String> loadCssVariables(Project project) {
        Set<String> variables = new HashSet<>();
        File configFile = new File(project.getBasePath(), ".cssvarsconfig");
        if (!configFile.exists()) return variables;

        try {
            List<String> lines = Files.readAllLines(configFile.toPath());
            for (String line : lines) {
                File cssFile = new File(project.getBasePath(), line.trim());
                if (!cssFile.exists()) continue;

                List<String> content = Files.readAllLines(cssFile.toPath());
                for (String cssLine : content) {
                    Matcher matcher = CSS_VAR_PATTERN.matcher(cssLine);
                    while (matcher.find()) {
                        variables.add(matcher.group(1));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return variables;
    }
}