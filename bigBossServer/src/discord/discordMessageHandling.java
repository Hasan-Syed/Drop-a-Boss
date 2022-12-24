package discord;

import org.json.JSONObject;

import clients.player;
import main.App;
import main.enums.abilityAppliar;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class discordMessageHandling extends ListenerAdapter {

    final App mainApp;

    /**
     * Discord Message Handler, handles incoming requests from Discord
     * 
     * @param mainApp main app
     */
    discordMessageHandling(App mainApp) {
        this.mainApp = mainApp;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        // make sure we handle the right command
        switch (event.getName()) {
            case "ping" -> {
                long time = System.currentTimeMillis();
                event.reply("Pong!").setEphemeral(true) // reply or acknowledge
                        .flatMap(v -> event.getHook().editOriginalFormat("Pong: %d ms",
                                System.currentTimeMillis() - time) // then edit original
                        ).queue(); // Queue both reply and edit
            }
            /**
             * Slack Commands
             * This is a force heal Command, generated from the information given with the
             * command on discord.
             * 
             * when using the '/manipulate-health' command on discord, the user supplies:
             * * 1. Entity Type (Role)
             * * 2. Instant Heal (Boolean)
             * * 3. Heal Amount (integer)
             * * 4. Entity ID (integer)
             * 
             * Using these Paramaters a Json object is written for a healObject, that is
             * then sent to the player
             * 
             * *The Heal Duration is always set to 10 seconds
             */
            case "manipulate-health" -> {
                int entityID = event.getOption("entity-id").getAsInt();
                int entityHealth = event.getOption("heal-amount").getAsInt();
                int healDuration = 0;
                JSONObject healShell = new JSONObject();
                JSONObject healInternal = new JSONObject();
                healInternal.put("causedBy", 0);
                if (event.getOption("instant-heal").getAsBoolean()) {
                    healInternal.put("instantHeal", entityHealth);
                    healInternal.put("maxHeal", 0);
                    healInternal.put("healDuration", 1);
                } else {
                    healInternal.put("instantHeal", 0);
                    healInternal.put("maxHeal", entityHealth);
                    try {
                        healDuration = event.getOption("duration").getAsInt();
                        healInternal.put("healDuration", healDuration);
                    } catch (Exception e) {
                        healDuration = 10;
                        healInternal.put("healDuration", healDuration);
                    }
                }
                healInternal.put("causedTo", entityID);
                healInternal.put("stackable", true);
                healShell.put("abilityType", abilityAppliar.healthObject);
                healShell.put("impact", healInternal);
                {
                    boolean clientFound = false;
                    for (player client : mainApp.clientList) {
                        if (client.ID == entityID) {
                            clientFound = true;
                            client.serverCastedAbilities.put(new JSONObject().put("castedAbility", healShell));
                            /**
                             * Sends a reply on Discord; letting the appliar(discord) know that the command
                             * has been sent.
                             */
                            event.reply("Attempting to Manipulate " + client.playerName
                                    + " health bar, the new updated health value is " + entityHealth
                                    + ", over the next "
                                    + healDuration + " seconds").queue();
                        }
                    }
                    if (!clientFound) {
                        event.reply("The Entity wasn't found").queue();
                    }
                }
            }
        }
    }
}
