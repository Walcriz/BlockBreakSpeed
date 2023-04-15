package me.walcriz.blockbreakspeed;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerDigType;
import me.walcriz.blockbreakspeed.block.BlockConfig;
import me.walcriz.blockbreakspeed.block.BlockManager;
import me.walcriz.blockbreakspeed.block.state.BreakModifierMap;
import me.walcriz.blockbreakspeed.block.trigger.TriggerType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Logger;

public class PlayerListener implements Listener {
    private Map<Player, MiningStatus> playersMining = new HashMap<>();

    private int checkTask = -1;
    private Logger logger = Main.getInstance().getLogger();

    public PlayerListener() {
        // Setup protocol lib listeners
        ProtocolManager manager = Main.getProtocolManager();
        manager.addPacketListener(new PacketAdapter(
                Main.getInstance(),
                ListenerPriority.NORMAL,
                PacketType.Play.Client.BLOCK_DIG
        ) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> { // Run on synchronous thread
                    onDigging(event);
                });
            }
        });
    }

    private void onDigging(PacketEvent event) {
        PacketContainer packet = event.getPacket();

        Player player = event.getPlayer();
        PlayerDigType status = packet.getPlayerDigTypes().read(0);
        BlockPosition blockLocation = packet.getBlockPositionModifier().read(0);

        Location location = blockLocation.toLocation(player.getWorld());
        Block block = location.getBlock();

        BlockManager manager = BlockManager.getInstance();
        if (!manager.contains(block.getType()))
            return;

        if (status == PlayerDigType.START_DESTROY_BLOCK) {
            startMining(player, block);
        } else {
            abortMining(player, block);
        }
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (!player.getGameMode().equals(GameMode.SURVIVAL))
            return;

        BlockManager manager = BlockManager.getInstance();
        if (!manager.contains(block.getType()))
            return;

        event.setInstaBreak(false); // Disable insta-break for blocks like mushroom blocks

        BlockConfig config = manager.getBlockConfig(block.getType());
        BreakModifierMap map = manager.getModifierMap(block.getType());

        // See if you should insta-break the block
        if (config.getHardness().calculateHardnessTicks(map, player) > 0)
            return;

        // Then set insta-break to true
        event.setInstaBreak(true);
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {

        if (!event.getPlayer().getGameMode().equals(GameMode.SURVIVAL))
            return;

        BlockManager manager = BlockManager.getInstance();
        Material blockType = event.getBlock().getType();
        if (!manager.contains(blockType))
            return;


        if (!playersMining.containsKey(event.getPlayer()))
            return;

        // Disable drops if wanted
        BlockConfig config = manager.getBlockConfig(blockType);
        event.setDropItems(!config.doSuppressDrops());
        breakBlock(event.getPlayer(), event.getBlock());
    }

    public void startMining(Player player, Block block) {
        MiningStatus status = new MiningStatus(player, block, System.currentTimeMillis());
        playersMining.put(player, status);
        applyEffects(player, status);

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
        removeEffects(player);
        executeTriggers(TriggerType.Stop, player, block);
    }

    private void executeTriggers(TriggerType type, Player player, Block block) {
        BlockManager.getInstance().executeTriggers(player, block, type);
    }

    public void applyEffects(Player player, MiningStatus status) {
        EffectValues values = status.getEffectValues();

        PotionEffect hasteEffect = new PotionEffect(PotionEffectType.FAST_DIGGING, -1, values.hasteValue, false, false, false);
        PotionEffect fatigueEffect = new PotionEffect(PotionEffectType.SLOW_DIGGING, -1, values.fatigueValue, false, false, false);

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
        public long lastMinedTime; // Just ignore please
        public int ticksSinceLastAnimation = 0;

        private BlockConfig hardness;

        public MiningStatus(Player player, Block block, long lastMinedTime) {
            this.player = player;
            this.block = block;
            this.lastMinedTime = lastMinedTime;

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
