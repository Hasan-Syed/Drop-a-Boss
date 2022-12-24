package discord;

import org.json.JSONObject;

import main.App;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class discordConnect {
        final App mainApp;
        public JDA discord;
        private JSONObject discordAPIResources;

        /**
         * This Class connects to Discord and Starts a Discord Lisener, the Listener
         * will take commands from discord and apply thing to the server
         * 
         * @param discordAPIResources Discord API Information
         * @param mainApp             Main Class
         */
        public discordConnect(JSONObject discordAPIResources, App mainApp) {
                this.discordAPIResources = discordAPIResources;
                this.mainApp = mainApp;
                initialize();
        }

        /**
         * Initializes the Discord Connect class, and Connects to Discord
         */
        void initialize() {
                discord = (JDA) JDABuilder
                                .createDefault((String) discordAPIResources.getString("token"))
                                .addEventListeners(new discordMessageHandling(mainApp))
                                .build();

                // Sets the global command list to the provided commands (removing all others)
                discord.updateCommands().addCommands(
                                Commands.slash("ping", "Calculate ping of the bot"),
                                // Heal Command Initiator
                                Commands.slash("manipulate-health",
                                                "This command is used to Heal any Entity within the game.")
                                                // Heal Which Entity
                                                .addOption(OptionType.ROLE, "entity-type",
                                                                "The set Role for the Entity, between (@player, or @ai)",
                                                                true)
                                                // If the Heal is a Instant
                                                .addOption(OptionType.BOOLEAN, "instant-heal",
                                                                "It the Heal is going to be instant", true)
                                                // Target's ID
                                                .addOption(OptionType.INTEGER, "entity-id", "The Targert Entities ID",
                                                                true)
                                                // new Health/Health Target
                                                .addOption(OptionType.INTEGER, "heal-amount", "The Heal Amount", true)
                                                // Get heal Duration
                                                .addOption(OptionType.INTEGER, "duration",
                                                                "In Seconds enter the seconds for heal"))
                                .queue();
        }
}
