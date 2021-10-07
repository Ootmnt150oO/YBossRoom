package YBossRoom.yuziouo;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

import java.io.File;
import java.util.ArrayList;

public class Loader extends PluginBase {
    static Loader loader;
    public ArrayList<String > rooms = new ArrayList<>();
    @Override
    public void onEnable() {
        loader = this;
        File file = new File(getDataFolder()+"/Room/");
        if (!file.exists())file.mkdirs();
        loadRoom();
        new Room();
        getServer().getPluginManager().registerEvents(new FormListener(),this);
        getServer().getPluginManager().registerEvents(new RoomListener(),this);
        getServer().getCommandMap().register("rm",new BossRoomCmd());
    }

    public static Loader getLoader() {
        return loader;
    }
    public Config getRoomConfig(String path){
        return new Config(getDataFolder()+"/Room/"+path+".yml",Config.YAML);
    }
    public void loadRoom(){
        File folder = new File(getDataFolder()+"/Room/");
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles == null) return;
        for (File file : listOfFiles) {
            if (file.isFile()) {
                String[]a = file.getName().split("\\.");
                if (a[1].equals("yml"))
                rooms.add(a[0]);
            }
        }
        System.out.println("以加載房間:"+rooms);
    }
}
