package io.cssvarslinker;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CssVarsConfigReader {
    @NotNull
    public static List<VirtualFile> getCssFiles(@NotNull Project project) {
        VirtualFile configFile = project.getBaseDir().findChild(".cssvarsconfig");
        if (configFile == null) {
            return Collections.emptyList();
        }

        try {
            String content = VfsUtil.loadText(configFile);
            JsonObject json = JsonParser.parseString(content).getAsJsonObject();
            JsonArray cssFiles = json.getAsJsonArray("cssFiles");
            List<VirtualFile> files = new ArrayList<>();
            for (JsonElement element : cssFiles) {
                String path = element.getAsString();
                VirtualFile file = VfsUtil.findFileByIoFile(
                        new File(project.getBasePath(), path), true);
                if (file != null && file.exists()) {
                    files.add(file);
                }
            }
            return files;
        } catch (Exception e) {
            // Log error if needed
            return Collections.emptyList();
        }
    }
}