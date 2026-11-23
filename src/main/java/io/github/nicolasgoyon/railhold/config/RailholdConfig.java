package io.github.nicolasgoyon.railhold.config;

import io.github.nicolasgoyon.railhold.rail.Coupling;
import io.github.nicolasgoyon.railhold.rail.Locomotive;
import io.github.nicolasgoyon.railhold.rail.LocomotiveStats;
import io.github.nicolasgoyon.railhold.rail.Traction;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Server configuration of Railhold: file {@code railhold-server.toml} in the {@code serverconfig}
 * folder of each world.
 */
public final class RailholdConfig {
    static final String KEY_PREFIX = "railhold.configuration.";

    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.DoubleValue CHAIN_BUFFER_GAP;
    public static final ModConfigSpec.DoubleValue CHAIN_REST_GAP;
    public static final ModConfigSpec.DoubleValue CHAIN_MAX_GAP;
    public static final ModConfigSpec.DoubleValue CHAIN_BREAK_GAP;
    public static final ModConfigSpec.IntValue CHAIN_BREAK_TICKS;
    public static final ModConfigSpec.DoubleValue CHAIN_STIFFNESS;
    public static final ModConfigSpec.DoubleValue CHAIN_DAMPING;
    public static final ModConfigSpec.IntValue TRAIN_MAX_CARTS;
    public static final ModConfigSpec.DoubleValue WAGON_MASS;
    public static final ModConfigSpec.DoubleValue CARGO_MASS;
    public static final ModConfigSpec.DoubleValue FRICTION;
    public static final ModConfigSpec.DoubleValue SLOPE_FORCE;
    public static final ModConfigSpec.DoubleValue POWERED_RAIL_FORCE;
    public static final ModConfigSpec.DoubleValue BRAKE_FORCE;
    public static final ModConfigSpec.DoubleValue CURVE_SPEED;
    public static final ModConfigSpec.IntValue LOOK_AHEAD;
    public static final ModConfigSpec.BooleanValue TRAIN_LOAD_CHUNKS;
    public static final ModConfigSpec.IntValue TRAIN_MAX_LOADING;
    private static final LocomotiveValues FURNACE;
    private static final LocomotiveValues HEAVY;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        Coupling defaults = Coupling.DEFAULTS;

