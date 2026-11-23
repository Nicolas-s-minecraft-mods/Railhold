# Railhold design

Railhold is a mod for Minecraft 1.21.1 (NeoForge). It adds trains of minecarts with chain links, locomotives and storage minecarts. The mod needs no other mod.

The rails stay vanilla. The player links minecarts with a chain to make a train. A locomotive of the mod at the front pulls the train. A vanilla furnace minecart pulls or pushes the train from any position in the train.

## Links

- Code: `CartLinks`, `ChainUse`. The player uses the item "chain" on a first minecart, then on a second minecart. The action bar shows the first minecart. The link uses 1 chain. The chain does not start the vanilla interaction (ride, chest menu).
- The first minecart goes in front of the second minecart. When the ends of the two minecarts do not permit this order, the second minecart goes in front.
- A minecart has a maximum of one front link and one back link. A link cannot make a loop. The gap between the two minecarts (see "Train") must be the maximum gap or less. All the minecarts of the two trains must be loaded.
- Shears, or an empty main hand while the player sneaks, remove the front link of a minecart. A minecart with no front link loses its back link. The chain drops. An empty hand without sneak still lets the player ride.
- Each minecart keeps its links in a data attachment: the UUID of the minecart in front and the UUID of the minecart behind. The links stay after a save and a reload of the world.

## Train

- Code: `Trains`, `Coupling`. The game ticks the entities in a random order. Thus the mod moves the trains after the entities, at the end of the tick of each level.
- The mod finds each train one time. Then it corrects the motion of each minecart in order, from the driver of the train to the two ends of the train. The correction of a minecart uses the corrected motion of the next minecart toward the driver. Thus a long train has no delay of one tick for each minecart.
- The driver is the front minecart when it is a locomotive or a vanilla furnace minecart with a push (with fuel). If not, the driver is the first vanilla furnace minecart with a push, also when it is behind its wagons. A vanilla furnace minecart moves in the direction of its push, from the position of the player that gives the fuel.
- Code: `TrainPush`. A train with no driver of these two types follows the minecart that gets a push. Thus a push on any minecart moves all the train.
  - Each tick, the mod keeps the motion of each minecart after the correction of the chains. In the next tick, the speed gain of a minecart is its horizontal speed minus this kept speed.
  - The gain is the change that the vanilla tick gives: a player push, a powered rail, a slope, a rider, a collision. The chains do not cause a gain.
  - The vanilla slowdown and a brake only decrease the speed. A curve changes only the direction. Thus they do not cause a gain.
  - The minecart with the largest gain drives the train for this tick, if this gain is more than 0.001 blocks per tick.
  - Two pushes in opposite directions at rest: the larger push drives the train. The chains then move the other minecart with the train. For two equal pushes, the driver of the last tick drives. If it does not get a push, the minecart nearest to the front of the train drives.
  - If no minecart has a gain, the driver of the last tick drives again. Thus a train that a push started coasts with the same driver. A new train with no push has the front minecart as its driver.
  - A push against the motion of a moving train decreases the speed of its minecart. It is not a gain, and the train ignores it. Thus the larger of two opposite pushes drives only when the train is at rest. It also drives when it makes its minecart faster than before.
  - A train on a slope with no push: the minecarts on the slope down get a gain. The minecart with the largest gain drives. The train goes down the slope. A minecart on the slope up loses speed and does not drive.
  - A brake (a powered rail with no power) decreases the speed of its minecart. It is not a gain, so the brake does not change the driver.
    - If the braked minecart is not the driver, the chains give back to it the speed of the train. The brake slows the train only a little.
    - If the braked minecart is the driver (for example, the minecart that a push started), it stays the driver. The chains then slow the other minecarts to its speed, and the brake stops all the train.
