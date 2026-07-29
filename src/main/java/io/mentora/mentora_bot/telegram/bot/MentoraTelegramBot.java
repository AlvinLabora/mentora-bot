package io.mentora.mentora_bot.telegram.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import io.mentora.mentora_bot.config.TelegramBotConnectionServiceProps;
import io.mentora.mentora_bot.telegram.service.TelegramCommandService;

@SuppressWarnings("deprecation")
@Component
public class MentoraTelegramBot extends TelegramLongPollingBot {

	@Autowired
	private TelegramBotConnectionServiceProps props;

	@Autowired
	private TelegramCommandService commandService;

	@Override
	public void onUpdateReceived(Update update) {
	    SendMessage response = commandService.handle(update);

	    if (response != null) {
	        try {
	            execute(response);
	        } catch (TelegramApiException e) {
	            e.printStackTrace();
	        }
	    }
	}

	@Override
	public String getBotUsername() {
		return props.getTelegramUsername();
	}

	@Override
	public String getBotToken() {
		return props.getTelemgramToken();
	}

}
