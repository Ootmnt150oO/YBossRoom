package YBossRoom.yuziouo;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.element.ElementToggle;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.utils.Config;

public class FormListener implements Listener {
    public static void RoomMenu(Player player){
        FormWindowSimple simple = new FormWindowSimple("副本系統","");
        simple.addButton(new ElementButton("加入副本"));
        simple.addButton(new ElementButton("退出匹配"));
        player.showFormWindow(simple,444747);
    }

    public  void BossRoomUI(Player player){
        FormWindowSimple simple = new FormWindowSimple("副本系統","副本房間 點即加入列隊" );
        for (String s:Loader.getLoader().rooms){
            simple.addButton(new ElementButton(s));
        }
        player.showFormWindow(simple,6987444);
    }
    public void BossRoomInfo(Player player,String roomname){
        Config config = Loader.getLoader().getRoomConfig(roomname);
        FormWindowCustom custom = new FormWindowCustom("副本介紹");
        custom.addElement(new ElementLabel("副本名稱:"+roomname));
        custom.addElement(new ElementLabel(config.getString("介紹表單內容").replaceAll("換行","\n")));
        custom.addElement(new ElementToggle("是否開始匹配副本"));
        player.showFormWindow(custom,885502210);
    }
    @EventHandler
    public void onFormResponse(PlayerFormRespondedEvent event) {
        Player player = event.getPlayer();
        int id = event.getFormID();
        if (event.wasClosed()) return;
        FormResponseSimple response;
        switch (id){
            case 6987444:
               response = (FormResponseSimple) event.getResponse();
               String text = response.getClickedButton().getText();
                BossRoomInfo(player,text);
               break;
            case 885502210:
                FormResponseCustom custom = (FormResponseCustom) event.getResponse();
                String[] aa = custom.getLabelResponse(0).split(":");
                if (custom.getToggleResponse(2)) {
                    if (!Room.getInstance().hasRoom(player)) {
                        if (Room.getInstance().manager.get(aa[1]).size() <= Loader.getLoader().getRoomConfig(aa[1]).getInt("人數上線")) {
                            Room.getInstance().joinTeam(aa[1], player);
                            player.sendMessage("副本列隊加入成功");
                        } else {
                            player.sendMessage("抱歉副本已經開始瞜");
                        }
                    }
                }
                break;
            case 444747:
                response = (FormResponseSimple) event.getResponse();
                if (response.getClickedButtonId() == 0){
                    BossRoomUI(player);
                }else {
                    if (Room.getInstance().hasRoom(player)&&!Room.getInstance().inroom.contains(player.getName())){
                        Room.getInstance().quiteTeam(player);
                        player.sendMessage("退出成功");
                    }else {
                        player.sendMessage("你尚未加入隊伍");
                    }
                }
                break;
            default:
                break;
        }
    }
}
