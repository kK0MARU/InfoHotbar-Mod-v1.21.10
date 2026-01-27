package net.starrymoon92.hotbarinfo;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HotBarInfoClient implements ClientModInitializer {
    // Создаем отдельный логгер для клиента, чтобы видеть загрузку
    public static final Logger LOGGER = LoggerFactory.getLogger("hotbarinfo-client");

    @Override
    public void onInitializeClient() {
        LOGGER.info("[HotBarInfo] Client Initialized! Registering HUD renderer...");

        // Регистрируем событие отрисовки интерфейса (HUD)
        HudRenderCallback.EVENT.register(this::onRenderHud);
    }

    private void onRenderHud(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();

        // Проверки: игрок существует? интерфейс не скрыт (F1)?
        if (client.player == null || client.options.hudHidden) {
            return;
        }

        // Если открыт F3, не рисуем, чтобы не было каши
        if (client.getDebugHud().shouldShowDebugHud()) {
            return;
        }

        // Получаем координаты
        int xPos = (int) client.player.getX();
        int yPos = (int) client.player.getY();
        int zPos = (int) client.player.getZ();

        String text = String.format("%d | %d | %d", xPos, yPos, zPos);

        TextRenderer textRenderer = client.textRenderer;
        int screenWidth = context.getScaledWindowWidth();
        int screenHeight = context.getScaledWindowHeight();

        int textWidth = textRenderer.getWidth(text);

        // Позиция X: центр экрана
        int drawX = (screenWidth - textWidth) / 2;

        // Позиция Y: Высота экрана минус отступ.
        // Стандартный хотбар ~22px, плюс отступы.
        // Ставим -70, чтобы быть над полоской опыта.
        int drawY = screenHeight - 70;

        // ВАЖНО: Цвет в формате ARGB.
        // 0xFFFFFFFF = Alpha(255) Red(255) Green(255) Blue(255) -> Непрозрачный белый.
        // Если указать просто 0xFFFFFF, текст будет полностью прозрачным.
        context.drawTextWithShadow(textRenderer, text, drawX, drawY, 0xFFFFFFFF);
    }
}