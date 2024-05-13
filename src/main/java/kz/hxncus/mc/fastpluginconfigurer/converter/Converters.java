package kz.hxncus.mc.fastpluginconfigurer.converter;

import kz.hxncus.mc.fastpluginconfigurer.FastPluginConfigurer;
import lombok.Getter;

@Getter
public enum Converters {
    ZMENU("zmenu", FastPluginConfigurer.getInstance().getZMenuHook()),
    CHESTCOMMANDS("chestcommands", FastPluginConfigurer.getInstance().getChestCommandsHook()),
    DELUXEMENUS("deluxemenus", FastPluginConfigurer.getInstance().getDeluxeMenusHook()),
    BETTERGUI("bettergui", FastPluginConfigurer.getInstance().getBetterguiHook());

    private final String name;
    private final Convertible converter;
    Converters(String name, Convertible converter) {
        this.name = name;
        this.converter = converter;
    }
}
