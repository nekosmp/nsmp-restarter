package rs.neko.smp.restarter.server;

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;

public class ServerRestarter implements DedicatedServerModInitializer {
  public static final Logger LOGGER = LoggerFactory.getLogger("nsmp-restarter");
  private static final Timer TIMER = new Timer();
  private static final int[] INTERVALS = new int[] {
      60 * 30,
      60 * 15,
      60 * 10,
      60 * 5,
      60 * 2,
      60,
      30,
      15,
      10,
      5,
      4,
      3,
      2,
      1
  };
  private static final int OFFSET = 6 * 60 * 60 * 1000;

  private static void shedule(Runnable runnable, int delay) {
    TIMER.schedule(new TimerTask() {
      @Override
      public void run() {
        runnable.run();
      }
    }, delay);
  }

  private static void announce(MinecraftServer server, int sec) {
    shedule(() -> {
      String output = "Server restart in ";
      if (sec > 60) {
        output += sec / 60 + " minutes";
      } else {
        output += sec + " seconds";
      }
      server.getPlayerManager().broadcast(Text.literal(output), false);
    }, OFFSET - sec * 1000);
  }

  @Override
  public void onInitializeServer() {
    LOGGER.info("Initializing NSMP Restarter");
    ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
      for (int interval : INTERVALS)
        announce(server, interval);
      shedule(() -> server.stop(false), OFFSET);
    });
    ServerLifecycleEvents.SERVER_STOPPING.register((s) -> {
      TIMER.cancel();
    });
  }
}
