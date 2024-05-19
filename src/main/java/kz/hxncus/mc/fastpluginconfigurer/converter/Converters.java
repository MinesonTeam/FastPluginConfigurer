package kz.hxncus.mc.fastpluginconfigurer.converter;

import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import lombok.Getter;

@Getter
public enum Converters {
    ZMENU("zmenu", FastPluginConfigurer.getInstance().getHookManager().getZMenuHook()),
    CHESTCOMMANDS("chestcommands", FastPluginConfigurer.getInstance().getHookManager().getChestCommandsHook()),
    DELUXEMENUS("deluxemenus", FastPluginConfigurer.getInstance().getHookManager().getDeluxeMenusHook()),
    BETTERGUI("bettergui", FastPluginConfigurer.getInstance().getHookManager().getBetterGUIHook());

    private final String name;
    private final Convertible converter;
    Converters(String name, Convertible converter) {
        this.name = name;
        this.converter = converter;
    }
}
