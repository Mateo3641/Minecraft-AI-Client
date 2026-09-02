package dev.bluehell.aihelpermc.managers;

import java.util.HashMap;
import java.util.UUID;

public class CooldownManager {

    private final HashMap<String, Long> cooldowns = new HashMap<>();

    public void setCooldown(UUID uuid, String comando, int segundos){
        String key = uuid.toString() + "_" + comando;
        cooldowns.put(key, System.currentTimeMillis() + (segundos * 1000L));
    }
    public boolean isOnCooldown(UUID uuid,String command){
        String key= uuid.toString()+"_"+command;
        if(cooldowns.containsKey(key)){
            return System.currentTimeMillis()< cooldowns.get(key);
        }
        return false;
    }
    public int getCooldown(UUID uuid, String command){
        String key = uuid.toString() + "_" + command;

        // 1. Si no existe en la lista, significa que le faltan 0 segundos
        if(!cooldowns.containsKey(key)){
            return 0;
        }
        // 2. Sacamos su hora de expiración y miramos el reloj
        long horaDeExpiracion = cooldowns.get(key);
        long horaActual = System.currentTimeMillis();
        // 3. Restamos para ver cuántos milisegundos le faltan
        long milisegundosFaltantes = horaDeExpiracion - horaActual;
        // 4. Si por alguna razón el tiempo ya pasó y dio negativo, devolvemos 0
        if (milisegundosFaltantes <= 0) {
            return 0;
        }
        // 5. Lo dividimos entre 1000 para convertirlo a segundos enteros
        return (int) (milisegundosFaltantes / 1000L);
    }
}
