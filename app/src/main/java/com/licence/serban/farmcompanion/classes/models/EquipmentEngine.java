package com.licence.serban.farmcompanion.classes.models;

/**
 * Created by Serban on 17.05.2017.
 */

class EquipmentEngine {
    private String type;
    private int horsePower;

    public EquipmentEngine(String type, int horsePower) {
        this.type = type;
        this.horsePower = horsePower;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getHorsePower() {
        return horsePower;
    }

    public void setHorsePower(int horsePower) {
        this.horsePower = horsePower;
    }
}
