package lassevkp.revivals;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DeadManager {

    public List<UUID> deadPlayers = new ArrayList<UUID>();


    public DeadManager(){

    }

    public void killPlayer(UUID uuid){
        if(deadPlayers.contains(uuid)) return;
        deadPlayers.add(uuid);
    }

    public void revivePlayer(UUID uuid){
        if(!deadPlayers.contains(uuid)) return;
        deadPlayers.remove(uuid);
    }


}
