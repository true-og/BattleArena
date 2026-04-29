package org.battleplugins.arena.module.luckperms;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import org.bukkit.entity.Player;

public class LuckPermsContainer {
    private final LuckPerms luckPerms;

    public LuckPermsContainer() {
        this.luckPerms = LuckPermsProvider.get();
    }

    public void addPermission(Player player, String permission, boolean isTransient) {
        if (this.luckPerms == null) {
            return;
        }

        UserManager userManager = this.luckPerms.getUserManager();
        User user = userManager.getUser(player.getUniqueId());
        if (user == null) {
            return;
        }

        Node node = Node.builder(permission).build();
        if (isTransient) {
            user.transientData().add(node);
        } else {
            user.data().add(node);
            userManager.saveUser(user);
        }
    }

    public void removePermission(Player player, String permission, boolean isTransient) {
        if (this.luckPerms == null) {
            return;
        }

        UserManager userManager = this.luckPerms.getUserManager();
        User user = userManager.getUser(player.getUniqueId());
        if (user == null) {
            return;
        }

        Node node = Node.builder(permission).build();
        if (isTransient) {
            user.transientData().remove(node);
        } else {
            user.data().remove(node);
            userManager.saveUser(user);
        }
    }
}
