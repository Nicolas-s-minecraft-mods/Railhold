# Railhold design

Railhold is a mod for Minecraft 1.21.1 (NeoForge). It makes trains of minecarts, with chain links, locomotives and storage minecarts.

## State

The mod loads in the game. It has no function yet. All the sections after this one are **planned work**: they come from the open issues. The features move from the module `balance` of Folkhold (#3), with the same behavior.

## Planned features

- **Chain links**: a chain links two minecarts. The linked minecarts move as one train.
- **Locomotives**: the furnace locomotive and the heavy locomotive pull a train.
- **Storage minecarts**: minecarts with a double chest, with a shulker box and with an ender chest.
- **Fuel display**: the player sees the fuel of a locomotive.
- **Chunk loading**: a server option lets the trains keep their chunks loaded.

## Planned compatibility

Railhold has its own mod id, so the config file, the registry names and the saved data have new names. A world of Folkhold Balance must keep its items and entities. To do this, the mod reads the old names (data fixer or aliases), or the release notes give the steps and say what is lost.
