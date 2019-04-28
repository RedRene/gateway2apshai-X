package de.lambdamoo.gta.client.screens.hud;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import de.lambdamoo.gta.client.util.SpriteImages;
import de.lambdamoo.gta.world.components.Player;
import de.lambdamoo.gta.world.components.Status;

public class StatusPlayerBar extends Table {
    private Label labelStatusName = null;
    private Label labelStatusStrength = null;
    private Label labelStatusLuck = null;
    private Label labelStatusAgility = null;
    private Label labelStatusKills = null;
    private Label labelStatusAttack = null;
    private Label labelStatusArmor = null;
    private Label labelEquipWeapon = null;
    private Label labelEquipArmor = null;
    private Label labelEquipShield = null;


    public void build2D(Label.LabelStyle labelStyle, SpriteImages spriteImages) {
        labelStatusName = new Label("", spriteImages.getLabelStyleHeadlineSmall());
        labelStatusStrength = new Label("Strength: ", labelStyle);
        labelStatusAgility = new Label("Agility: ", labelStyle);
        labelStatusLuck = new Label("Luck: ", labelStyle);
        labelStatusKills = new Label("Kills: ", labelStyle);
        labelStatusAttack = new Label("Attack: ", labelStyle);
        labelStatusArmor = new Label("Armor: ", labelStyle);

        pad(15);
        defaults().align(Align.left);
        setBackground(spriteImages.getNinePatchTranslucent65Background());
        align(Align.center);
        add(labelStatusName).padBottom(15);
        row();
        add(labelStatusStrength);
        row();
        add(labelStatusAgility);
        row();
        add(labelStatusLuck);
        row();
        add(labelStatusAttack).padTop(15);
        row();
        add(labelStatusArmor);
        row();
        add(labelStatusKills);
        row();

        labelEquipWeapon = new Label("", labelStyle);
        labelEquipArmor = new Label("", labelStyle);
        labelEquipShield = new Label("", labelStyle);

        // Equipped
        add(labelEquipWeapon).padTop(30);
        row();
        add(labelEquipArmor);
        row();
        add(labelEquipShield);
        row();
        setWidth(200);
        //this.setDebug(true, true);
    }


    /**
     * Renders the player status to the HUD
     *
     * @param status
     * @param player
     */
    public void renderPlayerStatus(Player player, Status status) {
        labelStatusName.setText(player.name);
        labelStatusStrength.setText("Strength: " + player.strength);
        labelStatusAgility.setText("Agility: " + player.agility);
        labelStatusLuck.setText("Luck: " + player.luck);
        labelStatusKills.setText("Kills: " + player.killCount);
        String str = "Attack: " + status.powerWeapon;
        if (status.powerWand > 0) {
            str += "+" + status.powerWand;
        }
        labelStatusAttack.setText(str);

        str = "Armor: " + status.powerArmor + "+" + status.powerShield;
        if (status.powerWand > 0) {
            str += "+" + status.powerWand;
        }
        labelStatusArmor.setText(str);

        if (player.inventory.currentWeapon != null) {
            labelEquipWeapon.setText(player.inventory.currentWeapon.name);
        } else {
            labelEquipWeapon.setText("");
        }
        if (player.inventory.currentArmor != null) {
            labelEquipArmor.setText(player.inventory.currentArmor.name);
        } else {
            labelEquipArmor.setText("");
        }
        if (player.inventory.currentShield != null) {
            labelEquipShield.setText(player.inventory.currentShield.name);
        } else {
            labelEquipShield.setText("");
        }
    }
}