- The break counter of a link has the key of the back minecart of the link. Thus a change of the driver does not change the counter.
- Code: `CartEnds`. The gap of a link is the distance between the positions of the two minecarts minus the length of one minecart. This length is 1.25 blocks, the length of the vanilla minecart model.
- On a straight rail, the gap is the distance between the two ends that face each other. The server uses the gap for all the rules of the chain. The gap changes smoothly in a curve and on a slope. Thus the chain does not jolt a minecart when it goes onto a new rail.
- The chain attaches at the ends of the two minecarts that face each other. An end is at 0.625 blocks from the position of the minecart, along the rail under it. On a curve, the end is along the diagonal. On a slope, the end is up 45 degrees, as the vanilla renderer turns the minecart.
- A minecart that is not on a rail turns to the other minecart. On a curve rail, the minecart turns 45 degrees. While a minecart is on the curve rail, the distance between the two ends is more than the gap. This distance is up to about 1 block for some ticks.
- In each link, the minecart nearer to the driver (the leader) pulls or pushes the other minecart (the follower). The mod changes only the speed of the follower, along its rail:
  - Below the buffer gap (0.15 blocks), or when the gap of the next tick goes below the buffer gap, the follower slows down. When the leader comes nearer, the follower moves away from it.
  - Thus two linked minecarts do not push each other as vanilla minecarts do, and they do not touch. In a curve, the straight line between the two minecarts is shorter than the rails between them. The gap can then go below the buffer gap, and the follower slows down for a short time.
  - From the buffer gap to the rest gap (0.2 blocks), the chain is slack.
  - From the rest gap to the maximum gap, a spring with damping pulls the follower.
  - Above the maximum gap, the chain acts as a rigid bar.
- Thus two linked minecarts keep a visible gap of about 0.2 blocks on a straight rail. The gap goes from the buffer gap when the leader pushes or brakes, to a little more than the rest gap when the leader pulls. In a curve and on a slope, the gap changes for a short time, from 0 to 0.5 blocks. The two minecarts do not touch.
- The motion of a minecart stays below the speed limit of its rail, so that a minecart does not leave a curve.
- A link breaks when the gap stays above the break gap for some ticks. This tolerance keeps the train together after a lag of some ticks. The chain drops at the middle of the link.
- A link breaks and drops the chain when a minecart breaks or goes through a portal. A link does not break when a minecart unloads.
- A train stops as one unit when one of its minecarts is not loaded. It also stops when one of its minecarts is in a chunk at the edge of the simulation distance. At this edge, the level does not tick the entities.
  - Each minecart that ticks goes back to its position before the tick, and its motion is zero. No slope, powered rail or rider moves it.
  - A furnace minecart keeps its push, and its fuel burns. The links wait, and the break counters do not increase. Thus two linked minecarts are never much farther apart than the break gap.
- Each link drops its chain when it breaks. The links that the mod corrects (see the next item) never drop a chain. Thus a link that only one minecart has owns no chain.
- The mod corrects the links each tick. It removes a link that only one minecart has. When `maxCarts` is less than the size of a train, the links after `maxCarts` minecarts break and drop their chains.
- Every second, the mod removes the dead links. A dead link is a link to a minecart that does not exist any more. An example is a minecart that a player broke while the other minecart was not loaded.
  - A link is dead when three conditions are true. Its minecart ticks. The level does not give the other minecart. All the chunks near the minecart have their entities loaded and tick them.
  - The distance for these chunks is `breakGap` + 3.25 blocks: the break gap, the two half lengths and a margin of 2 blocks.
  - The link must be dead in 2 checks in a row, because the entities of a chunk load after the chunk.
- The client draws a chain between the ends of the two minecarts of each link, as a leash. The sag of the chain is 0.25 blocks for each block of length, 0.12 blocks at most. Thus a short chain is almost straight.
- The data attachments do not go to the client. When a link changes, the server sends the links to the players that track the minecart. It also sends them when a player starts to track a minecart.

## Locomotive

