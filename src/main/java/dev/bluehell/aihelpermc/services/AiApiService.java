package dev.bluehell.aihelpermc.services;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AiApiService {
    private final Plugin plugin;
    private final String secretoApi;
    public AiApiService(Plugin plugin, String secretoApi){
        this.plugin= plugin;
        this.secretoApi=secretoApi;
    }

    public void makeQuestionAsync(Player player,String question){
        System.out.println("1. Entrando al método makeQuestionAsync...");
        Bukkit.getAsyncScheduler().runNow(plugin,scheduledTask -> {
            System.out.println("2. ¡El Hilo Asíncrono inició correctamente!");
            HttpClient client = HttpClient.newHttpClient();
            String jsonBody = "{\"user\": \"" + player.getName() +
                    "\", \"question\": \"" + question + "\"}";
            System.out.println("3. Intentando conectar a Node.js...");
            HttpRequest request= HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:3000/questions"))
                    .header("Content-Type","application/json")
                    .header("Authorization", "Bearer " + secretoApi)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            try {
                HttpResponse<String> response = client.send(request,
                        HttpResponse.BodyHandlers.ofString());
                // Lo que responda Node.js se guarda aquí:
                String jsonRespuesta = response.body();
                com.google.gson.JsonObject jsonObject =
                        com.google.gson.JsonParser.parseString(jsonRespuesta).getAsJsonObject();
                // 2. Extraemos solo la respuesta de la IA
                String IA_Answer = jsonObject.get("answer").getAsString();
                // 3. Le mandamos el mensaje bonito al jugador
                player.sendMessage("§a[Inteligencia Artificial] §f" + IA_Answer);
            } catch (Exception e) {
                System.out.println("X. ERROR FATAL EN LA CONEXIÓN HTTP:");
                e.printStackTrace();
                player.sendMessage("La IA está apagada, Intenta de nuevo luego.");
                throw new RuntimeException(e);
            }
            System.out.println("Enviando pregunta de a la IA: " + jsonBody);
        });
    }
}