        builder.comment("Trains: minecarts that a chain links.")
                .translation(KEY_PREFIX + "rails")
                .push("rails");
        CHAIN_BUFFER_GAP = builder
                .comment("Smallest gap in blocks between two linked minecarts: the distance between the ends that",
                        "face each other. Below this gap, the back minecart slows down: linked minecarts do not push",
                        "each other as vanilla minecarts do.")
                .translation(KEY_PREFIX + "bufferGap")
                .defineInRange("bufferGap", defaults.bufferGap(), 0.0, 2.0);
        CHAIN_REST_GAP = builder
                .comment("Gap in blocks between two linked minecarts when the chain is not stretched. Below this",
                        "gap, the chain does not pull. The value must be at least bufferGap.")
                .translation(KEY_PREFIX + "restGap")
                .defineInRange("restGap", defaults.restGap(), 0.05, 2.0);
        CHAIN_MAX_GAP = builder
                .comment("Largest gap in blocks of a stretched chain. Above this gap, the back minecart follows the",
                        "front minecart as with a rigid bar. A player links two minecarts only at this gap or",
                        "nearer. The value must be at least restGap.")
                .translation(KEY_PREFIX + "maxGap")
                .defineInRange("maxGap", defaults.maxGap(), 0.1, 6.0);
        CHAIN_BREAK_GAP = builder
                .comment("A link breaks when the gap stays above this value in blocks for breakTicks ticks.",
                        "The value must be at least maxGap.")
                .translation(KEY_PREFIX + "breakGap")
                .defineInRange("breakGap", defaults.breakGap(), 0.25, 14.0);
        CHAIN_BREAK_TICKS = builder
                .comment("Number of ticks (20 ticks = 1 second) above breakGap before a link breaks. This",
                        "tolerance keeps the train together after a lag of some ticks.")
                .translation(KEY_PREFIX + "breakTicks")
                .defineInRange("breakTicks", defaults.breakTicks(), 1, 1200);
        CHAIN_STIFFNESS = builder
                .comment("Force of the spring: the speed in blocks per tick that the back minecart gets each tick",
                        "for each block of stretch above restGap.")
                .translation(KEY_PREFIX + "stiffness")
                .defineInRange("stiffness", defaults.stiffness(), 0.0, 1.0);
        CHAIN_DAMPING = builder
                .comment("Damping of the spring: the part of the speed difference between the two minecarts that",
                        "the back minecart gets back each tick while the chain is stretched.")
                .translation(KEY_PREFIX + "damping")
                .defineInRange("damping", defaults.damping(), 0.0, 1.0);
        TRAIN_MAX_CARTS = builder
                .comment("Largest number of minecarts in one train.")
                .translation(KEY_PREFIX + "maxCarts")
                .defineInRange("maxCarts", 16, 2, 256);
        Traction traction = Traction.DEFAULTS;
        WAGON_MASS = builder
                .comment("Mass of a wagon with no cargo. A locomotive moves its mass and the mass of the minecarts",
                        "behind it.")
                .translation(KEY_PREFIX + "wagonMass")
                .defineInRange("wagonMass", traction.wagonMass(), 0.1, 100.0);
        CARGO_MASS = builder
                .comment("Mass of the cargo of a full chest minecart. A wagon with half of its slots full has half of",
                        "this mass. The stack size of each item gives its fill.")
                .translation(KEY_PREFIX + "cargoMass")
                .defineInRange("cargoMass", traction.cargoMass(), 0.0, 100.0);
        FRICTION = builder
                .comment("Friction force for each unit of mass of a train.")
                .translation(KEY_PREFIX + "friction")
                .defineInRange("friction", traction.friction(), 0.0, 1.0);
        SLOPE_FORCE = builder
                .comment("Force of a slope against a train for each unit of mass of a minecart on the slope. A",
                        "slope down pushes the train with the same force.")
                .translation(KEY_PREFIX + "slopeForce")
                .defineInRange("slopeForce", traction.slopeForce(), 0.0, 1.0);
        POWERED_RAIL_FORCE = builder
                .comment("Push of a powered rail with power, for each unit of mass of the minecart on it.")
                .translation(KEY_PREFIX + "poweredRailForce")
                .defineInRange("poweredRailForce", traction.poweredRailForce(), 0.0, 1.0);
        BRAKE_FORCE = builder
                .comment("Brake of a powered rail with no power, for each unit of mass of the minecart on it.")
                .translation(KEY_PREFIX + "brakeForce")
                .defineInRange("brakeForce", traction.brakeForce(), 0.0, 10.0);

        CURVE_SPEED = builder
                .comment("Speed cap in blocks per tick of a train with a locomotive in a curve, on a slope and at the",
                        "end of the rails. The train slows down to this speed before it gets there. 0.4 is the speed",
                        "cap of the vanilla rails: above it, a minecart can leave a curve.")
                .translation(KEY_PREFIX + "curveSpeed")
                .defineInRange("curveSpeed", Locomotive.RAIL_SPEED_CAP, 0.05, Locomotive.RAIL_SPEED_CAP);
        LOOK_AHEAD = builder
                .comment("Number of blocks that each minecart of a train with a locomotive examines on the rails in",
                        "front of it. A curve, a slope or the end of the rails in this distance sets the speed cap",
                        "of the train to curveSpeed.")
                .translation(KEY_PREFIX + "lookAhead")
                .defineInRange("lookAhead", 3, 2, 16);
        TRAIN_LOAD_CHUNKS = builder
                .comment("A locomotive that runs, has fuel and moves loads the chunks of its train and the chunks on",
                        "a straight line 32 blocks in front of it. These chunks tick their entities, so the train does",
                        "not stop at the edge of the loaded area. Around each of these chunks, a 3 x 3 area of chunks",
                        "ticks its blocks (random ticks, crops, redstone). While it loads chunks, the locomotive burns",
                        "its fuel as at its maximum speed, also when it does not move. A train that waits at the edge",
                        "for more than 200 ticks (for example after a restart) continues when a player comes near.",
                        "False: the train stops at the edge of the loaded area and waits.")
                .translation(KEY_PREFIX + "loadChunks")
                .define("loadChunks", false);
        TRAIN_MAX_LOADING = builder
                .comment("Largest number of locomotives on the server that load chunks at the same time, for all",
                        "the players and all the dimensions. First come, first served.")
                .translation(KEY_PREFIX + "maxLoadingTrains")
                .defineInRange("maxLoadingTrains", 8, 1, 64);
        FURNACE = locomotive(builder, "furnaceLocomotive", "The furnace locomotive. It burns the fuels of the furnace.",
                LocomotiveStats.FURNACE_DEFAULTS, Locomotive.RAIL_SPEED_CAP, "200 blocks for 1 coal");
        HEAVY = locomotive(builder, "heavyLocomotive", "The heavy locomotive. It burns blocks of coal.",
                LocomotiveStats.HEAVY_DEFAULTS, Locomotive.MAX_SPEED, "1000 blocks for 1 block of coal");
        builder.pop();

