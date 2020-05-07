package de.tesis.dynaware.grapheditor.demo;

import de.tesis.dynaware.grapheditor.GNodeSkin;
import de.tesis.dynaware.grapheditor.core.connectors.DefaultConnectorTypes;
import de.tesis.dynaware.grapheditor.model.GConnector;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.eclipse.emf.common.util.EList;

import java.util.ArrayList;
import java.util.List;

public class NodeProperties {

    public TableView<NodeProperty> propertiesTable;
    public Button okButton;
    public Button cancelButton;
    public Label positionLabel;
    public TextField amountInput;
    public TextField amountOutput;
    public TextField serialNumber;
    public TableColumn<NodeProperty, String> commandColumn;
    public TableColumn<NodeProperty, Prop> typeColumn;
    public TableColumn<NodeProperty, String> title1Column;
    public TableColumn<NodeProperty, String> title2Column;
    public TableColumn<NodeProperty, String> relationshipColumn;

    private GNodeSkin skin;
    private int countInput = 0;
    private int countOutput = 0;


    @FXML
    public void initialize() {
        positionLabel.setText(new NodeGeometryWrapper(skin.getItem()).render());

        EList<GConnector> connectors = skin.getItem().getConnectors();

        serialNumber.setText("00" + String.valueOf(connectors.size()-1));

        ObservableList<Prop> propertyList = FXCollections.observableArrayList(Prop.values());

        propertiesTable.setEditable(true);

        typeColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<NodeProperty,Prop>, ObservableValue<Prop>>() {

            @Override
            public ObservableValue<Prop> call(TableColumn.CellDataFeatures<NodeProperty, Prop> param) {
                NodeProperty nodeProperty = param.getValue();

                String code = nodeProperty.getType();
                if (code.equals("left-input") || code.equals("invers")) {
                    countInput += 1;
                    amountInput.setText(String.valueOf(countInput));
                } else {
                    countOutput += 1;
                    amountOutput.setText(String.valueOf(countOutput-1));
                }

                Prop prop = Prop.getByCode(code);
                return new SimpleObjectProperty<Prop>(prop);
            }
        });

        typeColumn.setCellFactory(ComboBoxTableCell.forTableColumn(propertyList));
        typeColumn.setOnEditCommit((TableColumn.CellEditEvent<NodeProperty, Prop> event) -> {
            TablePosition<NodeProperty, Prop> pos = event.getTablePosition();
            Prop newProp = event.getNewValue();
            int row = pos.getRow();
            NodeProperty nodeProperty = event.getTableView().getItems().get(row);
            nodeProperty.setType(newProp.toString());
            connectors.get(row).setType(newProp.toString());
        });

        commandColumn.setCellValueFactory(new PropertyValueFactory<>("command"));
        title1Column.setCellValueFactory(new PropertyValueFactory<>("title1"));
        title2Column.setCellValueFactory(new PropertyValueFactory<>("title2"));
        relationshipColumn.setCellValueFactory(new PropertyValueFactory<>("relationship"));

        if (connectors == null) {
            return;
        }

        for (GConnector connector : connectors) {
            if(!connector.getType().equals(DefaultConnectorTypes.TOP_OUTPUT)
                    && !connector.getType().equals(DefaultConnectorTypes.BOTTOM_OUTPUT)) {
                propertiesTable.getItems().add(new NodeProperty(
                        connector.getCommand(),
                        connector.getId()==null?connector.getType():connector.getId(),
                        String.valueOf(connector.getProperty()),
                        String.valueOf(connector.getParam()),
                        connector.getConnections().size() == 0 ? "no" : String.valueOf(connector.getConnections().size())
                ));
            }
        }
    }


    public void cancel(ActionEvent actionEvent) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void ok(ActionEvent actionEvent) {
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
        amountInput.setText(String.valueOf(0));
        amountOutput.setText(String.valueOf(0));
    }

    public void setSkin(GNodeSkin skin) {
        this.skin = skin;
    }

    private enum Prop{
        INPUT("INPUT"),
        OUTPUT("OUTPUT"),
        INPUT_N("INPUT-N");
        private String code;
        private static String input = "left-input";


        Prop(String code){
           this.code = code;
        }

        public static Prop getByCode(String code) {

            if (code.equals(input)|| code.equals("INPUT")) {
                return Prop.INPUT;
            } else if (code.equals("invers")) {
                return Prop.INPUT_N;
            } else {
                return Prop.OUTPUT;
            }


        }
    }
}
