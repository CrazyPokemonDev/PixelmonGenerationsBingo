package de.crazypokemondev.pixelmongenerations.bingo;

import com.lypaka.lypakautils.ConfigurationLoaders.BasicConfigManager;
import com.lypaka.lypakautils.ConfigurationLoaders.ConfigUtils;
import com.lypaka.lypakautils.ConfigurationLoaders.PlayerConfigManager;
import de.crazypokemondev.pixelmongenerations.bingo.common.config.PixelmonBingoConfig;
import de.crazypokemondev.pixelmongenerations.bingo.common.items.ModItems;
import de.crazypokemondev.pixelmongenerations.bingo.common.listeners.CaptureListener;
import de.crazypokemondev.pixelmongenerations.bingo.common.loot.LootTables;
import de.crazypokemondev.pixelmongenerations.bingo.network.BingoPacketHandler;
import de.crazypokemondev.pixelmongenerations.bingo.proxy.CommonProxy;
import net.minecraft.command.CommandBase;
import de.crazypokemondev.pixelmongenerations.bingo.server.commands.Commands;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Mod(modid = PixelmonBingoMod.MOD_ID, name = PixelmonBingoMod.NAME, version = PixelmonBingoMod.VERSION,
        dependencies = "required-after:pixelmon@[8.7.1,);required-after:lypakautils@[0.0.2,);")
public class PixelmonBingoMod
{
    public static final String MOD_ID = "pixelmongenerationsbingo";
    public static final String NAME = "Pixelmon Bingo";
    public static final String VERSION = "0.0.1";
    @SidedProxy(clientSide = "de.crazypokemondev.pixelmongenerations.bingo.proxy.ClientProxy",
            serverSide = "de.crazypokemondev.pixelmongenerations.bingo.proxy.ServerProxy")
    public static CommonProxy proxy;
    public static BasicConfigManager configManager;
    public static PlayerConfigManager bingoCardManager;

    public static Logger LOGGER;

    @Mod.Instance
    public static PixelmonBingoMod INSTANCE;

    public PixelmonBingoMod() {
        MinecraftForge.EVENT_BUS.register(new ModItems());
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) throws IOException, ObjectMappingException {
        LOGGER = event.getModLog();

        BingoPacketHandler.registerMessages();

        Path dir = ConfigUtils.checkDir(Paths.get("./config/" + MOD_ID));
        String[] files = new String[]{"pixelmonbingo.conf"};
        configManager = new BasicConfigManager(files, dir, PixelmonBingoMod.class, NAME, MOD_ID, LOGGER);
        configManager.init();
        PixelmonBingoConfig.load(configManager);

        bingoCardManager = new PlayerConfigManager("card.conf", "player-cards",
                dir, PixelmonBingoMod.class, NAME, MOD_ID, LOGGER);
        bingoCardManager.init();

        LootTables.registerAll();
    }

    @EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        for (CommandBase command : Commands.getCommandList()) {
            event.registerServerCommand(command);
        }
        MinecraftForge.EVENT_BUS.register(new CaptureListener());
    }
}
