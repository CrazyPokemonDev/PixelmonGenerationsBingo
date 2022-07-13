package de.crazypokemondev.pixelmongenerations.bingo;

import com.lypaka.lypakautils.ConfigurationLoaders.BasicConfigManager;
import com.lypaka.lypakautils.ConfigurationLoaders.ConfigUtils;
import de.crazypokemondev.pixelmongenerations.bingo.common.config.PixelmonBingoConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Mod(modid = PixelmonBingoMod.MODID, name = PixelmonBingoMod.NAME, version = PixelmonBingoMod.VERSION,
        dependencies = "required-after:pixelmon@[8.7.1,);required-after:lypakautils@[0.0.2,);")
public class PixelmonBingoMod
{
    public static final String MODID = "pixelmongenerationsbingo";
    public static final String NAME = "Pixelmon Bingo";
    public static final String VERSION = "0.0.1";
    public static BasicConfigManager configManager;

    public static Logger LOGGER;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) throws IOException, ObjectMappingException {
        LOGGER = event.getModLog();
        Path dir = ConfigUtils.checkDir(Paths.get("./config/pixelmonbingo"));
        String[] files = new String[]{"pixelmonbingo.conf"};
        configManager = new BasicConfigManager(files, dir, PixelmonBingoMod.class, NAME, MODID, LOGGER);
        configManager.init();
        PixelmonBingoConfig.load(configManager);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {

    }
}
