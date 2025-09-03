package br.com.nsbots;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.Vector;

import java.util.*;

public class NSBots extends JavaPlugin implements Listener {

    private String bootsName;
    private List<String> bootsLore;
    private Map<Enchantment, Integer> bootsEnchants = new HashMap<>();
    private Map<PotionEffectType, Integer> bootsEffects = new HashMap<>();
    private Material bootsMaterial;

    private double jumpPower = 1.2;
    private double jumpHeight = 0.5;
    private long jumpCooldownMillis = 3000;

    private final Map<UUID, Double> lastY = new HashMap<>();
    private final Map<UUID, Long> jumpCooldown = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfigValues();

        getCommand("nsbots").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("Apenas jogadores podem usar esse comando.");
                    return true;
                }
                Player player = (Player) sender;
                giveBoots(player);
                player.sendMessage(ChatColor.GREEN + "Você recebeu as " + bootsName + ChatColor.GREEN + " no inventário!");
                return true;
            }
        });

        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("NSBots habilitado.");
    }

    @Override
    public void onDisable() {
        getLogger().info("NSBots desabilitado.");
    }

    private void loadConfigValues() {
        FileConfiguration config = getConfig();

        bootsName = ChatColor.translateAlternateColorCodes('&', config.getString("boots.name", "&bBOTAS DO NSBOT"));

        bootsLore = new ArrayList<>();
        for (String line : config.getStringList("boots.lore")) {
            bootsLore.add(ChatColor.translateAlternateColorCodes('&', line));
        }

        bootsEnchants.clear();
        if (config.isConfigurationSection("boots.enchantments")) {
            for (String key : config.getConfigurationSection("boots.enchantments").getKeys(false)) {
                Enchantment enchant = Enchantment.getByName(key.toUpperCase());
                int level = config.getInt("boots.enchantments." + key);
                if (enchant != null) {
                    bootsEnchants.put(enchant, level);
                }
            }
        }

        bootsEffects.clear();
        if (config.isConfigurationSection("boots.effects")) {
            for (String key : config.getConfigurationSection("boots.effects").getKeys(false)) {
                PotionEffectType type = PotionEffectType.getByName(key.toUpperCase());
                int level = config.getInt("boots.effects." + key);
                if (type != null) {
                    bootsEffects.put(type, level);
                }
            }
        }

        String materialName = config.getString("boots.material", "LEATHER_BOOTS").toUpperCase();
        try {
            bootsMaterial = Material.valueOf(materialName);
        } catch (IllegalArgumentException e) {
            getLogger().warning("Material inválido no config.yml: " + materialName + ". Usando LEATHER_BOOTS.");
            bootsMaterial = Material.LEATHER_BOOTS;
        }

        jumpPower = config.getDouble("jump.power", 1.2);
        jumpHeight = config.getDouble("jump.height", 0.5);
        jumpCooldownMillis = config.getLong("jump.cooldown", 3000);
    }

    private void giveBoots(Player player) {
        ItemStack boots = new ItemStack(bootsMaterial, 1);
        ItemMeta meta = boots.getItemMeta();
        if (meta == null) return;

        meta.setDisplayName(bootsName);
        meta.setLore(bootsLore);

        for (Map.Entry<Enchantment, Integer> entry : bootsEnchants.entrySet()) {
            boots.addUnsafeEnchantment(entry.getKey(), entry.getValue());
        }

        boots.setItemMeta(meta);
        player.getInventory().addItem(boots);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        ItemStack boots = player.getInventory().getBoots();

        if (boots == null || boots.getType() == Material.AIR || !boots.hasItemMeta()) return;

        ItemMeta meta = boots.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;

        if (!ChatColor.stripColor(meta.getDisplayName()).equals(ChatColor.stripColor(bootsName))) return;

        double currentY = player.getLocation().getY();
        double previousY = lastY.getOrDefault(uuid, currentY);
        lastY.put(uuid, currentY);

        long now = System.currentTimeMillis();
        long lastJump = jumpCooldown.getOrDefault(uuid, 0L);

        boolean isJumping = currentY > previousY + 0.2 && player.getVelocity().getY() > 0;

        if (isJumping && now - lastJump >= jumpCooldownMillis) {
            Vector direction = player.getLocation().getDirection().normalize();
            direction.setY(jumpHeight);
            direction.multiply(jumpPower);
            player.setVelocity(direction);
            player.getWorld().playSound(player.getLocation(), Sound.ENDERDRAGON_WINGS, 1f, 1f);
            jumpCooldown.put(uuid, now);
        }

        for (Map.Entry<PotionEffectType, Integer> entry : bootsEffects.entrySet()) {
            applyEffect(player, entry.getKey(), entry.getValue(), 100);
        }
    }

    private void applyEffect(Player player, PotionEffectType type, int amplifier, int duration) {
        PotionEffect current = null;
        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (effect.getType().equals(type)) {
                current = effect;
                break;
            }
        }

        if (current == null || current.getAmplifier() < amplifier || current.getDuration() < duration / 2) {
            player.addPotionEffect(new PotionEffect(type, duration, amplifier, true, false));
        }
    }
}
