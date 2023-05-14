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
import me.walcriz.blockbreakspeed.block.BlockDatabase;
import me.walcriz.blockbreakspeed.block.state.StateModifierMap;
import me.walcriz.blockbreakspeed.block.trigger.TriggerType;
import me.walcriz.blockbreakspeed.utils.StringHelpers;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Logger;

public class PlayerListener implements Listener {
    private Map<UUID, MiningStatus> playersMining = new HashMap<>();

    private BukkitTask checkTask;
    private final Logger logger = Main.getPluginLogger();

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
                onDigging(event);
            }
        });

        // Start animation task
        // runAnimationTask();
    }

    // region Block Breaking Logic
    private void onDigging(PacketEvent event) {

        PacketContainer packet = event.getPacket();

        Player player = event.getPlayer();
        PlayerDigType status = packet.getPlayerDigTypes().read(0);
        BlockPosition blockLocation = packet.getBlockPositionModifier().read(0);

        StringHelpers.debugPlayerMsg(event.getPlayer(), "Digging(" + status.name() + ")");

        Location location = blockLocation.toLocation(player.getWorld());
        Block block = location.getBlock();

        BlockDatabase manager = BlockDatabase.getInstance();
        if (!manager.contains(block))
            return;

        switch (status) {
            case START_DESTROY_BLOCK -> {
                MiningStatus miningStatus = playersMining.getOrDefault(player.getUniqueId(), null);
                if (miningStatus != null) {
                    if (miningStatus.didStop) {
                        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> startMining(player, block), 1);
                        return;
                    }
                    return;
                }

                Bukkit.getScheduler().runTask(Main.getInstance(), () -> { // Run on synchronous thread
                    startMining(player, block);
                });
            }
            case SWAP_HELD_ITEMS, ABORT_DESTROY_BLOCK -> {
                MiningStatus miningStatus = playersMining.getOrDefault(player.getUniqueId(), null);
                if (miningStatus != null)
                    miningStatus.didStop = true;

                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> { // Run on synchronous thread
                    abortMining(player, block);
                }, 1);
            }
            default -> {
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> { // Run on synchronous thread
                    abortMining(player, block);
                });
            }
        }
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
        StringHelpers.debugPlayerMsg(event.getPlayer(), "Block Damage");

        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (!player.getGameMode().equals(GameMode.SURVIVAL))
            return;

        BlockDatabase manager = BlockDatabase.getInstance();
        if (!manager.contains(block))
            return;

        event.setInstaBreak(false); // Disable insta-break for blocks like mushroom blocks

        BlockConfig config = manager.getBlockConfig(block);
        StateModifierMap map = config.getBlockInfo().modifierMap();

        // See if you should insta-break the block
        if (config.getHardness().calculateHardnessTicks(map, player) > 0)
            return;

        // Then set insta-break to true
        event.setInstaBreak(true);
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        StringHelpers.debugPlayerMsg(event.getPlayer(), "Break Block");

        if (!event.getPlayer().getGameMode().equals(GameMode.SURVIVAL))
            return;

        BlockDatabase manager = BlockDatabase.getInstance();
        Block block = event.getBlock();
        if (!manager.contains(block))
            return;


        Player player = event.getPlayer();
        MiningStatus status = playersMining.getOrDefault(player.getUniqueId(), null);
        if (status == null)
            return;

        if (status.didStop) {
            status.didStop = false;
            abortMining(player, block);
            startMining(player, block);
            event.setCancelled(true);
            return;
        }

        // Disable drops if wanted
        BlockConfig config = manager.getBlockConfig(block);
        event.setDropItems(!config.doSuppressDrops());
        breakBlock(event.getPlayer(), event.getBlock());
    }

    public void startMining(Player player, Block block) {
        MiningStatus status = new MiningStatus(player, block);
        playersMining.put(player.getUniqueId(), status);
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
        MiningStatus status = playersMining.get(player.getUniqueId());
        if (status == null)
            return;

        status.applyOldPotionEffects();

        playersMining.remove(player.getUniqueId());
        executeTriggers(TriggerType.Stop, player, block);
    }

    private void executeTriggers(TriggerType type, Player player, Block block) {
        BlockDatabase.getInstance().executeTriggers(player, block, type);
    }

    public void applyEffects(Player player, MiningStatus status) {
        EffectValues values = status.getEffectValues();

        var oldFastEffect = status.fastDiggingEffect;
        var oldSlowEffect = status.slowDiggingEffect;

        PotionEffect hasteEffect = new PotionEffect(PotionEffectType.FAST_DIGGING, -1,
                values.hasteValue, false,
                oldFastEffect != null && oldFastEffect.hasParticles(),
                oldFastEffect != null && oldFastEffect.hasIcon());

        PotionEffect fatigueEffect = new PotionEffect(PotionEffectType.SLOW_DIGGING, -1,
                values.fatigueValue, false,
                oldSlowEffect != null && oldSlowEffect.hasParticles(),
                oldSlowEffect != null && oldSlowEffect.hasIcon());

        player.addPotionEffect(hasteEffect);
        player.addPotionEffect(fatigueEffect);
    }

    public void removeAllEffects() {
        playersMining.forEach((player, status) -> {
            status.applyOldPotionEffects();
        });

        playersMining = new HashMap<>();
    }
    // endregion

    // region Animation
    public void playAnimation(Player player) {
        System.out.println("Play Animation");
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.ANIMATION);
        packetContainer.getIntegers().write(0, player.getEntityId());
        packetContainer.getIntegers().write(1, 0);
        try {
            Main.getProtocolManager().sendServerPacket(player, packetContainer);
        } catch (InvocationTargetException e) {
            logger.warning("Could not send animation packet for player: " + player.getName());
            e.printStackTrace();
        }
    }

    int repeatTime = Main.getConfiguration().animationTaskRepeatTime;
    int animationTime = Main.getConfiguration().animationDelay;
    public void runAnimationTask() {
        if (Main.getConfiguration().disableAnimations)
            return;

        checkTask = Bukkit.getScheduler().runTaskTimerAsynchronously(
                Main.getInstance(), this::animationTask,
                repeatTime, repeatTime);
    }

    public void animationTask() {
        Collection<MiningStatus> statuses = playersMining.values();
        for (MiningStatus status : statuses) {
            if (!status.usePacketAnimations)
                return;

            status.ticksSinceLastAnimation += repeatTime;
            if (status.ticksSinceLastAnimation >= animationTime) {
                playAnimation(status.player);
            }
        }
    }

    public void cancelAnimationTask() {
        if (checkTask == null)
            return;

        checkTask.cancel();
        checkTask = null;
    }
    // endregion

    public static class MiningStatus {
        public Player player;
        public Block block;
        public int ticksSinceLastAnimation = 0;
        public boolean didStop = false;
        public boolean usePacketAnimations = false;

        PotionEffect slowDiggingEffect;
        PotionEffect fastDiggingEffect;

        private final BlockConfig config;

        public MiningStatus(Player player, Block block) {
            this.player = player;
            this.block = block;

            // slowDiggingEffect = player.getPotionEffect(PotionEffectType.SLOW_DIGGING);
            // fastDiggingEffect = player.getPotionEffect(PotionEffectType.FAST_DIGGING);

            BlockDatabase manager = BlockDatabase.getInstance();
            config = manager.getBlockConfig(block);
        }

        public void applyOldPotionEffects() {
            removePotionEffects();

            if (slowDiggingEffect != null)
                player.addPotionEffect(slowDiggingEffect);
            if (fastDiggingEffect != null)
                player.addPotionEffect(fastDiggingEffect);
        }

        public void removePotionEffects() {
            player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
            player.removePotionEffect(PotionEffectType.FAST_DIGGING);
        }

        public EffectValues getEffectValues() {
            StateModifierMap modifierMap = BlockDatabase.getInstance().getModifierMap(block);
            EffectValues values = config.getEffectValues(modifierMap, player, getHeldItem(), block);
            usePacketAnimations = values.usePacketAnimations;
            return values;
        }

        private ItemStack getHeldItem() {
            PlayerInventory inventory = player.getInventory();
            return inventory.getItem(inventory.getHeldItemSlot());
        }
    }
}
