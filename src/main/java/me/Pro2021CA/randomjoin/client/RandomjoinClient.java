package me.Pro2021CA.randomjoin.client;


import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static me.Pro2021CA.randomjoin.jsonReader.getServers;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;


public class RandomjoinClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register(((commandDispatcher, commandRegistryAccess) -> {
            LiteralCommandNode<FabricClientCommandSource> register = commandDispatcher.register(
                    literal("joinrandom")
                            .then(argument("search", StringArgumentType.string()).executes(commandContext -> {
                                String search = StringArgumentType.getString(commandContext, "search");
                                Minecraft client = Minecraft.getInstance();
                                List<String> servers = new ArrayList<>();
                                try {
                                    servers = getServers("https://api.minehut.com/servers?q=" + search);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                if(servers.isEmpty()){
                                    client.player.displayClientMessage(Component.literal("No servers found!"), false);
                                    return 0;
                                }
                                Integer size = servers.size();
                                String server = servers.get((int) Math.floor(Math.random()*size));
                                client.player.connection.sendCommand("join " + server);
                                return 1;
                            }))
            );
        }));
        ClientCommandRegistrationCallback.EVENT.register(((commandDispatcher, commandRegistryAccess) -> {
            LiteralCommandNode<FabricClientCommandSource> register = commandDispatcher.register(
                    literal("getservers")
                            .then(argument("search", StringArgumentType.string()).executes(commandContext -> {
                                String search = StringArgumentType.getString(commandContext, "search");
                                Minecraft client = Minecraft.getInstance();
                                List<String> servers = new ArrayList<>();
                                try {
                                    servers = getServers("https://api.minehut.com/servers?q=" + search);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                if(servers.isEmpty()){
                                    client.player.displayClientMessage(Component.literal("No servers found!"), false);
                                    return 0;
                                }
                                client.player.displayClientMessage(Component.literal(servers.toString()), false);
                                return 1;
                            })));

        }));
    }

}
