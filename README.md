# Java Console Mini RPG

A simple console-based RPG adventure game built in Java.  
Each input you enter progresses the game one step forward. Random battles, level-ups, rest sites, and turn-based combat await.

## Gameplay Overview

- Type **anything** (press Enter) to move forward in the world.
- After each input, the game may randomly:
  - Trigger a **battle encounter** (with sound)
  - Level up your character
  - Lead to a **rest site** where you can shop
- Type `status` at any time to view your current stats.
- Type `exit` to quit the game.

### Random Events

| Event | Triggered When |
|-------|----------------|
| **Enemy Encounter** | ~25% chance per input |
| **Rest Site**       | Every 3 battles |
| **Level Up**        | Based on XP after battles |
| **Shop**            | Option available at rest sites |

### ⚔️ Battle System

- Turn-based combat against random enemies
- Choose numbered actions like Attack or Heal
- Each fight ends with XP and gold gain
- Sound plays at the start of each battle

## 🔊 Sound Effect

- File: `EncounterSound.wav`
- Automatically plays when a battle starts
- Must be in the **same directory** as `MiniGame.java`


## 🚀 How to Run

### Requirements
- Java JDK 8 or later

### Run from Terminal

```bash
javac MiniGame.java
java MiniGame

_____________________________
EXAMPLE GAMEPLAY:
_____________________________
Where would you like to go?
> left

You take a step forward...

An enemy appears!
Choose your action:
1. Attack
2. Defend
3. Heal

