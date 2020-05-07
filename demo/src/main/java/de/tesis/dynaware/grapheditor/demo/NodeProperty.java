package de.tesis.dynaware.grapheditor.demo;

import java.util.List;

public class NodeProperty {
    private String command;
    private String type;
    private String title1;
    private String title2;
    private String relationship;

    public NodeProperty() {
        
    }

    public NodeProperty(String command, String type, String title1, String title2, String relationship) {
        this.command = command;
        this.type = type;
        this.title1 = title1;
        this.title2 = title2;
        this.relationship = relationship;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle1() {
        return title1;
    }

    public void setTitle1(String title1) {
        this.title1 = title1;
    }

    public String getTitle2() {
        return title2;
    }

    public void setTitle2(String title2) {
        this.title2 = title2;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }
}
