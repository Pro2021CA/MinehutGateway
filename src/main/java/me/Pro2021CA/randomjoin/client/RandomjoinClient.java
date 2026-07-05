package me.Pro2021CA.randomjoin.client;


import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.Pro2021CA.randomjoin.renderGui;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ChestMenu;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static me.Pro2021CA.randomjoin.jsonReader.getMinekeepServers;
import static me.Pro2021CA.randomjoin.jsonReader.getServers;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;


public class RandomjoinClient implements ClientModInitializer {
    public static final String MOD_ID = "minehutgateway";
    public static List<String> servers;

    @Override
    public void onInitializeClient() {
        KeyMapping.Category category = KeyMapping.Category.register(net.minecraft.resources.Identifier.fromNamespaceAndPath(MOD_ID, "gui"));
        KeyMapping togglerender = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.minehutgateway.toggle", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_EQUAL, category));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (togglerender.consumeClick()){
                client.setScreen(new renderGui(Component.empty()));
            }
        });


        ClientCommandRegistrationCallback.EVENT.register(((commandDispatcher, commandRegistryAccess) -> {
            LiteralCommandNode<FabricClientCommandSource> register = commandDispatcher.register(
                    literal("joinrandom")
                            .then(argument("search", StringArgumentType.string()).executes(commandContext -> {
                                String search = StringArgumentType.getString(commandContext, "search");
                                Minecraft client = Minecraft.getInstance();
                                servers = new ArrayList<>();
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
                            .then(argument("search", StringArgumentType.string())
                                    .then(argument("server", StringArgumentType.string()).executes(commandContext -> {
                                        String server = StringArgumentType.getString(commandContext, "server");
                                        String search = StringArgumentType.getString(commandContext, "search");
                                        Minecraft client = Minecraft.getInstance();
                                        servers = new ArrayList<>();
                                        try {
                                            if(server.equals("minehut")){
                                                servers = getServers("https://api.minehut.com/servers?q=" + search);
                                            }else if(server.equals("minekeep")){
                                                servers = getMinekeepServers("https://api.minekeep.net/v1/servers", search);
                                            }
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                        if(servers.isEmpty()){
                                            client.player.displayClientMessage(Component.literal("No servers found!"), false);
                                            return 0;
                                        }
                                        renderGui.page = 1;
                                        client.schedule(() -> {
                                            client.setScreen(new renderGui(Component.empty()));
                                        });
                                        return 1;
                            }))));

        }));
    }

}
