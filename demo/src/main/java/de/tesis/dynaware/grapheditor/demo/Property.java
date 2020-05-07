package de.tesis.dynaware.grapheditor.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public enum Property {
    PREFERENCES_WIDTH("100"),
    PREFERENCES_HEIGHT("100"),
    NODE_HEIGHT("125"),
    NODE_WIDTH("125"),
    NODE_SOLID("14"),
    CONNECTOR_SIZE("5"),
    CIRCLE_SIZE("5"),
    NODE_LABEL_FONT_SIZE("14"),
    COMMAND_LABEL_FONT_SIZE("14"),
    DISPLAY_BORDERS_NODES("true"),
    PROMPT("false"),
    PROPERTIES_NODE_WIDTH("740"),
    PROPERTIES_NODE_HEIGHT("620"),
    NODES_VIEW_WIDTH("200"),
    NODES_VIEW_HEIGHT("585");

    private String val;

    Property(String val) {
        this.val = val;
    }

    public String get() {
        return val;
    }

    public boolean is() {
        return Boolean.parseBoolean(val);
    }

    public void set(String val) {
        this.val = val;
    }

    public int getInt() {
        return Integer.parseInt(val);
    }

    /**
     * Replaces default values of properties by values from settings.properties
     */
    static {
        var is = Property.class
                .getResourceAsStream("/de/tesis/dynaware/grapheditor/demo/settings.properties");
        try (is; var reader = new BufferedReader(new InputStreamReader(is))) {
            var collect = reader.lines().map(line -> line.replace(".", "_"))
                    .map(line -> line.split("="))
                    .filter(kv -> kv.length == 2)
                    .collect(Collectors.toMap(kv -> kv[0].trim(), kv -> kv[1].trim()));

            for (Property property : values()) {
                var val = collect.get(property.name());
                if (val != null) {
                    property.val = val;
                }
            }
        } catch (IOException e) {
            throw new GraphEditorException(e);
        }
    }
}
