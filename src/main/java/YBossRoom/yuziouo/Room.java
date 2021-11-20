package YBossRoom.yuziouo;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.TextFormat;

import java.util.ArrayList;
import java.util.HashMap;

public class Room {
    public ArrayList<String> room;
    public HashMap<String,ArrayList<String>> manager = new HashMap<>();
    public HashMap<String,Integer> death = new HashMap<>();
    public HashMap<String,String> nametag = new HashMap<>();
    static Room instance;
    public ArrayList<String> inroom = new ArrayList<>();
    HashMap<String,Integer> task = new HashMap<>();
    public Room(){
        instance = this;
        for (String s:Loader.getLoader().rooms){
            createRoom(s);
            System.out.println(s+" 已經創建成功");
        }
    }
    public boolean hasRoom(Player player){
        for (ArrayList<String> a: manager.values()){
            if (a.contains(player.getName())) return true;
        }
        return false;
    }
    public ArrayList<String> getRoom(Player player){
        if (hasRoom(player)){
            for (ArrayList<String> a: manager.values()){
                if (a.contains(player.getName()))return a;
            }
        }
        return null;
    }
    public void createRoom(String s ){
            room = new ArrayList<>();
            manager.put(s,room);
        }
    public void joinTeam(String s,Player player){
        if (!hasRoom(player)){
            manager.get(s).add(player.getName());
            Server.getInstance().broadcastMessage(TextFormat.YELLOW+"玩家 "+player.getName()+TextFormat.AQUA+"已經加入副本:"+TextFormat.YELLOW+s);
            if (manager.get(s).size()==Loader.getLoader().getRoomConfig(s).getInt("人數上限")){
                for (String s1:manager.get(s)){
                    Player player1 = Server.getInstance().getPlayer(s1);
                    if (player1!= null){
                        Server.getInstance().getScheduler().scheduleTask(Loader.getLoader(), new Runnable() {
                            @Override
                            public void run() {
                             player1.sendMessage("副本匹配成功 你即將在5秒後 被傳送到副本");
                             Server.getInstance().getScheduler().scheduleDelayedTask(Loader.getLoader(), new Runnable() {
                                 @Override
                                 public void run() {
                                     nametag.put(player1.getName(),player1.getNameTag());
                                     death.put(player1.getName(),0);
                                     player1.teleport(Server.getInstance().getLevelByName(Loader.getLoader().getRoomConfig(s).getString("副本世界")).getSpawnLocation());
                                     String[] a = Loader.getLoader().getRoomConfig(s).getString("座標").split(":");
                                     player1.teleport(new Vector3(Integer.parseInt(a[0]),Integer.parseInt(a[1]),Integer.parseInt(a[2])));
                                     player1.sendMessage("你已經傳送到副本:"+s);
                                     inroom.add(player1.getName());
                                     if (Loader.getLoader().getRoomConfig(s).getStringList("觸發指令")!=null)
                                     for (String cmd:Loader.getLoader().getRoomConfig(s).getStringList("觸發指令")){
                                         Server.getInstance().dispatchCommand(new ConsoleCommandSender(),cmd);
                                     }
                                     task.put(player1.getName(),Server.getInstance().getScheduler().scheduleRepeatingTask(Loader.getLoader(), new Runnable() {
                                         int i = 0;
                                         @Override
                                         public void run() {
                                             i++;
                                             player1.setNameTag(player1.getName()+"\n❤"+player1.getHealth()+"/"+player1.getMaxHealth());
                                             player1.sendPopup("目前開始時間"+i+"/"+Loader.getLoader().getRoomConfig(s).getInt("副本限制時間"));
                                             if (i>=Loader.getLoader().getRoomConfig(s).getInt("副本限制時間")){
                                                 player1.sendMessage(Loader.getLoader().getRoomConfig(s).getString("副本超時訊息"));
                                                 quiteTeam(player1);
                                                 Server.getInstance().getScheduler().cancelTask(task.get(player1.getName()));
                                                 task.remove(player1.getName());
                                                 inroom.remove(player1.getName());
                                                 death.remove(player1.getName());
                                                 player1.setNameTag(nametag.get(player1.getName()));
                                                 nametag.remove(player1.getName());
                                                 player1.teleport(Server.getInstance().getDefaultLevel().getSpawnLocation());

                                             }
                                         }
                                     },20).getTaskId());
                                 }
                             },20*5);
                            }
                        });
                    }
                }
            }
        }
    }
    public void quiteTeam(Player player){
        if (hasRoom(player)){
            getRoom(player).remove(player.getName());
        }
    }
    public static Room getInstance() {
        return instance;
    }
}
