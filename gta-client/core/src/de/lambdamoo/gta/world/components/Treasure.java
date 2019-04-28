package de.lambdamoo.gta.world.components;

import com.artemis.Component;

public class Treasure extends Component {
    public int points = 0;
    public int quality;
    public String qualityName = null;
    public int score = 0;
    public boolean traped = false;
    public int type;
    public String name = null;
    public int trapId = -1;
}
