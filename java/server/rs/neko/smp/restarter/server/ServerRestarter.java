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
  private static final int OFFSET = 6 * 60 * 60 * 1000;

  private static void shedule(Runnable runnable, int delay) {
    TIMER.schedule(new TimerTask() {
      @Override
      public void run() {
        runnable.run();
      }
    }, delay);
  }

  public static void broadcast(MinecraftServer server, String text) {
    server.getPlayerManager().broadcast(Text.literal(text), false);
  }

  private static void announceMin(MinecraftServer s, int min) {
    shedule(() -> broadcast(s, "Server restart in " + min + " minutes"), OFFSET - min * 60000);
  }

  private static void announceSec(MinecraftServer s, int sec) {
    shedule(() -> broadcast(s, "Server restart in " + sec + " seconds"), OFFSET - sec * 1000);
  }

  @Override
  public void onInitializeServer() {
    LOGGER.info("Initializing NSMP Restarter");
    ServerLifecycleEvents.SERVER_STARTED.register((server) -> init(server));
  }

  private void init(MinecraftServer s) {
    announceMin(s, 30);
    announceMin(s, 15);
    announceMin(s, 10);
    announceMin(s, 5);
    announceMin(s, 2);
    announceSec(s, 60);
    announceSec(s, 30);
    announceSec(s, 15);
    announceSec(s, 10);
    announceSec(s, 5);
    announceSec(s, 4);
    announceSec(s, 3);
    announceSec(s, 2);
    announceSec(s, 1);
    shedule(() -> s.stop(false), OFFSET);
  }
}
