package org.battleplugins.arena.module.diamondbank;

import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.event.action.EventAction;
import org.battleplugins.arena.resolver.Resolvable;

import java.util.Map;
import java.util.Optional;

public class EditDiamondsAction extends EventAction {
    private static final String AMOUNT_KEY = "amount";
    private static final String LEGACY_BANK_KEY = "bank";

    public EditDiamondsAction(Map<String, String> params) {
        super(params, AMOUNT_KEY);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
        if (!arenaPlayer.getArena().isModuleEnabled(DiamondBankIntegration.ID)) {
            return;
        }

        Optional<DiamondBankIntegration> moduleOpt = arenaPlayer.getArena().getPlugin()
                .<DiamondBankIntegration>module(DiamondBankIntegration.ID)
                .map(module -> module.initializer(DiamondBankIntegration.class));

        // No DiamondBank module (should never happen)
        if (moduleOpt.isEmpty()) {
            return;
        }

        DiamondBankContainer container = moduleOpt.get().getDiamondBankContainer();
        if (container == null || !container.isAvailable()) {
            return;
        }

        // DiamondBank-OG has no multi-economy 'bank' equivalent; warn loudly instead of silently ignoring stale configs.
        if (this.get(LEGACY_BANK_KEY) != null) {
            arenaPlayer.getArena().getPlugin().warn("The 'bank' parameter on the 'edit-diamonds' action is not supported by DiamondBank-OG and will be ignored.");
        }

        long shards;
        try {
            shards = DiamondAmounts.parseToShards(this.get(AMOUNT_KEY));
        } catch (IllegalArgumentException e) {
            arenaPlayer.getArena().getPlugin().warn("Invalid 'amount' for the 'edit-diamonds' action: {}", e.getMessage());
            return;
        }

        container.editCurrency(arenaPlayer.getPlayer(), shards);
    }
}
