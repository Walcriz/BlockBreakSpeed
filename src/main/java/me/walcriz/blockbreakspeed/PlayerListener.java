package me.walcriz.blockbreakspeed;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import me.walcriz.blockbreakspeed.block.BlockConfig;
import me.walcriz.blockbreakspeed.block.BlockManager;
import me.walcriz.blockbreakspeed.block.state.BreakModifierMap;
import me.walcriz.blockbreakspeed.block.trigger.TriggerType;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class PlayerListener implements Listener {
    private Map<Player, MiningStatus> playersMining = new HashMap<>();

    private int checkTask = -1;

    @EventHandler
    public void onLeftClickOnBlock(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK)
            return;

        if (event.getClickedBlock() == null)
            return;

        BlockManager manager = BlockManager.getInstance();
        if (!manager.contains(event.getClickedBlock().getType()))
            return;

        if (!playersMining.containsKey(event.getPlayer())) {
            startMining(event.getPlayer(), event.getClickedBlock());
            return;
        }

        MiningStatus status = playersMining.get(event.getPlayer());
        if (status.block.equals(event.getClickedBlock())) {
            status.wasMining = true;
            status.ticksSinceLastAnimation += 1;
            if (status.ticksSinceLastAnimation >= Main.config.animationDelay) {
                playAnimation(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        if (!playersMining.containsKey(event.getPlayer()))
            return;

        MiningStatus status = playersMining.get(event.getPlayer());
        if (!status.block.equals(event.getBlock()))
            return;

        breakBlock(event.getPlayer(), event.getBlock());
    }

    private void tryStartTask() {
        if (checkTask == -1) {
            int delay = Main.config.miningCheckTicks;
            checkTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), () -> {
                playersMining.forEach((p, status) -> { // p: player
                    if (!status.wasMining) {
                        abortMining(p, status.block);
                        return;
                    }

                    status.wasMining = false;
                    applyEffects(p, status);
                });
            }, delay, delay);
        }
    }

    public void startMining(Player player, Block block) {
        MiningStatus status = new MiningStatus(block, true);
        playersMining.put(player, status);
        applyEffects(player, status);
        tryStartTask();
        playAnimation(player);

        executeTriggers(TriggerType.Start, player, block);
    }

    public void abortMining(Player player, Block block) {
        stopMining(player, block);
        executeTriggers(TriggerType.Abort, player, block);
    }

    public void breakBlock(Player player, Block block) {
        stopMining(player, block);
        executeTriggers(TriggerType.Break, player, block);
    }

    public void stopMining(Player player, Block block) {
        playersMining.remove(player);
        removeEffects(player);

        executeTriggers(TriggerType.Stop, player, block);
    }

    private void executeTriggers(TriggerType type, Player player, Block block) {
        BlockManager.getInstance().executeTriggers(player, block, type);
    }

    public void applyEffects(Player player, MiningStatus status) {
        EffectValues values = status.getEffectValues();

        PotionEffect hasteEffect = new PotionEffect(PotionEffectType.FAST_DIGGING, 999, values.hasteValue, false, false, false);
        PotionEffect fatigueEffect = new PotionEffect(PotionEffectType.SLOW_DIGGING, 999, values.fatigueValue, false, false, false);

        player.addPotionEffect(hasteEffect);
        player.addPotionEffect(fatigueEffect);
    }

    public void removeEffects(Player player) {
        player.removePotionEffect(PotionEffectType.FAST_DIGGING);
        player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
    }

    public void removeAllEffects() {
        playersMining.forEach((player, status) -> {
            removeEffects(player);
        });

        playersMining = new HashMap<>();
    }

    public void playAnimation(Player player) {
        if (Main.config.disableAnimations)
            return;

        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.ANIMATION);
        packetContainer.getIntegers().write(0, player.getEntityId());
        packetContainer.getIntegers().write(0, 1);
        try {
            Main.getProtocolManager().sendServerPacket(player, packetContainer);
        } catch (InvocationTargetException e) {
            Main.getInstance().logger.warning("Could not send animation packet for player: " + player.getName());
        }
    }

    public void cancelTask() {
        if (checkTask == -1)
            return;

        Bukkit.getScheduler().cancelTask(checkTask);
        checkTask = -1;
    }

    public static class MiningStatus {
        public Player player;
        public Block block;
        public boolean wasMining; // Just ignore please
        public int ticksSinceLastAnimation = 0;

        private BlockConfig hardness;

        public MiningStatus(Block block, boolean wasMining) {
            this.block = block;
            this.wasMining = wasMining;

            BlockManager manager = BlockManager.getInstance();
            hardness = manager.getBlockConfig(block.getType());
        }

        public EffectValues getEffectValues() {
            BreakModifierMap modifierMap = BlockManager.getInstance().getModifierMap(block.getType());
            return hardness.getEffectValues(modifierMap, player, getHeldItem(), block);
        }

        private ItemStack getHeldItem() {
            PlayerInventory inventory = player.getInventory();
            return inventory.getItem(inventory.getHeldItemSlot());
        }
    }
}
