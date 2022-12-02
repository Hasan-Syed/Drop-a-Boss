# Drop-a-Boss

## Goal
My Goal is to create a game, where the user can create there own bosses, and abilities. Start up your server, join with the boyz and fight.

# Current Status
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