        SPEC = builder.build();
    }

    /**
     * The coupling from the values of the section {@code rails}. Each gap is at least the gap before it in the order
     * buffer, rest, maximum, break.
     */
    public static Coupling coupling() {
        double buffer = CHAIN_BUFFER_GAP.get();
        double rest = Math.max(buffer, CHAIN_REST_GAP.get());
        double max = Math.max(rest, CHAIN_MAX_GAP.get());
        double breakGap = Math.max(max, CHAIN_BREAK_GAP.get());
        return new Coupling(buffer, rest, max, breakGap, CHAIN_BREAK_TICKS.get(), CHAIN_STIFFNESS.get(),
                CHAIN_DAMPING.get());
    }

    /** The load of the trains, from the values of the section {@code rails}. */
    public static Traction traction() {
        return new Traction(WAGON_MASS.get(), CARGO_MASS.get(), FRICTION.get(), SLOPE_FORCE.get(),
                POWERED_RAIL_FORCE.get(), BRAKE_FORCE.get());
    }

    /** The stats of the furnace locomotive, from the section {@code rails.furnaceLocomotive}. */
    public static LocomotiveStats furnaceLocomotive() {
        return FURNACE.stats();
    }

    /** The stats of the heavy locomotive, from the section {@code rails.heavyLocomotive}. */
    public static LocomotiveStats heavyLocomotive() {
        return HEAVY.stats();
    }

    /** Defines the section of a type of locomotive in the section {@code rails}. */
    private static LocomotiveValues locomotive(ModConfigSpec.Builder builder, String name, String comment,
                                               LocomotiveStats defaults, double speedLimit, String fuelUse) {
        builder.comment(comment).translation(KEY_PREFIX + name).push(name);
        LocomotiveValues values = new LocomotiveValues(
                builder.comment("Largest speed in blocks per tick on straight and flat rails, " + speedLimit + " at most.",
                                speedLimit < Locomotive.MAX_SPEED ? "The heavy locomotive is faster."
                                        : "Above this speed, a minecart can leave the rails.")
                        .translation(KEY_PREFIX + "maxSpeed")
                        .defineInRange("maxSpeed", defaults.maxSpeed(), 0.01, speedLimit),
                builder.comment("Pulling force at speed 0. The force decreases in a line to 0 at maxSpeed. Speed",
                                "change each tick = (force - resistance) / mass of the train.")
                        .translation(KEY_PREFIX + "pullingForce")
                        .defineInRange("pullingForce", defaults.force(), 0.0, 10.0),
                builder.comment("Mass of the locomotive.")
                        .translation(KEY_PREFIX + "mass")
                        .defineInRange("mass", defaults.mass(), 0.1, 100.0),
                builder.comment("Largest quantity of fuel, in ticks of furnace fuel (1 coal = 1600).")
                        .translation(KEY_PREFIX + "fuelCapacity")
                        .defineInRange("fuelCapacity", defaults.fuelCapacity(), 1, 1_000_000),
                builder.comment("Fuel that the locomotive burns for each block that it moves while it runs. The",
                                "default gives about " + fuelUse + ".")
                        .translation(KEY_PREFIX + "fuelPerBlock")
                        .defineInRange("fuelPerBlock", defaults.fuelPerBlock(), 0.0, 1000.0));
        builder.pop();
        return values;
    }

    /** The configuration values of a type of locomotive. */
    private record LocomotiveValues(ModConfigSpec.DoubleValue maxSpeed, ModConfigSpec.DoubleValue force,
                                    ModConfigSpec.DoubleValue mass, ModConfigSpec.IntValue fuelCapacity,
                                    ModConfigSpec.DoubleValue fuelPerBlock) {
        LocomotiveStats stats() {
            return new LocomotiveStats(maxSpeed.get(), force.get(), mass.get(), fuelCapacity.get(),
                    fuelPerBlock.get());
        }
    }

    private RailholdConfig() {
    }
}