- Code: `Locomotive`, `FurnaceLocomotive`, `CartItem`, `Traction`. The furnace locomotive is a minecart that pulls the minecarts behind it. Recipe: furnace minecart, 4 iron ingots and 1 piston.
- The item and the dispenser place it on a rail as a vanilla minecart. It looks like the vanilla furnace minecart with a copper color on the furnace. The item has the icon of the furnace minecart in a copper color. It has no screen, and no player rides it.
- The player adds a furnace fuel with a right click (1 coal = 1600 units). A lava bucket gives back the bucket. A right click with an empty hand starts or stops the locomotive. The action bar shows the state and the fuel, in blocks.
- On start, the locomotive goes away from the minecart behind it. With no minecart behind it, the locomotive goes away from the player. Then its direction follows the rails.
- Each tick, the speed of the locomotive changes by (pull − resistance) / mass:
  - Pull: the pulling force at speed 0, less in a line to 0 at the maximum speed. A locomotive that does not run or has no fuel brakes with its pulling force.
  - Mass: the mass of the locomotive plus the mass of each minecart behind it. The mass of a wagon is `wagonMass` plus `cargoMass` multiplied by the fill ratio of its slots. The fill ratio counts each stack as its count divided by its stack size.
  - A wagon with more than 27 slots multiplies its cargo mass by its slots divided by 27 (2 for the double chest minecart). A wagon with 27 slots or fewer (chest minecart, hopper minecart) has the full `cargoMass`.
  - A wagon that still has a loot table counts as empty, because a read of its slots generates the loot.
  - Resistance: for each minecart, its mass multiplied by `friction`, plus `slopeForce` up a slope (minus down a slope). A powered rail with power removes `poweredRailForce`, and a powered rail with no power adds `brakeForce`.
- The speed is never less than 0: a train that is too heavy for a slope stops and does not slide back. The speed is never more than the speed cap of the train (see "Speed").
- The vanilla slope, powered rails and slowdown do not change the speed of the locomotive. The chains (see "Train") pull the minecarts behind it.
- A locomotive with a front link is a wagon of the train in front. It does not drive and burns no fuel.
- The fuel burns for each block that the locomotive moves while it runs. It burns 8 units for each block, thus about 200 blocks for 1 coal.
- A train that waits does not move, so its fuel does not burn. The exception is a locomotive that loads chunks (see "Loaded area"). The fuel is never more than `fuelCapacity`. A broken locomotive drops its item. Its fuel is lost.
- Code: `HeavyLocomotive`. The heavy locomotive is a second type of locomotive, with the same rules and its own stats in the configuration. It has more pulling force and a maximum speed of 0.6 blocks per tick.
- Recipe of the heavy locomotive: blast furnace, minecart, 2 blocks of iron and 1 piston. Its only fuel is the block of coal (16000 units). It burns 16 units for each block, thus about 1000 blocks for 1 block of coal.
- The heavy locomotive looks like the vanilla minecart with a blast furnace in a steel color. The item has the icon of the furnace minecart in the same color.

## Double chest minecart

- Code: `DoubleChestMinecart`, `CartItem`. A minecart with 54 slots. Recipe: chest minecart and chest, with no shape.
- The item and the dispenser place it on a rail as a vanilla minecart. A right click opens a screen of 6 rows. More than one player can open it at the same time, as the vanilla chest minecart.
- Hoppers and hopper minecarts fill it and empty it, as the vanilla chest minecart. It also gives the item handlers of NeoForge for other mods. On a detector rail, a comparator gives its fill level.
- A destroyed minecart drops its item and all its stacks one time. A minecart that `/kill` removes drops its stacks but not its item, as the vanilla chest minecart.
- It has no loot table. It ignores a loot table from a command or from saved data. Thus it never generates loot.
- It is a wagon of a train as the other minecarts with a container. It carries 2 times the cargo of a chest minecart, so its cargo mass is 2 times larger. A full double chest minecart has a mass of `wagonMass` + 2 × `cargoMass` (see "Locomotive").
- It looks like the vanilla chest minecart with a second chest on the first chest. The item has the icon of the chest minecart in a darker color.

## Shulker box minecart

- Code: `ShulkerMinecart`, `ShulkerMinecartItem`, `ShulkerMinecartRecipe`. A minecart with a shulker box: 27 slots and the shulker box screen. It has no color or one of the 16 dye colors.
- Recipe: minecart and shulker box, with no shape. The result keeps the items and the color of the shulker box. A shulker box with a loot table or a lock does not match, because the minecart cannot keep the loot table or the lock.
- The item and the dispenser place it on a rail as a vanilla minecart, with its items and its color.
- The minecart keeps its items as a shulker box does. A player, a creative player, an explosion, lava, a cactus or `/kill` can remove it. Then it drops one item with its items, its color and its name.
- Its stacks never drop one by one. An empty minecart drops its item only when damage destroys it and the game rule `doEntityDrops` is true.
- The item cannot go into a shulker box, a bundle or a shulker box minecart, as a shulker box. A shulker box cannot go into the minecart. A destroyed item entity (for example in lava) drops its items, as a shulker box item.
- The tooltip of the item shows the first 5 stacks and the number of the other stacks, as a shulker box item.
- Hoppers, hopper minecarts and the item handlers of NeoForge fill it and empty it, with the same rule as a shulker box. It has no loot table, as the double chest minecart. It is a wagon of a train with the mass of a chest minecart.
- It shows the vanilla shulker box in its color on the minecart. The item has the icon of the chest minecart in the color of the shulker box, mixed with white.

