package dev.bluehell.aihelpermc.commands;

import dev.bluehell.aihelpermc.managers.CooldownManager;
import dev.bluehell.aihelpermc.services.AiApiService;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class AskCommand implements CommandExecutor {
    private final AiApiService aiApiService;
    private final CooldownManager cooldownManager;

    // Las variables inyectadas
    private final String mensajeCooldown;
    private final int segundosCooldown;
    public AskCommand(AiApiService aiApiService, CooldownManager cooldownManager, String mensajeCooldown,
                      int segundosCooldown) {
        if (cooldownManager == null) throw new IllegalArgumentException("El CooldownManager no puede ser nulo");
        if (aiApiService == null) throw new IllegalArgumentException("El AiApiService no puede ser nulo");
        if (mensajeCooldown == null) throw new IllegalArgumentException("El mensaje de cooldown no puede ser nulo");

        this.aiApiService = aiApiService;
        this.cooldownManager = cooldownManager;
        this.mensajeCooldown = mensajeCooldown;
        this.segundosCooldown = segundosCooldown;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        if (!(sender instanceof Player)){
            sender.sendMessage("Este comando solo puede ser utilizado por jugadores.");
            return true;
        }
        if (!player.hasPermission("aihelpermc.ia.use")) {
            // Puedes poner el mensaje en duro así, o inyectarlo desde el Main igual que el otro
            player.sendMessage(ChatColor.RED + "No tienes permiso para usar la Inteligencia Artificial.");
            return true; // Importante el return true para cortar la ejecución
        }
        if (args.length == 0){
            sender.sendMessage("Debes preguntarme algo primero.");
            return true;
        }

        if (cooldownManager.isOnCooldown(uuid, "ia")){
            // Usamos directamente la variable de texto inyectada
            int faltan = cooldownManager.getCooldown(uuid, "ia");
            String mensajeListoSoloFaltaReemplazar = this.mensajeCooldown.replace("%tiempo%", String.valueOf(faltan));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', mensajeListoSoloFaltaReemplazar));

            return true;
        }

        String question = String.join(" ", args);
        if (question.length() > 200) {
            player.sendMessage("Tu pregunta es demasiado larga.");
            return true;
        }
        sender.sendMessage("Has preguntado: " + question + " la IA está procesando espera ...");

        // Anotamos el cooldown usando la variable de número inyectada
        cooldownManager.setCooldown(uuid, "ia", this.segundosCooldown);

        aiApiService.makeQuestionAsync(player, question);
        return true;
    }
}
