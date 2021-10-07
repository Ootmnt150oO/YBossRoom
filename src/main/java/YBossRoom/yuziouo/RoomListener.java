package YBossRoom.yuziouo;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.math.Vector3;

import java.util.ArrayList;
import java.util.Map;

public class RoomListener implements Listener {
    ArrayList<String> dead = new ArrayList<>();
    @EventHandler
    public void onTp(PlayerTeleportEvent event){
        if (Room.getInstance().inroom.contains(event.getPlayer().getName())&&!dead.contains(event.getPlayer().getName()))
            event.setCancelled(true);
    }
    @EventHandler
    public void onChange(PlayerGameModeChangeEvent event){
        if (Room.getInstance().inroom.contains(event.getPlayer().getName()))
            event.setCancelled(true);
    }
    @EventHandler
    public void onQuite(PlayerQuitEvent event){
        if (Room.getInstance().inroom.contains(event.getPlayer().getName())){
            Room.getInstance().quiteTeam(event.getPlayer());
            Room.getInstance().inroom.remove(event.getPlayer().getName());
            Room.getInstance().death.remove(event.getPlayer().getName());
            event.getPlayer().setNameTag(event.getPlayer().getName());
        }

    }
    @EventHandler
    public void onPVP(EntityDamageByEntityEvent event){
        if (event.getEntity() instanceof Player&&event.getDamager()instanceof Player) {
            Player damager = (Player) event.getDamager();
            Player entity = (Player) event.getEntity();
            if (Room.getInstance().inroom.contains(damager.getName())&&Room.getInstance().inroom.contains(entity.getName()))
            event.setCancelled(true);
        }
    }
   @EventHandler(priority = EventPriority.LOWEST)
    public void onEndGame(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if (Room.getInstance().inroom.contains(player.getName())){
        for (String  s:Loader.getLoader().rooms){
            if (Loader.getLoader().getRoomConfig(s).getString("副本結束點")!= null) {
                    String pos = event.getBlock().getLevel().getName()+":"+event.getBlock().getFloorX() + ":" + event.getBlock().getFloorY() + ":" + event.getBlock().getFloorZ();
                    if (pos.equals(Loader.getLoader().getRoomConfig(s).getString("副本世界")+":"+Loader.getLoader().getRoomConfig(s).getString("副本結束點"))) {
                        Room.getInstance().quiteTeam(player);
                        Server.getInstance().getScheduler().cancelTask(Room.getInstance().task.get(player.getName()));
                        Room.getInstance().task.remove(player.getName());
                        Room.getInstance().inroom.remove(player.getName());
                        player.sendMessage(Loader.getLoader().getRoomConfig(s).getString("副本完成訊息"));
                        Room.getInstance().death.remove(player.getName());
                        player.setNameTag(player.getName());
                        player.teleport(Server.getInstance().getDefaultLevel().getSpawnLocation());
                        break;
                        }
                }
            }
        }
   }
   @EventHandler
    public void onDeath(PlayerDeathEvent event){
        Player player = event.getEntity().getPlayer();
        if (Room.getInstance().inroom.contains(player.getName())){
           if (ValueGetKey(player)!= null) {
               if (Room.getInstance().death.get(player.getName()) >= Loader.getLoader().getRoomConfig(ValueGetKey(player)).getInt("玩家生命數量")) {
                   player.sendMessage( Loader.getLoader().getRoomConfig(ValueGetKey(player)).getString("副本失敗訊息"));
                   Room.getInstance().quiteTeam(player);
                   Server.getInstance().getScheduler().cancelTask(Room.getInstance().task.get(player.getName()));
                   Room.getInstance().task.remove(player.getName());
                   Room.getInstance().inroom.remove(player.getName());
                   Room.getInstance().death.remove(player.getName());
                   player.setNameTag(player.getName());
               } else {
                   int a = Room.getInstance().death.get(player.getName());
                   Room.getInstance().death.remove(player.getName());
                   Room.getInstance().death.put(player.getName(), a + 1);
                   int left = Loader.getLoader().getRoomConfig(ValueGetKey(player)).getInt("玩家生命數量") - Room.getInstance().death.get(player.getName());
                   player.sendMessage("你的生命剩下:" + left);
                   dead.add(player.getName());
               }
           }
        }
   }
   public String ValueGetKey(Player player){
        for (Map.Entry<String,ArrayList<String>> entry:Room.getInstance().manager.entrySet()){
            if (entry.getValue().contains(player.getName())) return entry.getKey();
        }
        return null;
   }
   @EventHandler
    public void onPlace(BlockPlaceEvent event){
        if (Room.getInstance().inroom.contains(event.getPlayer().getName()))event.setCancelled(true);
   }
   @EventHandler
    public void onBreak(BlockBreakEvent event){
       if (Room.getInstance().inroom.contains(event.getPlayer().getName()))event.setCancelled(true);
   }
   @EventHandler
    public void onRespawn(PlayerRespawnEvent event){
        Player player = event.getPlayer();
        if (Room.getInstance().inroom.contains(player.getName())){
            String pos = Loader.getLoader().getRoomConfig(ValueGetKey(player)).getString("座標");
            String[] aa = pos.split(":");
            Server.getInstance().getScheduler().scheduleDelayedTask(Loader.getLoader(), new Runnable() {
                @Override
                public void run() {
                    player.teleport(new Vector3(Integer.parseInt(aa[0]),Integer.parseInt(aa[1]),Integer.parseInt(aa[2])));
                    dead.remove(player.getName());
                }
            },2);
        }
   }
}