## Ender chest minecart

- Code: `EnderChestMinecart`. A minecart with an ender chest. Recipe: minecart and ender chest, with no shape. The item and the dispenser place it on a rail as a vanilla minecart.
- A right click opens the ender chest of the player (3 rows), as the ender chest block. Two players see their own ender chests in the same minecart.
- The menu closes when the minecart goes away or when the player is too far from the minecart. This distance is the same as for the vanilla chest minecart.
- The minecart holds no items: it is not a container. Hoppers, hopper minecarts, comparators and the item handlers of NeoForge have no access to it. Thus only the player can get the items of his ender chest.
- A destroyed minecart drops only its item, not obsidian, when the game rule `doEntityDrops` is true. The ender chest of the player keeps its items.
- It is a wagon of a train with no cargo: its mass is always `wagonMass`.
- It shows the vanilla ender chest on the minecart. The item has the icon of the chest minecart in a dark green color.

## Speed

- Code: `TrainSpeed`, a mixin of `AbstractMinecart#getMaxSpeedWithRail`. The vanilla rails move a minecart in one straight step each tick, along the rail under it.
- The speed cap of the vanilla rails is 0.4 blocks per tick. Above this speed, a minecart can go past a curve or the top of a slope in one step and leave the rails.
- Each tick, the locomotive gives a speed cap to all the minecarts of its train. The cap is `maxSpeed` of the locomotive (0.6 at most) when the rails are straight and flat in front of each minecart. The check examines `lookAhead` blocks.
- In this distance, a curve, a slope or a junction in a curve position sets the cap to `curveSpeed` (0.4). The end of the rails also sets this cap. A chunk that is not loaded also sets this cap.
- All the minecarts of the train then slow down to this speed in one tick, before the train gets there. Thus a train in a curve or on a slope always moves as vanilla minecarts do.
- The check does not load chunks. It reads only the loaded and complete chunks.
- The cap is half in water, as for the vanilla rails. A minecart keeps the cap of its train for one tick after the last tick of the locomotive.
- The train cap applies only on the vanilla rails. On a rail of another mod, the cap is the lower value of the cap of the train and the cap of the rail.
- Only vanilla rails count as straight and flat for the check. On a vanilla rail, a cap that a mod gives to a minecart stays a limit.
- A vanilla furnace minecart that is a wagon gets the speed cap of its train, in place of its vanilla cap of 0.2.
- A minecart that is not in a train with a locomotive keeps the vanilla speed cap, also a minecart after its link breaks. A vanilla furnace minecart at the front of a train does not give a cap.

## Loaded area

