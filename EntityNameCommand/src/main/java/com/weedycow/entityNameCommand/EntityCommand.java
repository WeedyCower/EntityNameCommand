package com.weedycow.entityNameCommand;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityCommand implements CommandExecutor
{
    FileConfiguration config;
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(!command.getName().equals("enc")) return false;

        //OP默认拥有权限
        if (!sender.hasPermission("enc.use") || !sender.isOp())
        {
            sender.sendMessage(ChatColor.RED + "你没有执行此命令的权限！");
            return false;
        }

        //enc查看帮助
        if(args.length<1)
        {
            sender.sendMessage(ChatColor.GREEN + "查看帮助：enc\n重载插件：enc rld\n查看允许的世界：enc wds\n查看已添加关键词：enc kwd\n添加允许的世界：enc awd [世界名]\n删除允许的世界：enc dwd [世界名]\n添加关键词：enc akw [c/p] [关键词] [命令]\n（其中c为控制台执行，p为玩家执行；指令可输入多个，指令若含有空格用下划线_代替） \n删除关键词：enc dkw [关键词]\n设置关键词替换符：enc sks [替换符]\n设置玩家名替换符：enc sps [替换符]");
        }
        else
        {
            config = Main.main.getConfig();

            switch (args[0])
            {
                case "rld":
                    // 重载插件配置文件
                    Main.main.reloadConfig();
                    Main.main.saveConfig();
                    sender.sendMessage(ChatColor.GREEN + "配置文件已重载。");
                    return true;
                case "wds":
                    // 列出能使用的世界
                    List<String> allowedWorlds = config.getStringList("allowed_worlds");
                    sender.sendMessage(ChatColor.GREEN + "能使用的世界列表: " + String.join(", ", allowedWorlds));
                    return true;
                case "kwd":
                    // 列出已加入的关键词、对应命令和是否后台执行
                    for (String keyword : config.getConfigurationSection("commands").getKeys(false))
                    {
                        String commandString = config.getString("commands." + keyword + ".command");
                        boolean executeAsConsole = config.getBoolean("commands." + keyword + ".execute_as_console");
                        sender.sendMessage(ChatColor.GREEN + "关键词: " + keyword +
                                ", 命令: " + commandString +
                                ", 是否后台执行: " + executeAsConsole);
                    }
                    return true;
                case "awd":
                    // 添加可用世界
                    if (args.length == 2)
                    {
                        String worldName = args[1];
                        addWorld(worldName);
                        sender.sendMessage(ChatColor.GREEN + "已添加可用世界: " + worldName);
                    }
                    else
                    {
                        sender.sendMessage(ChatColor.RED + "请提供一个世界的名称。");
                    }
                    return true;
                case "dwd":
                    // 删除可用世界
                    if (args.length == 2)
                    {
                        String worldName = args[1];
                        delWorld(worldName);
                        sender.sendMessage(ChatColor.GREEN + "已删除可用世界: " + worldName);
                    }
                    else
                    {
                        sender.sendMessage(ChatColor.RED + "请提供一个世界的名称。");
                    }
                    return true;
                case "akw":
                    // 添加关键词
                    if (args.length >= 4)
                    {
                        boolean executeAsConsole = args[1].equals("c");

                        String keyword = args[2];

                        List<String> commands = new ArrayList<>(Arrays.asList(args).subList(3, args.length));

                        List<String> modifiedCommands = new ArrayList<>();

                        // 替换每个命令中的下划线
                        for (String cmd : commands)
                        {
                            modifiedCommands.add(cmd.replaceAll("_", " "));
                        }

                        addKeyword(executeAsConsole, keyword, modifiedCommands);

                        sender.sendMessage(ChatColor.GREEN + "已添加关键词: " + keyword);
                    }
                    else
                    {
                        sender.sendMessage(ChatColor.RED + "请提供关键词、命令和是否后台执行的参数。");
                    }
                    return true;
                case "dkw":
                    //删除关键词
                    if (args.length == 2)
                    {
                        String keyword = args[1];
                        delKeyword(keyword);
                        sender.sendMessage(ChatColor.GREEN + "已删除关键词: " + keyword);
                    }
                    else
                    {
                        sender.sendMessage(ChatColor.RED + "请提供要删除的关键词。");
                    }
                    return true;
                case "sks":
                    //设置关键词替换符
                    if (args.length == 2)
                    {
                        String sub = args[1];
                        setSubKeyword(sub);
                        sender.sendMessage(ChatColor.GREEN + "已设置关键词替换符: " + sub);
                    }
                    else
                    {
                        sender.sendMessage(ChatColor.RED + "请提供一个关键词替换符。");
                    }
                    return true;
                case "sps":
                    //设置玩家名替换符
                    if (args.length == 2)
                    {
                        String sub = args[1];
                        setSubPlayername(sub);
                        sender.sendMessage(ChatColor.GREEN + "已设置玩家名替换符: " + sub);
                    }
                    else
                    {
                        sender.sendMessage(ChatColor.RED + "请提供一个玩家名替换符。");
                    }
                    return true;
                default:
                    sender.sendMessage(ChatColor.RED + "命令输入错误！请输入/enc查看指令列表！");
                    break;
            }
        }
        return false;
    }

    private void delWorld(String worldName)
    {
        List<String> allowedWorlds = config.getStringList("allowed_worlds");
        if(allowedWorlds.contains(worldName))
        {
            allowedWorlds.remove(worldName);
            config.set("allowed_worlds", allowedWorlds);
        }
        Main.main.saveConfig();
    }

    private void addWorld(String worldName)
    {
        // 添加可用世界到配置文件
        List<String> allowedWorlds = config.getStringList("allowed_worlds");
        allowedWorlds.add(worldName);
        config.set("allowed_worlds", allowedWorlds);
        Main.main.saveConfig();
    }

    private void delKeyword(String keyword)
    {
        List<String> keyWords = config.getStringList("entity_keywords");
        if(keyWords.contains(keyword))
        {
            keyWords.remove(keyword);
            config.set("entity_keywords", keyWords);
            config.set("commands." + keyword, null);
        }
        Main.main.saveConfig();
    }

    private void addKeyword( boolean executeAsConsole, String keyword, List<String> commands)
    {
        // 添加关键词及其对应命令和是否后台执行到配置文件
        List<String> entityKeywords = config.getStringList("entity_keywords");
        entityKeywords.add(keyword);
        config.set("entity_keywords", entityKeywords);
        config.set("commands." + keyword + ".command", commands);
        config.set("commands." + keyword + ".execute_as_console", executeAsConsole);
        Main.main.saveConfig();
    }

    private void setSubKeyword(String sub)
    {
        config.set("sub_keyword", sub);
        Main.main.saveConfig();
    }

    private void setSubPlayername(String name)
    {
        config.set("sub_playername", name);
        Main.main.saveConfig();
    }
}
