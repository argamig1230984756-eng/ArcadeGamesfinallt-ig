package me.arcade.games;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.arcade.games.arena.GameType;
import me.arcade.games.game.Game;
import me.arcade.games.game.impl.ArcadeModeGame;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public final class ArcadePlaceholder extends PlaceholderExpansion {
    private final ArcadePlugin plugin;
    public ArcadePlaceholder(ArcadePlugin plugin) { this.plugin = plugin; }
    @Override public @NotNull String getIdentifier() { return "arcadegames"; }
    @Override public @NotNull String getAuthor() { return "NO MERCY"; }
    @Override public @NotNull String getVersion() { return plugin.getDescription().getVersion(); }
    @Override public boolean persist() { return true; }
    @Override public String onRequest(OfflinePlayer player, @NotNull String params) {
        String key = params.toLowerCase(java.util.Locale.ROOT);
        if (key.startsWith("players_")) {
            String gameKey = key.substring("players_".length());
            GameType type = GameType.parse(gameKey);
            if (type == null) return "0";
            int count = 0;
            for (Game game : plugin.manager().all()) if (game.arena().type == type) count += game.playerCount();
            return String.valueOf(count);
        }
        if (player == null) return "0";
        Player online = Bukkit.getPlayer(player.getUniqueId());
        if (online != null) {
            Game game = plugin.manager().gameOf(online);
            if (game != null) {
                switch (key) {
                    case "time", "time_left" -> { return game.remainingTimeText(); }
                    case "alive", "players_left" -> { return String.valueOf(game.aliveCount()); }
                    case "total", "players_total" -> { return String.valueOf(game.totalCount()); }
                    case "coins", "it" -> { if (game instanceof ArcadeModeGame arcade) return arcade.scoreboardValue(online, key); }
                }
            }
        }
        return switch (key) {
            case "wins" -> String.valueOf(plugin.stat(player.getUniqueId(), "wins"));
            case "losses" -> String.valueOf(plugin.stat(player.getUniqueId(), "losses"));
            default -> "";
        };
    }
}
