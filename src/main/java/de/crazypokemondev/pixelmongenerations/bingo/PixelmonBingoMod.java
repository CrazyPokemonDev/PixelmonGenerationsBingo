package de.crazypokemondev.pixelmongenerations.bingo;

import com.lypaka.lypakautils.ConfigurationLoaders.BasicConfigManager;
import com.lypaka.lypakautils.ConfigurationLoaders.ConfigUtils;
import com.lypaka.lypakautils.ConfigurationLoaders.PlayerConfigManager;
import de.crazypokemondev.pixelmongenerations.bingo.common.config.PixelmonBingoConfig;
import de.crazypokemondev.pixelmongenerations.bingo.common.items.ModItems;
import de.crazypokemondev.pixelmongenerations.bingo.common.listeners.CaptureListener;
import de.crazypokemondev.pixelmongenerations.bingo.common.listeners.EntityInteractListener;
import de.crazypokemondev.pixelmongenerations.bingo.common.listeners.LoginListener;
import de.crazypokemondev.pixelmongenerations.bingo.common.loot.LootTables;
import de.crazypokemondev.pixelmongenerations.bingo.network.BingoPacketHandler;
import de.crazypokemondev.pixelmongenerations.bingo.proxy.CommonProxy;
import de.crazypokemondev.pixelmongenerations.bingo.server.commands.Commands;
import net.minecraft.command.CommandBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
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
    public static final String VERSION = "0.1.0";
    @SidedProxy(clientSide = "de.crazypokemondev.pixelmongenerations.bingo.proxy.ClientProxy",
            serverSide = "de.crazypokemondev.pixelmongenerations.bingo.proxy.ServerProxy")
    public static CommonProxy proxy;
    public static BasicConfigManager configManager;
    public static PlayerConfigManager bingoCardManager;
    private final Path configDir;

    public static Logger LOGGER;

    @Mod.Instance
    public static PixelmonBingoMod INSTANCE;

    public PixelmonBingoMod() {
        MinecraftForge.EVENT_BUS.register(new ModItems());
        configDir = ConfigUtils.checkDir(Paths.get("./config/" + MOD_ID));
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) throws IOException, ObjectMappingException {
        LOGGER = event.getModLog();

        BingoPacketHandler.registerMessages();

        String[] files = new String[]{"pixelmonbingo.conf"};
        configManager = new BasicConfigManager(files, configDir, PixelmonBingoMod.class, NAME, MOD_ID, LOGGER);
        configManager.init();
        PixelmonBingoConfig.load(configManager);

        LootTables.registerAll();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new LoginListener());
        MinecraftForge.EVENT_BUS.register(new EntityInteractListener());
    }

    @EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        bingoCardManager = new PlayerConfigManager("card.conf",
                "cards-" + event.getServer().getFolderName(),
                configDir, PixelmonBingoMod.class, NAME, MOD_ID, LOGGER);
        bingoCardManager.init();

        for (CommandBase command : Commands.getCommandList()) {
            event.registerServerCommand(command);
        }
        MinecraftForge.EVENT_BUS.register(new CaptureListener());
    }
}
