package me.Pro2021CA.randomjoin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import static me.Pro2021CA.randomjoin.client.RandomjoinClient.servers;

public class renderGui extends Screen {
    public static int page;
    public renderGui(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        int startx = 5;
        int starty = 10;
        int width = 75;
        int max = 500/(width+startx);

        int spacing = startx;
        int counter = max;
        int yrows = 0;
        int maxrows = 8;
        for(int i = (page-1)*max*maxrows; i < page*max*maxrows; i++){
            if(i >= servers.size()){
                if(yrows == 0){
                    page--;
                    minecraft.setScreen(new renderGui(Component.empty()));
                }
                break;
            }
            String server = servers.get(i);
            Button buttonWidget = Button.builder(Component.literal(server), (btn) -> {
                this.minecraft.getToastManager().addToast(
                        SystemToast.multiline(this.minecraft, SystemToast.SystemToastId.NARRATOR_TOGGLE, Component.nullToEmpty(server), Component.nullToEmpty("Connecting to server."))
                );
                minecraft.player.connection.sendCommand("join " + server);
            }).bounds(startx, starty, width, 20).build();
            startx += width + spacing;
            this.addRenderableWidget(buttonWidget);
            counter--;
            if(counter == 0) {
                startx = spacing;
                starty += 20 + spacing;
                counter = max;
                if (yrows == maxrows) {
                    return;
                }
                yrows++;
            }
        }
        Button nextPage = Button.builder(Component.literal("next page"), (btn) -> {
            page++;
            minecraft.setScreen(new renderGui(Component.empty()));
        }).bounds(325, 225, 75, 20).build();
        this.addRenderableWidget(nextPage);
        if(page != 1){
            Button prevPage = Button.builder(Component.literal("previous page"), (btn) -> {
                page--;
                minecraft.setScreen(new renderGui(Component.empty()));
            }).bounds(75, 225, 75, 20).build();
            this.addRenderableWidget(prevPage);
        }

    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);

        // Minecraft doesn't have a "label" widget, so we'll have to draw our own text.
        // We'll subtract the font height from the Y position to make the text appear above the button.
        // Subtracting an extra 10 pixels will give the text some padding.
        // textRenderer, text, x, y, color, hasShadow
        graphics.drawString(this.font, "Click A server to join!", 175, 250 - this.font.lineHeight - 10, 0xFFFFFFFF, true);
    }
}
