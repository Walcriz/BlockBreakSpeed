import be.seeseemelk.mockbukkit.Coordinate;
import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.inventory.ItemFactoryMock;
import me.walcriz.blockbreakspeed.Config;
import me.walcriz.blockbreakspeed.EffectValues;
import me.walcriz.blockbreakspeed.Main;
import me.walcriz.blockbreakspeed.block.BlockConfig;
import me.walcriz.blockbreakspeed.block.BlockInfo;
import me.walcriz.blockbreakspeed.block.BlockManager;
import me.walcriz.blockbreakspeed.block.Hardness;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

import static org.junit.Assert.*;

public class BlockBreakSpeedTests {
    private ServerMock server;
    private Main plugin;

    // NOTE: These unit tests wont start. Paper changed their plugin loading mechanism and MockBukkit is broken because of it.
    @Before
    public void before() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Main.class, true);
        plugin.reloadBlockConfigs();
    }

    @Test
    public void canLoad() {
        plugin.mockConfig(new Config());
        List<BlockConfig> blockConfigs =  plugin.mockBlockConfigs(
                new String[]{ "helditem{type=diamond_pickaxe;value=1}" },
                new String[]{ "start{command=say something}" },
                new Hardness(20, 10, 30),
                Material.STONE);

        assertEquals(1, blockConfigs.size());

        BlockConfig config = blockConfigs.get(0);
        assertEquals(Material.STONE, config.getMaterial());

        assertFalse(config.cancelBreakEvent);
        BlockInfo info = config.getBlockInfo();

        assertEquals(1, info.modifierMap().size());
        assertEquals(1, info.triggerMap().size());

        BlockManager manager = BlockManager.getInstance();
        assertTrue(manager.contains(Material.STONE));
    }

    @Test
    public void canCalculate() {
        Player player = server.addPlayer();
        player.setGameMode(GameMode.SURVIVAL);

        PlayerInventory inventory = player.getInventory();
        ItemStack itemStack = server.getItemFactory().createItemStack("minecraft:diamond_pickaxe");
        inventory.setItemInMainHand(itemStack);

        plugin.mockConfig(new Config());

        List<BlockConfig> blockConfigs =  plugin.mockBlockConfigs(
                new String[]{ "helditem{type=diamond_pickaxe;value=12}" },
                new String[]{ "start{command=say something}" },
                new Hardness(20, 10, 30),
                Material.OAK_WOOD);

        BlockManager manager = BlockManager.getInstance();
        BlockConfig config = blockConfigs.get(0);

        WorldMock world = server.addSimpleWorld("test_world");
        Block block = world.getBlockAt(0, 0,0 );

        BlockData data = server.createBlockData(Material.OAK_WOOD);
        block.setBlockData(data);

        EffectValues effectValues = config.getEffectValues(manager.getModifierMap(Material.OAK_WOOD), player, itemStack, block);
        assertNotNull(effectValues);

        System.out.println(effectValues);
    }

    @After
    public void after() {
        MockBukkit.unmock();
    }
}