- A train stops at the edge of the loaded area (see "Train"). This is the default.
- Code: `TrainChunks`, option `loadChunks`, off by default. Each tick, a locomotive that runs, has fuel and moves gives a chunk ticket to the chunk of each minecart of its train. It also gives a ticket to the chunks on a straight line in front of it for 32 blocks.
- The line follows the direction of travel of the locomotive, not the rails. In a curve, some chunks of the line are not on the rails. A vanilla furnace minecart does not load chunks.
- A ticket has a distance of 2. Its chunk ticks its entities. Around it, a 3 × 3 area of chunks ticks its blocks (random ticks, crops, redstone), but not its entities.
- The locomotive gives tickets only while it moves: it must move in the last 20 ticks. A train that runs but cannot move does not keep its chunks loaded. Examples are a wall, an unpowered powered rail, or a slope that is too steep. Only a real movement counts.
- A train that waits at the edge of the loaded area goes back to its position each tick. It gets its tickets when its locomotive gives it a speed and moved in the last 200 ticks.
- Thus a train that moved to the edge continues. A train that cannot move (for example against a wall) with a wagon at the edge does not keep its chunks loaded.
- A train that waits at the edge for more than 200 ticks (for example after a restart of the server) gets no tickets. It continues when a player comes near it.
- While a locomotive gives tickets, it burns its fuel in each tick as if it moved at its maximum speed. The fuel of a tick is (the larger value of its movement and `maxSpeed`) × `fuelPerBlock`. This is also true when it does not move.
- Thus the chunk loading always costs fuel. The furnace locomotive burns the fuel of 0.4 blocks in each tick (1 coal for about 25 seconds). The heavy locomotive burns the fuel of 0.6 blocks (1 block of coal for about 83 seconds).
- When the fuel ends, the locomotive stops and gives no more tickets.
- Each ticket has a lifespan of 20 ticks, and the locomotive gives its tickets again each tick. The tickets go away after 1 second when the locomotive stops moving, stops, has no fuel, goes away or unloads. They also go away when the option is off.
- The game does not save the tickets.
- Only `maxLoadingTrains` locomotives on the whole server (all the players and all the dimensions) load chunks at the same time, first come first served. There is no limit for each player.
- A locomotive keeps its place until it gives no tickets for 20 ticks. Then the place is free for another locomotive.
- The game does not tick the entities of a level that has no player and no forced chunk for 300 ticks. The tickets of the trains do not change this.

## Server cost

- The mod finds each train one time in each tick, from its front minecart. It keeps no cache of the trains.
- Target: 2 ms for each tick for the code of the trains (the pass of the trains and the ticks of the locomotives).
- The GameTests `TrainCostGameTests` measure the cost with `System.nanoTime`: 20 trains of 16 minecarts that move, for 40 ticks. The test machine has 2 CPUs and other work. The table gives the lowest and highest value of 3 or 4 runs, for each tick:

| Part | `loadChunks` false | `loadChunks` true (20 locomotives give tickets) |
| --- | --- | --- |
| Pass of the trains (`Trains`, at the end of the tick of the level, with the other listeners of this event) | 0.31 to 0.85 ms | 0.58 to 1.00 ms |
| Ticks of the 20 locomotives (vanilla movement, forces, look-ahead, speed cap, chunk tickets) | 0.48 to 1.06 ms | 0.90 to 1.48 ms |
| Code of the trains (sum of the two lines above) | 0.79 to 1.91 ms | 1.48 to 2.47 ms |
| Ticks of the 300 wagons (vanilla movement with the speed cap of the train) | 2.05 to 3.86 ms | 2.67 to 3.44 ms |

- With `loadChunks` false (the default), the code of the trains is below the target of 2 ms. With `loadChunks` true, it can be above the target (up to 2.47 ms).
- The values contain vanilla work (the movement of the locomotives) and the other listeners of the event. Thus they are more than the cost of the code of the mod. The mod keeps no cache of the trains: the pass of the trains is less than 1 ms in all the runs.

## Configuration

File `railhold-server.toml` in the `serverconfig` folder of each world, section `rails`. The config screen of the mod list shows the same values, in English and in French.

