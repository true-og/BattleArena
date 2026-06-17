package org.battleplugins.arena.module.diamondbank;

import org.battleplugins.arena.BattleArena;
import org.battleplugins.arena.event.BattleArenaPostInitializeEvent;
import org.battleplugins.arena.event.action.EventActionType;
import org.battleplugins.arena.module.ArenaModule;
import org.battleplugins.arena.module.ArenaModuleInitializer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

/**
 * A module that allows for hooking into the DiamondBank-OG economy plugin.
 */
@ArenaModule(id = DiamondBankIntegration.ID, name = "DiamondBank-OG", description = "Adds support for hooking into the DiamondBank-OG economy plugin.", authors = "TrueOG Network")
public class DiamondBankIntegration implements ArenaModuleInitializer {
    public static final String ID = "diamondbank";

    public static final EventActionType<EditDiamondsAction> EDIT_DIAMONDS_ACTION = EventActionType.create("edit-diamonds", EditDiamondsAction.class, EditDiamondsAction::new);

    private DiamondBankContainer diamondBankContainer;

    @EventHandler
    public void onPostInitialize(BattleArenaPostInitializeEvent event) {
        // Check that DiamondBank-OG is installed
        if (!Bukkit.getServer().getPluginManager().isPluginEnabled("DiamondBank-OG")) {
            event.getBattleArena().module(DiamondBankIntegration.ID).ifPresent(container ->
                    container.disable("DiamondBank-OG is required for the DiamondBank integration module to work!")
            );

            return;
        }

        this.diamondBankContainer = new DiamondBankContainer(BattleArena.getInstance());
    }

    public DiamondBankContainer getDiamondBankContainer() {
        return this.diamondBankContainer;
    }
}
