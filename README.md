# Drop-a-Boss

## Goal
My Goal is to create a game, where the user can create there own bosses, and abilities. Start up your server, join with the boyz and fight.

# Current Status
## Version: 2.0
## 2022/12/24
Same as the last update there is nothing to be done in the game right now, players (up to 6 but is variable) can join the game and move around, use the heal and flash ability, staying consistent from before; bosses will be added to fight.
### Launch commands
  - Launching the Server
    - Terminal: Java -jar {path to server jar}
  - Launching the Game
    - Terminal: java -jar --enable-preview {Path to game jar} [Username] [Server IP] [Server Port]
    - Server Port for Normal Operations is '6969'.

    - The game can now be launched with the .jar file. 
      - once launched 3 popups will ask for:
        - Username
        - Server IP
        - Server Port
### In-game Content rn
- Nothing, right now there is nothing to do, I am currently trying to setup the game components, As a player now you can join the game and just walk around, and use the 2 abilities implemented in the game right now.
### Next Update Goals
  - Sprite Updates:
    - Map Sprites: walls, grass
    - Ability Sprits: heal
    - Character Sprite: well the player sprites
  - HUD: The HUD has been implemented
    - well the way it looks
    - add passive effects panel
    - add 'not available' text if the ability is unavailable
  - Map: My Current map is pretty bad so I am going to create 2 new maps
  - New Abilities:
    - Pull Up Squad
      - One Squad car for 4 players will drive up to the caster, the caster can then with 3 other players hop in and drive away
      - Specs (subject to change):
        - The Vehicle will exist for 5 seconds
        - The Vehicle can sit 3 + the driver
        - The Vehicle Starts with 15px/s and slows through its existence
        - after its stand-in duration will:
          - slow each passenger for 2 seconds
    - Quadrum Bombardum
      - immobilizes the caster and zooms out from the caster's view, and lets the caster take 4 shots (subject to change):
        - If a shot hits a target, it will cause damage to the target.
        - If the second shot hits the same target, the target will be immobilized.
        - If the third shot hits the same target, it will cause damage to the target.
        - If the Forth shot hits the same target, it will make the caster invulnerable for a set amount of time.
      - Specs (subject to change):
        - The caster will be immobilized while the ability is active.
        - The first (damage) shot will cause 50 damage to all targets in the way of the shot
        - The second (immobilize) shot will immobilize the entity for 3 seconds.
        - The third (damage) shot will cause 100 damage (double) to all targets in the way of the shot.
        - the fourth (invulnerable) shot will cause the caster to become invulnerable for 3 seconds.
  - Artillery:
    - basic pistol
      - specs (subject to change):
        - Magazines will hold a max of 12 bullets.
        - Entities will hold a max of 30 bullets.
      - note:
        - If at anytime 2 of these bullets intersect, it will cause 500 damage to all entities.
### General Information
  - The game doesn't function properly without the server, to avoid individual players giving themselves buff by changing ability values on their installations the abilities are stored on the server. when a client connects to the server, the server sends the client the abilities, and because of that if a client is launched without a server connection, a player is spawned with no abilities.
  - Added Discord Bot Support
    - /manipulate-health can be used to minipulate the health of an entity.

### Known Bugs
  - If the server and a player are playing on the same system, will really mess with the heal ability:
    - said player will recieve multiple heals even when one heal is casted on them.

### Credits:
  - RyiSnow: https://www.youtube.com/@RyiSnow/featured
    - Alot of my Game's base is based on a tutorial from RyiSnow. Thank you.

#

## Version: 1.0
## 2022/12/02
There is not alot that can be done right now, you can start up your server, join up and walk around, as stated in my goal my plan is to add bosses that can be fought.
### Launch Commands
  - Launching the Server
    - Terminal: Java -jar --enable-preview {path to server jar}
  - Launching the Game
    - Terminal: java -jar --enable-preview {Path to game jar} [Username] [Server IP] [Server Port] [Thread Sleep Delay]
    - Thread Sleep Delay for normal Operations is '15'. (this will be removed)
    - Server Port for Normal Operations is '6969'.
### In game Content rn
  - Nothing, right now there is nothing to do, i am currently trying to setup the game componenets, As a player now you can join the game and just walk around, and use the 2 abilities implemented in the game right now.
### Next Update Goals
  - Add a HUD (Will be done over the next few Updates)
    - Will contain the player's Health, Mana (not Implemented).
    - Within the HUD there going to be 4 slots for abilities.
    - On top of the HUD (in the middle) Passive affects applied to the player
  - Refactor Ability classes (Will be done over the next few Updates)
    - The abilityCarcus.java, and abilityBase will we re-done, it has gotten so confusing it is hard to debug the code, so i will refactor both classes.
    - Ability HEAL: is broken beyond repair, one of the main reasons of the refactoring of the Ability Section.
