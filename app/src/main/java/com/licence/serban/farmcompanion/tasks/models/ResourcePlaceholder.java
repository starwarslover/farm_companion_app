package com.licence.serban.farmcompanion.tasks.models;

/**
 * Created by Serban on 14.04.2017.
 */

public final class ResourcePlaceholder {
    private String id;
    private String name;
    private double quantity;

    public ResourcePlaceholder() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
}
