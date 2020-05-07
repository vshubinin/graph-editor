/*
 * Copyright (C) 2005 - 2014 by TESIS DYNAware GmbH
 */
package de.tesis.dynaware.grapheditor.demo;

import java.net.URL;

import de.tesis.dynaware.grapheditor.GraphEditor;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * A demo application to show uses of the {@link GraphEditor} library.
 */
public class GraphEditorDemo extends Application {

    public static Stage primary;

    private static final String APPLICATION_TITLE = "Graph Editor Demo"; //$NON-NLS-1$
    private static final String DEMO_STYLESHEET = "demo.css"; //$NON-NLS-1$
    private static final String TREE_SKIN_STYLESHEET = "treeskins.css"; //$NON-NLS-1$
    private static final String TITLED_SKIN_STYLESHEET = "titledskins.css"; //$NON-NLS-1$
    private static final String FONT_AWESOME = "fontawesome.ttf"; //$NON-NLS-1$

    @Override
    public void start(final Stage stage) throws Exception {

        primary = stage;

        final URL location = getClass().getResource("GraphEditorDemo.fxml"); //$NON-NLS-1$
        final FXMLLoader loader = new FXMLLoader();
        final Parent root = loader.load(location.openStream());

        final Scene scene = new Scene(root, 830, 630);

        scene.getStylesheets().add(getClass().getResource(DEMO_STYLESHEET).toExternalForm());
        scene.getStylesheets().add(getClass().getResource(TREE_SKIN_STYLESHEET).toExternalForm());
        scene.getStylesheets().add(getClass().getResource(TITLED_SKIN_STYLESHEET).toExternalForm());
        Font.loadFont(getClass().getResource(FONT_AWESOME).toExternalForm(), 12);

        stage.setScene(scene);
        stage.setTitle(APPLICATION_TITLE);

        stage.show();
    }

    public static void main(final String[] args) {
        launch(args);
    }
}