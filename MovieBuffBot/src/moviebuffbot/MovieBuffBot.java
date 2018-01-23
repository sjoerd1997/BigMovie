/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moviebuffbot;

import com.rivescript.RiveScript;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;


public class MovieBuffBot extends TelegramLongPollingBot {
    RiveScript bot = new RiveScript();
    
    public MovieBuffBot() {
        super();
        bot.setSubroutine("system", new SystemSubroutine());
        bot.setSubroutine("jdbc", new JdbcSubroutine());
        bot.setSubroutine("send", new SendSubroutine(this));
        bot.setSubroutine("map", new MapSubroutine(this));
        bot.loadDirectory("resources/rivescript");
        bot.sortReplies();
    }
    
    @Override
    @SuppressWarnings("CallToPrintStackTrace")
    public void onUpdateReceived(Update update) {

        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            // Set variables
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();
            
            // Get reply
            String reply = bot.reply(String.valueOf(chat_id), message_text);            

            SendMessage message = new SendMessage() // Create a message object object
                .setChatId(chat_id)
                .setText(reply);
            try {
                execute(message); // Sending our message object to user
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        // Return bot username
        return "willemnator_bot";
    }

    @Override
    public String getBotToken() {
        // Return bot token from BotFather
        return "543131633:AAG--Ycxyx3mnGdXPz91w-nKREMpG00MQBw";
    }

}
