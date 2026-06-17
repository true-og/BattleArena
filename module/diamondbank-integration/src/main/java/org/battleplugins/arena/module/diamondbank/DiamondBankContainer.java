package org.battleplugins.arena.module.diamondbank;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.trueog.diamondbankog.DiamondBankException;
import net.trueog.diamondbankog.api.DiamondBankAPIJava;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.logging.Level;

public class DiamondBankContainer {
    private static final String TRANSACTION_REASON = "BattleArena";

    private final Plugin plugin;
    private final DiamondBankAPIJava economy;

    public DiamondBankContainer(Plugin plugin) {
        this.plugin = plugin;
        this.economy = createEconomy();
    }

    /**
     * @return whether the DiamondBank-OG economy service is registered and usable
     */
    public boolean isAvailable() {
        return this.economy != null;
    }

    /**
     * Edits a player's DiamondBank-OG bank balance by the given signed shard
     * amount. A positive amount deposits, a negative amount withdraws (9 shards =
     * 1 diamond).
     *
     * <p>The DiamondBank-OG Java API blocks on the database and on a per-player
     * transaction lock, so the work is dispatched off the main server thread. The
     * call is fire-and-forget: failures are logged (and the player is notified on
     * insufficient funds) but cannot retroactively affect arena flow.
     */
    public void editCurrency(Player player, long shards) {
        if (this.economy == null || shards == 0) {
            return;
        }

        UUID uuid = player.getUniqueId();
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> apply(player, uuid, shards));
    }

    private void apply(Player player, UUID uuid, long shards) {
        try {
            if (shards > 0) {
                this.economy.addToPlayerBankShards(uuid, shards, TRANSACTION_REASON, null);
                return;
            }

            // Pre-check balance: DiamondBank-OG globally disables the economy on an insufficient subtract, so subtract only runs when funds suffice (small TOCTOU window, acceptable for arena fees).
            long needed = -shards;
            long balance = this.economy.getBankShards(uuid);
            if (balance < needed) {
                this.plugin.getLogger().warning("Could not withdraw " + needed + " shard(s) from " + player.getName()
                        + ": insufficient balance (" + balance + " shard(s))");
                player.sendMessage(Component.text("You do not have enough diamonds for this.", NamedTextColor.RED));
                return;
            }

            this.economy.subtractFromPlayerBankShards(uuid, needed, TRANSACTION_REASON, null);
        } catch (DiamondBankException e) {
            this.plugin.getLogger().log(Level.WARNING, "DiamondBank-OG economy error editing currency for "
                    + player.getName() + " (the DiamondBank-OG economy may now be disabled)", e);
        } catch (Exception e) {
            this.plugin.getLogger().log(Level.WARNING, "Unexpected error editing DiamondBank-OG currency for "
                    + player.getName(), e);
        }
    }

    @Nullable
    private static DiamondBankAPIJava createEconomy() {
        RegisteredServiceProvider<DiamondBankAPIJava> rsp = Bukkit.getServer().getServicesManager().getRegistration(DiamondBankAPIJava.class);
        if (rsp == null) {
            return null;
        }

        return rsp.getProvider();
    }
}
