package com.ddhigh.sign.entity;

public class PkgEntity {
    private String name;
    public int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PkgEntity(String name) {
        this.name = name;
    }

    public PkgEntity() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        PkgEntity e = (PkgEntity) o;
        return e.getName().equals(this.getName());
    }
}
