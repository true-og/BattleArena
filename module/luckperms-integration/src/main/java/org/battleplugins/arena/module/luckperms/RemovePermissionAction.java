package org.battleplugins.arena.module.luckperms;

import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.event.action.EventAction;
import org.battleplugins.arena.resolver.Resolvable;

import java.util.Map;
import java.util.Optional;

public class RemovePermissionAction extends EventAction {
    private static final String PERMISSION_KEY = "permission";
    private static final String TRANSIENT_KEY = "transient";

    public RemovePermissionAction(Map<String, String> params) {
        super(params, PERMISSION_KEY);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
        if (!arenaPlayer.getArena().isModuleEnabled(LuckPermsIntegration.ID)) {
            return;
        }

        Optional<LuckPermsIntegration> moduleOpt = arenaPlayer.getArena().getPlugin()
                .<LuckPermsIntegration>module(LuckPermsIntegration.ID)
                .map(module -> module.initializer(LuckPermsIntegration.class));

        if (moduleOpt.isEmpty()) {
            return;
        }

        LuckPermsIntegration module = moduleOpt.get();
        String permission = this.get(PERMISSION_KEY);
        boolean isTransient = Boolean.parseBoolean(this.getOrDefault(TRANSIENT_KEY, "true"));
        module.getLuckPermsContainer().removePermission(arenaPlayer.getPlayer(), permission, isTransient);
    }
}
