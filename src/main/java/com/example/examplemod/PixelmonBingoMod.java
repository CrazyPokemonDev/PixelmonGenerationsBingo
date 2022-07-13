package com.example.examplemod;

import com.lypaka.lypakautils.ConfigurationLoaders.BasicConfigManager;
import com.lypaka.lypakautils.ConfigurationLoaders.ConfigUtils;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Mod(modid = PixelmonBingoMod.MODID, name = PixelmonBingoMod.NAME, version = PixelmonBingoMod.VERSION)
public class PixelmonBingoMod
{
    public static final String MODID = "pixelmongenerationsbingo";
    public static final String NAME = "Pixelmon Bingo";
    public static final String VERSION = "0.0.1";
    public static BasicConfigManager configManager;

    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) throws IOException {
        logger = event.getModLog();
        Path dir = ConfigUtils.checkDir(Paths.get("./config/pixelmonbingo"));
        String[] files = new String[]{"pixelmonbingo.conf"};
        configManager = new BasicConfigManager(files, dir, PixelmonBingoMod.class, NAME, MODID, logger);
        configManager.init();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        // some example code
        logger.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }
}
