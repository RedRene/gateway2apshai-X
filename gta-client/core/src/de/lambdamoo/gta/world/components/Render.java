package de.lambdamoo.gta.world.components;

import com.artemis.Component;

public class Render extends Component {
    public int currentSpriteIndex = 0;
    public int maxSpriteIndex = 0;
    public int spriteIndex = 0;

    public void incSpriteIndex() {
        this.currentSpriteIndex++;
        if (this.currentSpriteIndex >= this.maxSpriteIndex) {
            this.currentSpriteIndex = 0;
        }
    }


}
