package com.example.nicolai.clider.model;

//Data object for keeping track of each clothe in the preference list.
public class TagItem {
    private String name;
    private int posistion;
    private boolean checked;
    private String tagName;

    public TagItem(String name, int posistion, boolean checked, String tagName) {
        this.name = name;
        this.posistion = posistion;
        this.checked = checked;
        this.tagName = tagName;
    }


    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosistion() {
        return posistion;
    }

    public void setPosistion(int posistion) {
        this.posistion = posistion;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
