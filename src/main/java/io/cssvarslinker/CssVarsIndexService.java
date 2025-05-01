package io.cssvarslinker;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Service(Service.Level.PROJECT)
public final class CssVarsIndexService {
    private final Project project;
    private final MessageBusConnection connection;

    public CssVarsIndexService(Project project) {
        this.project = project;
        this.connection = project.getMessageBus().connect();

        // S'abonner aux modifications de fichiers pour recharger l'index si besoin
        this.connection.subscribe(VirtualFileManager.VFS_CHANGES, new BulkFileListener() {
            @Override
            public void after(@NotNull List<? extends VFileEvent> events) {
                for (VFileEvent event : events) {
                    VirtualFile file = event.getFile();
                    if (file != null) {
                        // Si le fichier .cssvarsconfig est modifié, recharger l'index
                        if (file.getName().equals(".cssvarsconfig")) {
                            reloadIndex();
                            return;
                        }

                        // Si un fichier CSS référencé est modifié, recharger l'index
                        if (isReferencedCssFile(file)) {
                            reloadIndex();
                            return;
                        }
                    }
                }
            }
        });
    }

    private boolean isReferencedCssFile(VirtualFile file) {
        // À implémenter: vérifier si le fichier est référencé dans .cssvarsconfig
        // Pour l'instant, on recharge si c'est un fichier CSS
        return file.getExtension() != null && file.getExtension().equalsIgnoreCase("css");
    }

    private void reloadIndex() {
        // Réinitialiser le loader de variables CSS
        CssVarsLoader loader = CssVarsLoader.getInstance(project);
        try {
            // On utilise la reflection pour accéder à une méthode privée
            java.lang.reflect.Method resetMethod = loader.getClass().getDeclaredMethod("reset");
            resetMethod.setAccessible(true);
            resetMethod.invoke(loader);
        } catch (Exception e) {
            System.out.println("❌ Could not reset CSS vars loader: " + e.getMessage());
        }
    }

    // Activité de démarrage pour indexer les variables CSS dès l'ouverture du projet
    public static class IndexStartupActivity implements StartupActivity.DumbAware {
        @Override
        public void runActivity(@NotNull Project project) {
            // S'assurer que le service est créé et initialisé
            project.getService(CssVarsIndexService.class);

            // Forcer le chargement des variables CSS
            CssVarsLoader.getInstance(project);
        }
    }
}