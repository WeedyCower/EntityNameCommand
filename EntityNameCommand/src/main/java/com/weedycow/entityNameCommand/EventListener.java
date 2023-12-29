package com.weedycow.entityNameCommand;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

public class EventListener implements Listener
{
    FileConfiguration config;

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event)
    {
        // 如果不是主手，则直接返回
        if (event.getHand() != EquipmentSlot.HAND) return;

        config = Main.main.getConfig();

        Player player = event.getPlayer();

        Entity clickedEntity = event.getRightClicked();

        handle(player, clickedEntity);
    }

    private void handle(Player player, Entity clickedEntity)
    {
        // 检查实体所在的世界是否允许
        if (isWorldAllowed(player))
        {
            // 检查实体的名字是否包含关键词
            String entityName = clickedEntity.getName();

            String keyword = containsKeyword(entityName);

            if (keyword!=null)
            {
                List<String> commands = getCommandForKeyword(keyword);

                boolean executeAsConsole = shouldExecuteAsConsole(keyword);

                //遍历指令集
                for(String command : commands)
                {

                    if (command.contains(getSubKeyword()))
                    {
                        command = command.replace(getSubKeyword(), keyword);
                    }

                    if (command.contains(getSubPlayername()))
                    {
                        command = command.replace(getSubPlayername(), player.getName());
                    }
                    //玩家or后台
                    CommandSender senderToUse = executeAsConsole ? Bukkit.getConsoleSender() : player;

                    Bukkit.getServer().dispatchCommand(senderToUse, command);
                }
            }
        }
    }

    private boolean isWorldAllowed(Player player)
    {
        List<String> allowedWorlds = config.getStringList("allowed_worlds");

        return allowedWorlds.contains(player.getWorld().getName());
    }

    private String containsKeyword(String entityName)
    {
        List<String> keywords = config.getStringList("entity_keywords");

        for (String keyword : keywords)
        {
            if (entityName.contains(keyword))
            {
                return keyword;
            }
        }

        return null;
    }

    private List<String> getCommandForKeyword(String keyword)
    {
        return config.getStringList("commands." + keyword + ".command");
    }

    private boolean shouldExecuteAsConsole(String keyword)
    {
        return config.getBoolean("commands." + keyword + ".execute_as_console");
    }

    private String getSubKeyword()
    {
        return config.getString("sub_keyword");
    }

    private String getSubPlayername()
    {
        return config.getString("sub_playername");
    }
}
