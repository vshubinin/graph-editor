package de.tesis.dynaware.grapheditor.demo;

import de.tesis.dynaware.grapheditor.GraphEditor;
import de.tesis.dynaware.grapheditor.core.view.GraphEditorContainer;
import de.tesis.dynaware.grapheditor.demo.selections.SelectionCopier;
import de.tesis.dynaware.grapheditor.model.GNode;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.eclipse.emf.common.util.EList;

import java.util.stream.Collectors;

public class NodeList {
    public ListView<String> nodes;
    private GraphEditor graphEditor;
    private SelectionCopier selectionCopier;
    private GraphEditorContainer graphEditorContainer;
    private ObservableList<String> items = FXCollections.observableArrayList();

    public void setGraphEditor(GraphEditor graphEditor) {
        this.graphEditor = graphEditor;
    }

    public void setSelectionCopier(SelectionCopier selectionCopier) {
        this.selectionCopier = selectionCopier;
    }

    public void setGraphEditorContainer(GraphEditorContainer graphEditorContainer) {
        this.graphEditorContainer = graphEditorContainer;
    }

    public ObservableList<String> getItems() {
        return items;
    }

    @FXML
    public void initialize() {
        nodes.setEditable(false);
        nodes.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s1, String s2) {
                String id = s2.substring(0, s2.indexOf(":"));
                EList<GNode> nodes = graphEditor.getModel().getNodes();
                for (GNode node : nodes) {
                    if (node.getId().equals(id)) {
                        graphEditor.getSelectionManager().select(node);
                        break;
                    }
                }
            }
        });

        items.addAll(graphEditor.getModel().getNodes()
                .stream()
                .map(node -> node.getId() + ":" + node.getType() + " x: " + node.getX() + " y: " + node.getY())
                .collect(Collectors.toList()));

        nodes.setItems(items);
    }
}
