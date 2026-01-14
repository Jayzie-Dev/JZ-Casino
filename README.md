# All-in Casino

A production-grade casino plugin for Minecraft servers featuring Slot Machine, Dice, and Blackjack games.

## Features

### Games

**Slot Machine**
- 3-reel slot system with weighted RNG
- 9 configurable symbols with custom multipliers
- Animated spinning with progressive reel stops
- Configurable house edge

**Dice Game**
- High-roll vs dealer mechanics
- Configurable win multipliers
- Delayed reveal animation
- Adjustable house edge

**Blackjack**
- Classic 21 gameplay with lite rules
- Interactive Hit and Stand buttons
- Dealer AI (stands on 17)
- Blackjack detection with 3:2 payout
- Smart Ace counting (11 or 1)
- Push handling for ties

### Core Features

- Direct EssentialsX economy integration
- Anti-exploit protection (cooldowns, bet limits, disconnect protection)
- Fully configurable (symbols, multipliers, house edge, messages)
- GUI-based gameplay with smooth animations
- Thread-safe RNG implementation
- Synchronous economy transactions
- Lightweight (64KB)

## Requirements

- Minecraft 1.20.4 - 1.21.10+ (Paper/Spigot)
- Java 17 or higher
- EssentialsX

## Installation

1. Download `JZ-casino-1.0.0.jar`
2. Place in `plugins/` folder
3. Ensure EssentialsX is installed
4. Restart server
5. Configure `plugins/AllInCasino/config.yml` (optional)
6. Use `/casino` to play

## Commands

| Command | Permission | Description |
|---------|-----------|-------------|
| `/casino` | `casino.use` | Open casino menu |
| `/casinoadmin` | `casino.admin` | Admin dashboard (coming soon) |

## Permissions

| Permission | Default | Description |
|-----------|---------|-------------|
| `casino.use` | true | Access to casino games |
| `casino.slot` | true | Access to slot machine |
| `casino.dice` | true | Access to dice game |
| `casino.blackjack` | true | Access to blackjack |
| `casino.admin` | op | Admin access |

## Configuration

### Basic Settings

```yaml
casino:
  cooldown: 5              # Seconds between games
  min-bet: 10.0            # Minimum bet amount
  max-bet: 1000000000.0    # Maximum bet amount
```

### Slot Machine

```yaml
slot:
  enabled: true
  house-edge: 5.0          # House advantage percentage
  animation:
    duration: 60           # Animation length in ticks
    frame-delay: 3         # Ticks between frames
  symbols:
    JACKPOT:
      weight: 2            # Probability weight (lower = rarer)
      multiplier: 20.0     # Payout multiplier
      display-name: "Jackpot"
```

### Dice Game

```yaml
dice:
  enabled: true
  win-multiplier: 2.0      # Payout multiplier on win
  house-edge: 2.0          # House advantage percentage
  reveal-delay: 40         # Delay before showing result (ticks)
```

### Blackjack

```yaml
blackjack:
  enabled: true
  win-multiplier: 1.0      # Regular win payout
  blackjack-multiplier: 1.5 # Blackjack payout (3:2)
  dealer-stand: 17         # Dealer stands on this value
  house-edge: 1.0          # House advantage percentage
```

## Building

```bash
./gradlew clean build
```

Output: `build/libs/JZ-casino-1.0.0.jar`

## Technical Details

### RNG System
- Uses `ThreadLocalRandom` for thread-safety
- Weighted random selection for slot symbols
- No predictable patterns
- Server-safe implementation

### Economy Integration
- Direct EssentialsX API integration
- All transactions are synchronous
- Balance verification before withdrawal
- Automatic refund on disconnect
- Proper error handling

### Anti-Exploit Protection
- Per-player cooldown system
- Min/max bet enforcement
- GUI inventory locking during games
- One game at a time per player
- Disconnect protection with refunds

### Performance
- Lightweight (64KB)
- Minimal server impact
- Efficient O(1) lookups
- No database required
- Optimized scheduler usage

## Project Structure

```
me.jz.casino/
├── CasinoPlugin.java          # Main plugin class
├── economy/
│   ├── EconomyProvider.java   # Economy interface
│   └── EssentialsEconomyProvider.java
├── manager/
│   ├── CasinoManager.java     # Central game manager
│   ├── CooldownManager.java   # Per-player cooldowns
│   └── PlayerDataManager.java # Active game tracking
├── game/
│   ├── CasinoGame.java        # Base game class
│   ├── slot/                  # Slot machine implementation
│   ├── dice/                  # Dice game implementation
│   └── blackjack/             # Blackjack implementation
├── gui/                       # GUI implementations
├── listener/                  # Event handlers
├── util/                      # Utility classes
└── config/                    # Configuration manager
```

## Roadmap

### V2
- Enhanced animations and effects
- Sound effects and particles
- Improved bet input system

### V3
- Admin dashboard with live statistics
- Multiple casino tables
- NPC dealer integration
- Per-table configuration
- Player statistics tracking

## License

All rights reserved.

## Support

For issues, suggestions, or contributions, please open an issue on GitHub.

---

**Version:** 1.0.0  
**Author:** JZ  
**Minecraft Version:** 1.20.4 - 1.21.10+  
**API:** Paper/Spigot
