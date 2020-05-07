package de.tesis.dynaware.grapheditor.demo.customskins;

import de.tesis.dynaware.grapheditor.*;
import de.tesis.dynaware.grapheditor.core.connectors.DefaultConnectorTypes;
import de.tesis.dynaware.grapheditor.core.view.GraphEditorContainer;
import de.tesis.dynaware.grapheditor.demo.selections.SelectionCopier;
import de.tesis.dynaware.grapheditor.model.*;
import javafx.geometry.Side;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySchemeSkinController implements SkinController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MySchemeSkinController.class);
    private static final int MAX_CONNECTOR_COUNT = 10;
    private final GraphEditor graphEditor;
    private final GraphEditorContainer graphEditorContainer;

    public MySchemeSkinController(GraphEditor graphEditor, GraphEditorContainer graphEditorContainer) {
        this.graphEditor = graphEditor;
        this.graphEditorContainer = graphEditorContainer;
    }

    @Override
    public void addNode(double currentZoomFactor) {
        double windowXOffset = graphEditorContainer.getContentX() / currentZoomFactor;
        double windowYOffset = graphEditorContainer.getContentY() / currentZoomFactor;

        GNode node = GraphFactory.eINSTANCE.createGNode();
        // TODO centralize
        node.setY(graphEditorContainer.getMouseEvent().getSceneY() + windowYOffset);

        GConnector rightOutput = GraphFactory.eINSTANCE.createGConnector();
        node.getConnectors().add(rightOutput);

        GConnector leftInput = GraphFactory.eINSTANCE.createGConnector();
        node.getConnectors().add(leftInput);

        GConnector leftInputNext = GraphFactory.eINSTANCE.createGConnector();
        node.getConnectors().add(leftInputNext);

        leftInputNext.setId("invers");

        GConnector top = GraphFactory.eINSTANCE.createGConnector();
        node.getConnectors().add(top);

        top.setId("top");

        GConnector buttom = GraphFactory.eINSTANCE.createGConnector();
        node.getConnectors().add(buttom);

        buttom.setId("buttom");

        node.setX(graphEditorContainer.getMouseEvent().getSceneX() + windowXOffset);

        rightOutput.setType(DefaultConnectorTypes.RIGHT_OUTPUT);
        leftInput.setType(DefaultConnectorTypes.LEFT_INPUT);
        leftInputNext.setType(DefaultConnectorTypes.LEFT_INPUT);
        top.setType(DefaultConnectorTypes.TOP_OUTPUT);
        buttom.setType(DefaultConnectorTypes.BOTTOM_OUTPUT);

        Commands.addNode(graphEditor.getModel(), node);
    }

    @Override
    public void activate() {
        graphEditor.setNodeSkinFactory(this::createSkin);
        graphEditor.setConnectorSkinFactory(this::createSkin);
        graphEditorContainer.getMinimap().setConnectionFilter(c -> true);
    }

    private GNodeSkin createSkin(GNode gNode) {
        return new AndNodeSkin(gNode);
    }

    private GConnectorSkin createSkin(GConnector gConnector){
        return  new AndConnectorSkin(gConnector);
    }

    /**
     * Adds a connector of the given type to all nodes that are currently selected.
     *
     * @param position the position of the new connector
     * @param input {@code true} for input, {@code false} for output
     */
    @Override
    public void addConnector(final Side position, final boolean input) {

        final String type = getType(position, input);

        final GModel model = graphEditor.getModel();
        final SkinLookup skinLookup = graphEditor.getSkinLookup();
        final CompoundCommand command = new CompoundCommand();
        final EditingDomain editingDomain = AdapterFactoryEditingDomain.getEditingDomainFor(model);

        for (final GNode node : model.getNodes()) {

            if (skinLookup.lookupNode(node).isSelected()) {
                if (countConnectors(node, position) < MAX_CONNECTOR_COUNT) {

                    final GConnector connector = GraphFactory.eINSTANCE.createGConnector();
                    connector.setType(type);

                    final EReference connectors = GraphPackage.Literals.GNODE__CONNECTORS;
                    command.append(AddCommand.create(editingDomain, node, connectors, connector));
                }
            }
        }

        if (command.canExecute()) {
            editingDomain.getCommandStack().execute(command);
        }
    }

    @Override
    public void clearConnectors() {
        Commands.clearConnectors(graphEditor.getModel(), graphEditor.getSelectionManager().getSelectedNodes());
    }

    @Override
    public void handlePaste(final SelectionCopier selectionCopier) {
        selectionCopier.paste(null);
    }

    @Override
    public void handleSelectAll() {
        graphEditor.getSelectionManager().selectAll();
    }

    /**
     * Counts the number of connectors the given node currently has of the given type.
     *
     * @param node a {@link GNode} instance
     * @param side the {@link Side} the connector is on
     * @return the number of connectors this node has on the given side
     */
    private int countConnectors(final GNode node, final Side side) {

        int count = 0;

        for (final GConnector connector : node.getConnectors()) {
            if (side.equals(DefaultConnectorTypes.getSide(connector.getType()))) {
                count++;
            }
        }

        return count;
    }

    /**
     * Gets the connector type string corresponding to the given position and input values.
     *
     * @param position a {@link Side} value
     * @param input {@code true} for input, {@code false} for output
     * @return the connector type corresponding to these values
     */
    private String getType(final Side position, final boolean input)
    {
        switch (position)
        {
            case TOP:
                if (input)
                {
                    return DefaultConnectorTypes.TOP_INPUT;
                }
                return DefaultConnectorTypes.TOP_OUTPUT;
            case RIGHT:
                if (input)
                {
                    return DefaultConnectorTypes.RIGHT_INPUT;
                }
                return DefaultConnectorTypes.RIGHT_OUTPUT;
            case BOTTOM:
                if (input)
                {
                    return DefaultConnectorTypes.BOTTOM_INPUT;
                }
                return DefaultConnectorTypes.BOTTOM_OUTPUT;
            case LEFT:
                if (input)
                {
                    return DefaultConnectorTypes.LEFT_INPUT;
                }
                return DefaultConnectorTypes.LEFT_OUTPUT;
        }
        return null;
    }
}
