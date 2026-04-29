package org.battleplugins.arena.module.luckperms;

import org.battleplugins.arena.event.BattleArenaPostInitializeEvent;
import org.battleplugins.arena.event.action.EventActionType;
import org.battleplugins.arena.module.ArenaModule;
import org.battleplugins.arena.module.ArenaModuleInitializer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

/**
 * A module that allows for hooking into the LuckPerms plugin.
 */
@ArenaModule(id = LuckPermsIntegration.ID, name = "LuckPerms", description = "Adds support for hooking into the LuckPerms plugin.", authors = "BattlePlugins")
public class LuckPermsIntegration implements ArenaModuleInitializer {
    public static final String ID = "luckperms";

    public static final EventActionType<AddPermissionAction> ADD_PERMISSION_ACTION = EventActionType.create("add-permission", AddPermissionAction.class, AddPermissionAction::new);
    public static final EventActionType<RemovePermissionAction> REMOVE_PERMISSION_ACTION = EventActionType.create("remove-permission", RemovePermissionAction.class, RemovePermissionAction::new);

    private LuckPermsContainer luckPermsContainer;

    @EventHandler
    public void onPostInitialize(BattleArenaPostInitializeEvent event) {
        if (!Bukkit.getServer().getPluginManager().isPluginEnabled("LuckPerms")) {
            event.getBattleArena().module(LuckPermsIntegration.ID).ifPresent(container ->
                    container.disable("LuckPerms is required for the LuckPerms integration module to work!")
            );

            return;
        }

        this.luckPermsContainer = new LuckPermsContainer();
    }

    public LuckPermsContainer getLuckPermsContainer() {
        return this.luckPermsContainer;
    }
}
