/*
 * Copyright (C) 2005 - 2014 by TESIS DYNAware GmbH
 */
package de.tesis.dynaware.grapheditor.demo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.tesis.dynaware.grapheditor.demo.customskins.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;

import de.tesis.dynaware.grapheditor.Commands;
import de.tesis.dynaware.grapheditor.GraphEditor;
import de.tesis.dynaware.grapheditor.core.DefaultGraphEditor;
import de.tesis.dynaware.grapheditor.core.skins.defaults.connection.SimpleConnectionSkin;
import de.tesis.dynaware.grapheditor.core.view.GraphEditorContainer;
import de.tesis.dynaware.grapheditor.demo.customskins.titled.TitledSkinConstants;
import de.tesis.dynaware.grapheditor.demo.customskins.tree.TreeConnectorValidator;
import de.tesis.dynaware.grapheditor.demo.customskins.tree.TreeSkinConstants;
import de.tesis.dynaware.grapheditor.demo.selections.SelectionCopier;
import de.tesis.dynaware.grapheditor.demo.utils.AwesomeIcon;
import de.tesis.dynaware.grapheditor.model.GModel;
import de.tesis.dynaware.grapheditor.model.GNode;
import de.tesis.dynaware.grapheditor.model.GraphFactory;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.SetChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the {@link GraphEditorDemo} application.
 */
