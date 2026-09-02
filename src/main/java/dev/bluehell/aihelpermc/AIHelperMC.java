package dev.bluehell.aihelpermc;

import dev.bluehell.aihelpermc.commands.AskCommand;
import dev.bluehell.aihelpermc.managers.CooldownManager;
import dev.bluehell.aihelpermc.services.AiApiService;
import org.bukkit.plugin.java.JavaPlugin;

public final class AIHelperMC extends JavaPlugin {

    private CooldownManager cooldownManager=new CooldownManager();
    @Override
    public void onEnable() {
        saveDefaultConfig();
        String secretoNode = getConfig().getString("api-secret");
        int segundosCooldown = getConfig().getInt("cooldown-seconds");
        String mensajeCooldown = getConfig().getString("mensajes.cooldown");
        AiApiService aiApiService = new AiApiService(this,secretoNode);
        AskCommand comandoIA = new AskCommand(aiApiService, cooldownManager, mensajeCooldown, segundosCooldown);
        this.getCommand("ia").setExecutor(comandoIA);
        getLogger().info("¡AIHelperMC encendido correctamente!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
