/*
 * Made by Liam "ReddChicken" McMenemie.
 * Licence: CC BY-NC-SA 3.0 (http://creativecommons.org/licenses/by-nc-sa/3.0/)
 */

package net.reddchicken.ForcedGrammar;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ForcedGrammar extends JavaPlugin implements Listener {

	public void onEnable() {

		// Check for and generate config. file.
		Boolean configExists = new File(this.getDataFolder(), "config.yml").exists();
		if (!configExists) { saveDefaultConfig(); }
		
		// Register events.
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	@EventHandler
	public void onPLayerChat(PlayerChatEvent event) {
		
		
		//\/\/\/\/ Global Variables \/\/\/\/\\
		
		// Create some variables to work with.
		
		String originalMessage = event.getMessage();
				
		String newMessage = "";
		
		// Statistics code (unused)
		int numCorrections = 0;
		
		
		//\/\/\/\/ Escape Condition \/\/\/\/\\
		
		// Check for escape character, return if found.
		
		if (originalMessage.charAt(0) == '\\') {
			event.setMessage(originalMessage.substring(1));
			return;
		}

		
		//\/\/\/\/\/ Correct Spelling \/\/\/\/\/\\
		
		// Replace incorrect words with their correct spelling.
		
		List<String> words = Arrays.asList(originalMessage.split("\\s"));
		
		// Get spellings from the configuration file.
		Map<String, String> dictionary = new HashMap<String, String>();
		Set<String> keys = getConfig().getKeys(false);
		
		for (String key : keys) {
			String value = getConfig().get(key).toString();
			dictionary.put(key, value);
		}
		
		// Correct words.
		int i = 0;
		for (String word : words) {
			for (String search : dictionary.keySet()) {
				String replace = dictionary.get(search);
				
				if (word.equalsIgnoreCase(search)) {
					words.set(i, replace);
					numCorrections++;
				}
			}
			i++;
		}
		
		// Convert back to string.
		for (String word : words) {
			newMessage += word + " ";
		}
		
		newMessage = newMessage.trim();
		
		
		//\/\/\/\/\/ Capitalise Sentences \/\/\/\/\/\\
		
		// Capitalise the first letter of every sentence.
		
		String[] sentences = newMessage.split("(?<=[!?\\.])\\s");
		String tempMessage = "";
		for (String sentence : sentences) {
			String firstChar = Character.toString(sentence.charAt(0));
			firstChar = firstChar.toUpperCase();
			sentence = sentence.substring(1);
			sentence = firstChar + sentence;
			tempMessage = tempMessage + sentence + " ";
		}
		
		newMessage = tempMessage.trim();
		
		
		//\/\/\/\/\/ Ensure End Punctuation \/\/\/\/\/\\
		
		// Check that ending punctuation is present, stick a full-stop on the end if there isn't.
		
		String[] endings = {".", "!", "?"};
		String lastChar = Character.toString(newMessage.charAt(newMessage.length() - 1)); 
		
		boolean punctuated = false;
		for (String ending : endings) {
			if (lastChar.equals(ending)) {
				getLogger().info("Yes");
				punctuated = true;
			} 
		}
		
		if (!punctuated) {
			newMessage += ".";
			numCorrections++;
		}
		
		
		//\/\/\/\/\/ Set final message. \/\/\/\/\/\\
		
		event.setMessage(newMessage);
	}
	
}