public class GraphEditorDemoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphEditorDemoController.class);

    private static final String STYLE_CLASS_TITLED_SKINS = "titled-skins"; //$NON-NLS-1$

    public CheckMenuItem viewNodeList;
    @FXML
    private RadioMenuItem myScheme;
    @FXML
    private MenuItem preferencesButton;
    @FXML
    private Menu addNodeMenu;
    @FXML
    private MenuItem generateFileButton;
    @FXML
    private MenuItem refreshSchemeButton;
    @FXML
    private AnchorPane root;
    @FXML
    private MenuBar menuBar;
    @FXML
    private MenuItem addConnectorButton;
    @FXML
    private MenuItem clearConnectorsButton;
    @FXML
    private Menu connectorTypeMenu;
    @FXML
    private Menu connectorPositionMenu;
    @FXML
    private RadioMenuItem inputConnectorTypeButton;
    @FXML
    private RadioMenuItem inputInversConnectorTypeButton;
    @FXML
    private RadioMenuItem outputConnectorTypeButton;
    @FXML
    private RadioMenuItem leftConnectorPositionButton;
    @FXML
    private RadioMenuItem rightConnectorPositionButton;
    @FXML
    private RadioMenuItem topConnectorPositionButton;
    @FXML
    private RadioMenuItem bottomConnectorPositionButton;
    @FXML
    private RadioMenuItem showGridButton;
    @FXML
    private RadioMenuItem snapToGridButton;
    @FXML
    private RadioMenuItem readOnlyButton;
    @FXML
    private RadioMenuItem defaultSkinButton;
    @FXML
    private RadioMenuItem treeSkinButton;
    @FXML
    private RadioMenuItem titledSkinButton;
    @FXML
    private Menu intersectionStyle;
    @FXML
    private RadioMenuItem gappedStyleButton;
    @FXML
    private RadioMenuItem detouredStyleButton;
    @FXML
    private ToggleButton minimapButton;
    @FXML
    private GraphEditorContainer graphEditorContainer;

    private final GraphEditor graphEditor = new DefaultGraphEditor();
	private final SelectionCopier selectionCopier = new SelectionCopier(graphEditor.getSkinLookup(),
			graphEditor.getSelectionManager());
    private final GraphEditorPersistence graphEditorPersistence = new GraphEditorPersistence();

    private DefaultSkinController defaultSkinController;
    private TreeSkinController treeSkinController;
    private TitledSkinController titledSkinController;
    private MySchemeSkinController mySchemeSkinController;

    private final ObjectProperty<SkinController> activeSkinController = new SimpleObjectProperty<>()
    {

        @Override
        protected void invalidated() {
            super.invalidated();
            if(get() != null) {
                get().activate();
            }
        }

    };

    /**
     * Called by JavaFX when FXML is loaded.
     */
    public void initialize() {

        final GModel model = GraphFactory.eINSTANCE.createGModel();

        graphEditor.setModel(model);
        graphEditorContainer.setGraphEditor(graphEditor);

        setDetouredStyle();

        defaultSkinController = new DefaultSkinController(graphEditor, graphEditorContainer);
        treeSkinController = new TreeSkinController(graphEditor, graphEditorContainer);
        titledSkinController = new TitledSkinController(graphEditor, graphEditorContainer);
        mySchemeSkinController = new MySchemeSkinController(graphEditor, graphEditorContainer);

        activeSkinController.set(mySchemeSkinController);

		graphEditor.modelProperty().addListener((w, o, n) -> selectionCopier.initialize(n));
        selectionCopier.initialize(model);

        initializeMenuBar();
        addActiveSkinControllerListener();

        root.setOnMouseMoved(mouseEvent -> graphEditorContainer.setMouseEvent(mouseEvent));

        GraphEditorDemo.primary.setOnCloseRequest(shutdownHook());
    }

    @FXML
    public void load() {
        graphEditorPersistence.loadFromFile(graphEditor);
        checkSkinType();
    }

    @FXML
    public void loadSample() {
        defaultSkinButton.setSelected(true);
        setDefaultSkin();
        graphEditorPersistence.loadSample(graphEditor);
    }

    @FXML
    public void loadSampleLarge() {
        defaultSkinButton.setSelected(true);
        setDefaultSkin();
        graphEditorPersistence.loadSampleLarge(graphEditor);
    }

    @FXML
    public void loadTree() {
        treeSkinButton.setSelected(true);
        setTreeSkin();
        graphEditorPersistence.loadTree(graphEditor);
    }

    @FXML
    public void loadTitled() {
        titledSkinButton.setSelected(true);
        setTitledSkin();
        graphEditorPersistence.loadTitled(graphEditor);
    }

    @FXML
    public void save() {
        graphEditorPersistence.saveToFile(graphEditor);
    }

    @FXML
    public void clearAll() {
        Commands.clear(graphEditor.getModel());
    }

    @FXML
    public void exit() {
        boolean saved = handleUnsavedSchema(new WindowEvent(
                GraphEditorDemo.primary,
                WindowEvent.WINDOW_CLOSE_REQUEST));
        if (!saved) {
            Platform.exit();
        }
    }


    @FXML
    public void undo() {
        Commands.undo(graphEditor.getModel());
    }

    @FXML
    public void redo() {
        Commands.redo(graphEditor.getModel());
    }

    @FXML
    public void copy() {
        selectionCopier.copy();
    }

    @FXML
    public void paste() {
        activeSkinController.get().handlePaste(selectionCopier);
    }

    @FXML
    public void selectAll() {
        activeSkinController.get().handleSelectAll();
    }

    @FXML
    public void deleteSelection() {
        final List<EObject> selection = new ArrayList<>(graphEditor.getSelectionManager().getSelectedItems());
        graphEditor.delete(selection);
    }

    @FXML
    public void addNode() {
        activeSkinController.get().addNode(graphEditor.getView().getLocalToSceneTransform().getMxx());
    }

    @FXML
    public void addConnector() {
        activeSkinController.get().addConnector(getSelectedConnectorPosition(), inputConnectorTypeButton.isSelected());
    }

    @FXML
    public void clearConnectors() {
        activeSkinController.get().clearConnectors();
    }

    @FXML
    public void setDefaultSkin() {
        activeSkinController.set(defaultSkinController);
    }

    @FXML
    public void setTreeSkin() {
        activeSkinController.set(treeSkinController);
    }

    @FXML
    public void setTitledSkin() {
        activeSkinController.set(titledSkinController);
    }

    @FXML
    public void setGappedStyle() {

        graphEditor.getProperties().getCustomProperties().remove(SimpleConnectionSkin.SHOW_DETOURS_KEY);
        graphEditor.reload();
    }

    @FXML
    public void setDetouredStyle() {

        final Map<String, String> customProperties = graphEditor.getProperties().getCustomProperties();
        customProperties.put(SimpleConnectionSkin.SHOW_DETOURS_KEY, Boolean.toString(true));
        graphEditor.reload();
    }

    @FXML
    public void toggleMinimap() {
        graphEditorContainer.getMinimap().visibleProperty().bind(minimapButton.selectedProperty());
    }

    /**
     * Initializes the menu bar.
     */
    private void initializeMenuBar() {

        final ToggleGroup skinGroup = new ToggleGroup();
        skinGroup.getToggles().addAll(defaultSkinButton, treeSkinButton, titledSkinButton, myScheme);

        final ToggleGroup connectionStyleGroup = new ToggleGroup();
        connectionStyleGroup.getToggles().addAll(gappedStyleButton, detouredStyleButton);

        final ToggleGroup connectorTypeGroup = new ToggleGroup();
        connectorTypeGroup.getToggles().addAll(inputConnectorTypeButton, inputInversConnectorTypeButton, outputConnectorTypeButton);

        final ToggleGroup positionGroup = new ToggleGroup();
        positionGroup.getToggles().addAll(leftConnectorPositionButton, rightConnectorPositionButton);
        positionGroup.getToggles().addAll(topConnectorPositionButton, bottomConnectorPositionButton);

        graphEditor.getProperties().gridVisibleProperty().bind(showGridButton.selectedProperty());
        graphEditor.getProperties().snapToGridProperty().bind(snapToGridButton.selectedProperty());
        graphEditor.getProperties().readOnlyProperty().bind(readOnlyButton.selectedProperty());

        minimapButton.setGraphic(AwesomeIcon.MAP.node());

        final SetChangeListener<? super EObject> selectedNodesListener = change -> checkConnectorButtonsToDisable();
        graphEditor.getSelectionManager().getSelectedItems().addListener(selectedNodesListener);
        checkConnectorButtonsToDisable();
    }

    /**
     * Adds a listener to make changes to available menu options when the skin type changes.
     */
    private void addActiveSkinControllerListener() {

        activeSkinController.addListener((observable, oldValue, newValue) -> {
            handleActiveSkinControllerChange();
        });
    }

    /**
     * Enables & disables certain menu options and sets CSS classes based on the new skin type that was set active.
     */
    private void handleActiveSkinControllerChange() {

        if (treeSkinController.equals(activeSkinController.get())) {

            graphEditor.setConnectorValidator(new TreeConnectorValidator());
            graphEditor.getView().getStyleClass().remove(STYLE_CLASS_TITLED_SKINS);
            treeSkinButton.setSelected(true);

        } else if (titledSkinController.equals(activeSkinController.get())) {

            graphEditor.setConnectorValidator(null);
            if (!graphEditor.getView().getStyleClass().contains(STYLE_CLASS_TITLED_SKINS)) {
                graphEditor.getView().getStyleClass().add(STYLE_CLASS_TITLED_SKINS);
            }
            titledSkinButton.setSelected(true);

        } else {

            graphEditor.setConnectorValidator(null);
            graphEditor.getView().getStyleClass().remove(STYLE_CLASS_TITLED_SKINS);
            defaultSkinButton.setSelected(true);
        }

        // Demo does not currently support mixing of skin types. Skins don't know how to cope with it.
        clearAll();
        flushCommandStack();
        checkConnectorButtonsToDisable();
        selectionCopier.clearMemory();
    }

    /**
     * Crudely inspects the model's first node and sets the new skin type accordingly.
     */
    private void checkSkinType() {

        if (!graphEditor.getModel().getNodes().isEmpty()) {

            final GNode firstNode = graphEditor.getModel().getNodes().get(0);
            final String type = firstNode.getType();

            if (TreeSkinConstants.TREE_NODE.equals(type)) {
                activeSkinController.set(treeSkinController);
            } else if (TitledSkinConstants.TITLED_NODE.equals(type)) {
                activeSkinController.set(titledSkinController);
            } else if (AndNodeSkin.AND_NODE_TYPE.equals(type)) {
                activeSkinController.set(mySchemeSkinController);
            } else {
                activeSkinController.set(defaultSkinController);
            }
        }
    }

    /**
     * Checks if the connector buttons need disabling (e.g. because no nodes are selected).
     */
    private void checkConnectorButtonsToDisable() {

		final boolean nothingSelected = graphEditor.getSelectionManager().getSelectedItems().stream()
				.noneMatch(e -> e instanceof GNode);

        final boolean treeSkinActive = treeSkinController.equals(activeSkinController.get());
        final boolean titledSkinActive = titledSkinController.equals(activeSkinController.get());

        if (titledSkinActive || treeSkinActive) {
            addConnectorButton.setDisable(true);
            clearConnectorsButton.setDisable(true);
            connectorTypeMenu.setDisable(true);
            connectorPositionMenu.setDisable(true);
        } else if (nothingSelected) {
            addConnectorButton.setDisable(true);
            clearConnectorsButton.setDisable(true);
            connectorTypeMenu.setDisable(false);
            connectorPositionMenu.setDisable(false);
        } else {
            addConnectorButton.setDisable(false);
            clearConnectorsButton.setDisable(false);
            connectorTypeMenu.setDisable(false);
            connectorPositionMenu.setDisable(false);
        }

        intersectionStyle.setDisable(treeSkinActive);
    }

    /**
     * Flushes the command stack, so that the undo/redo history is cleared.
     */
    private void flushCommandStack() {

        final EditingDomain editingDomain = AdapterFactoryEditingDomain.getEditingDomainFor(graphEditor.getModel());
        if (editingDomain != null) {
            editingDomain.getCommandStack().flush();
        }
    }

    /**
     * Gets the side corresponding to the currently selected connector position in the menu.
     *
     * @return the {@link Side} corresponding to the currently selected connector position
     */
    private Side getSelectedConnectorPosition() {

        if (leftConnectorPositionButton.isSelected()) {
            return Side.LEFT;
        } else if (rightConnectorPositionButton.isSelected()) {
            return Side.RIGHT;
        } else if (topConnectorPositionButton.isSelected()) {
            return Side.TOP;
        } else {
            return Side.BOTTOM;
        }
    }


    @FXML
    public void setMyScheme(ActionEvent actionEvent) {
        activeSkinController.set(mySchemeSkinController);
    }

    @FXML
    public void setPreferences(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass()
                    .getResourceAsStream("/de/tesis/dynaware/grapheditor/demo/Preferences.fxml"));
            var stage = new Stage();
            stage.setTitle("Preferences");
            stage.setScene(new Scene(root, Property.PREFERENCES_WIDTH.getInt(), Property.PREFERENCES_HEIGHT.getInt()));
            stage.setResizable(false);
            stage.initOwner(GraphEditorDemo.primary);
            stage.show();
        } catch (IOException e) {
            LOGGER.error("could nod load preferences", e);
            throw new GraphEditorException(e);
        }
    }

    public void viewNodeList(ActionEvent actionEvent) {
        if (viewNodeList.isSelected()) {
            try {
                FXMLLoader loader = new FXMLLoader();
                NodeList controller = new NodeList();
                controller.setGraphEditor(graphEditor);
                controller.setGraphEditorContainer(graphEditorContainer);
                controller.setSelectionCopier(selectionCopier);

                loader.setController(controller);
                Parent root = loader.load(getClass()
                        .getResourceAsStream("/de/tesis/dynaware/grapheditor/demo/NodeList.fxml"));

                var stage = new Stage();
                stage.setTitle("Nodes");
                stage.setScene(new Scene(root, Property.NODES_VIEW_WIDTH.getInt(), Property.NODES_VIEW_HEIGHT.getInt()));
                stage.initOwner(GraphEditorDemo.primary);
                stage.show();
            } catch (IOException e) {
                LOGGER.error("could nod load node list", e);
                throw new GraphEditorException(e);
            }
        }
    }

    public EventHandler<WindowEvent> shutdownHook() {
        return this::handleUnsavedSchema;
    }

    private boolean handleUnsavedSchema(WindowEvent event) {
        boolean itCanBePersisted = Optional.ofNullable(graphEditor.getModel())
                .map(GModel::getNodes)
                .map(nodes -> !nodes.isEmpty())
                .orElse(false);

        boolean updated = Optional.ofNullable(graphEditor.getModel())
                .map(GModel::isUpdated)
                .orElse(false);

        if (itCanBePersisted && updated) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("You have one unsaved schema");
            alert.setContentText("Do you want to save the open schema before leaving?");

            ButtonType yes = new ButtonType("Yes");
            ButtonType no = new ButtonType("No");
            ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(yes, no, cancel);

            ButtonType result = alert.showAndWait().orElse(null);
            if (result == yes) {
                graphEditorPersistence.saveToFile(graphEditor);
            } else if (result == no) {
                Platform.exit();
                System.exit(0);
            } else {
                event.consume();
            }
            return true;
        }
        return false;
    }
}
