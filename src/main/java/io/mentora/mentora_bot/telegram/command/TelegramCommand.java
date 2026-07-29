package io.mentora.mentora_bot.telegram.command;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import io.mentora.mentora_bot.telegram.model.TelegramContext;

public interface TelegramCommand {

    String getCommand();

    SendMessage execute(TelegramContext context);
}