| Value | Default | Meaning |
| --- | --- | --- |
| `bufferGap` | 0.15 | Smallest gap between the ends of two linked minecarts, in blocks |
| `restGap` | 0.2 | Gap of a slack chain, in blocks |
| `maxGap` | 1.25 | Largest gap of a stretched chain, in blocks. Also the largest gap for a new link |
| `breakGap` | 2.75 | A link breaks above this gap, in blocks |
| `breakTicks` | 40 | Ticks above `breakGap` before a link breaks (2 seconds) |
| `stiffness` | 0.15 | Speed change for each block of stretch, in blocks per tick |
| `damping` | 0.3 | Part of the speed difference that the back minecart gets back each tick |
| `maxCarts` | 16 | Largest number of minecarts in a train |
| `wagonMass` | 1.0 | Mass of a wagon with no cargo |
| `cargoMass` | 3.0 | Mass of the cargo of a full wagon |
| `friction` | 0.001 | Friction force for each unit of mass |
| `slopeForce` | 0.0078125 | Force of a slope for each unit of mass on it |
| `poweredRailForce` | 0.06 | Push of a powered rail with power, for each unit of mass on it |
| `brakeForce` | 0.5 | Brake of a powered rail with no power, for each unit of mass on it |
| `curveSpeed` | 0.4 | Speed cap of a train with a locomotive in a curve and on a slope, in blocks per tick (0.4 at most) |
| `lookAhead` | 3 | Number of blocks that each minecart of a train with a locomotive examines in front of it |
| `loadChunks` | false | A locomotive that runs, has fuel and moves loads the chunks of its train and the chunks 32 blocks in front of it |
| `maxLoadingTrains` | 8 | Largest number of locomotives on the whole server that load chunks at the same time |
| `furnaceLocomotive.maxSpeed` | 0.4 | Largest speed on straight and flat rails, in blocks per tick (0.4 at most) |
| `furnaceLocomotive.pullingForce` | 0.12 | Pulling force at speed 0 |
| `furnaceLocomotive.mass` | 2.0 | Mass of the locomotive |
| `furnaceLocomotive.fuelCapacity` | 32000 | Largest quantity of fuel (1 coal = 1600) |
| `furnaceLocomotive.fuelPerBlock` | 8.0 | Fuel for each block that the locomotive moves |
| `heavyLocomotive.maxSpeed` | 0.6 | Largest speed on straight and flat rails, in blocks per tick (0.6 at most) |
| `heavyLocomotive.pullingForce` | 0.4 | Pulling force at speed 0 |
| `heavyLocomotive.mass` | 4.0 | Mass of the locomotive |
| `heavyLocomotive.fuelCapacity` | 160000 | Largest quantity of fuel (1 block of coal = 16000) |
| `heavyLocomotive.fuelPerBlock` | 16.0 | Fuel for each block that the locomotive moves |

With the default values, a furnace locomotive pulls 2 full wagons up a slope of 1 block in 1. With 12 full wagons, it stops on this slope. On flat rails, its top speed is about 0.37 blocks per tick with 8 empty wagons and 0.29 with 8 full wagons. The top speed of a heavy locomotive is about 0.58 blocks per tick with 8 empty wagons and 0.55 with 8 full wagons.

## Worlds of Folkhold Balance

Code: `WorldAliases`. A world of Folkhold Balance saves the items, the entities and the chain links with the namespace `folkhold_balance`. Railhold reads each of these ids as the Railhold id with the same path. Thus such a world opens with Railhold and loses nothing:

| Saved id | Railhold id |
| --- | --- |
| `folkhold_balance:furnace_locomotive` (item and entity) | `railhold:furnace_locomotive` |
| `folkhold_balance:heavy_locomotive` (item and entity) | `railhold:heavy_locomotive` |
| `folkhold_balance:double_chest_minecart` (item and entity) | `railhold:double_chest_minecart` |
| `folkhold_balance:shulker_box_minecart` (item and entity) | `railhold:shulker_box_minecart` |
| `folkhold_balance:ender_chest_minecart` (item and entity) | `railhold:ender_chest_minecart` |
| `folkhold_balance:cart_link` (data attachment of a minecart) | `railhold:cart_link` |

- A minecart saves its data again with the Railhold id when its chunk saves.
- An alias applies only when the registry has no entry with the id. Do not use Railhold and the trains of Folkhold Balance in the same game: the two mods then move the same trains.
- The config file has a new name. To keep changed values on a server, stop the server. Copy the values of the section `[rails]` of `serverconfig/folkhold_balance-server.toml` to the same section of `serverconfig/railhold-server.toml`. If this file does not exist, start the server one time with Railhold to make it.

## Tests

- JUnit (`src/test`): the chain (`CouplingTest`), the forces of the locomotives (`TractionTest`) and the driver of a train with no locomotive (`TrainPushTest`).
- GameTest (`src/gametest`): the links and the trains (`TrainGameTests`, `TrainGapGameTests`, `TrainPushGameTests`, `TrainSpeedGameTests`), the locomotives (`LocomotiveGameTests`) and the storage minecarts (`DoubleChestMinecartGameTests`, `ShulkerMinecartGameTests`, `EnderChestMinecartGameTests`).
- GameTest: the chunk loading (`TrainChunkGameTests`, `TrainLoadingGameTests`), the server cost (`TrainCostGameTests`) and the ids of the worlds of Folkhold Balance (`WorldAliasGameTests`).
- The templates are boxes of air in `src/gametest/resources/data/railhold/structure`: `bench`, `track`, `climb` and `express`.
