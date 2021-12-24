package YBossRoom.yuziouo;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.Config;

import java.util.ArrayList;

public class BossRoomCmd extends Command {
    public BossRoomCmd() {
        super("rm");
    }
    public static ArrayList<Player> point = new ArrayList<>();
    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (commandSender.isOp()){
            if (commandSender.isPlayer()){
                Player player = (Player) commandSender;
                if (strings.length == 2){
                    if (strings[0].equals("create")){
                        if(Loader.getLoader().rooms.contains(strings[1]))return true;
                        Config config = new Config(Loader.getLoader().getDataFolder()+"/Room/"+strings[1]+".yml",Config.YAML);
                        config.set("副本世界",player.getLevel().getName());
                        config.set("座標",player.getFloorX()+":"+player.getFloorY()+":"+player.getFloorZ());
                        config.set("人數上限",5);
                        config.set("觸發指令",null);
                        config.set("副本限制時間",300);
                        config.set("副本結束點",null);
                        config.set("副本完成訊息","恭喜完成副本");
                        config.set("副本超時訊息","可惜啦 超過時間瞜");
                        config.set("副本失敗訊息","可惜 你已經把你的生命耗盡了");
                        config.set("玩家生命數量",3);
                        config.set("介紹表單內容","我是內容");
                        config.save();
                        Loader.getLoader().rooms.add(strings[1]);
                        Room.getInstance().createRoom(strings[1]);
                        commandSender.sendMessage("創建房間成功");
                        return true;
                    }else if (strings[0].equals("end")){
                        if(!Loader.getLoader().rooms.contains(strings[1]))return true;
                        Config config = Loader.getLoader().getRoomConfig(strings[1]);
                        config.remove("副本結束點");
                        int y = player.getFloorY()-1;
                        String pos = player.getFloorX()+":"+y+":"+player.getFloorZ();
                        config.set("副本結束點",pos);
                        config.save();
                            player.sendMessage("設定成功");
                            return true;
                    }
                }else if (strings.length == 1){
                    if (strings[0].equals("reload")){
                        Loader.getLoader().rooms.clear();
                        Loader.getLoader().loadRoom();
                        for (String ss:Loader.getLoader().rooms) {
                            Room.getInstance().createRoom(ss);
                        }
                        player.sendMessage("副本房間重新讀取完成");
                    }
                }
            }
        }
        Player player = (Player) commandSender;
        FormListener.RoomMenu(player);
        return true;
    }
}
