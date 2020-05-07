/*
 * Copyright (C) 2005 - 2014 by TESIS DYNAware GmbH
 */
package de.tesis.dynaware.grapheditor.core.skins.defaults;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tesis.dynaware.grapheditor.GConnectorSkin;
import de.tesis.dynaware.grapheditor.GConnectorStyle;
import de.tesis.dynaware.grapheditor.core.connectors.DefaultConnectorTypes;
import de.tesis.dynaware.grapheditor.core.skins.defaults.utils.AnimatedColor;
import de.tesis.dynaware.grapheditor.core.skins.defaults.utils.ColorAnimationUtils;
import de.tesis.dynaware.grapheditor.model.GConnector;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;

/**
 * The default connector skin.
 *
 * <p>
 * A connector that uses this skin must have one of the 8 types defined in {@link DefaultConnectorTypes}. If the
 * connector does not have one of these types, it will be set to <b>left-input</b>.
 * </p>
 */
public class DefaultConnectorSkin extends GConnectorSkin {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultConnectorSkin.class);

    private static final String STYLE_CLASS_BASE = "tree-input-connector";
    private static final String STYLE_CLASS_BACKGROUND = "default-node-background";
    private static final String NUMBER_FIRST_COMMAND = "00F4";
    private static final String NUMBER_NODE = "12";

    private static final PseudoClass PSEUDO_CLASS_ALLOWED = PseudoClass.getPseudoClass("allowed");
    private static final PseudoClass PSEUDO_CLASS_FORBIDDEN = PseudoClass.getPseudoClass("forbidden");

    private static final String ALLOWED = "-animated-color-allowed";
    private static final String FORBIDDEN = "-animated-color-forbidden";

    private static final double LINE_SPACE = 7;

    private static final double RADIUS = 7;

    private final Pane root = new Pane();
    private final Line line = new Line();
    //Creating a Text object
    private final Text text = new Text();
    private static int count = 1;
    private final Rectangle rectangle = new Rectangle();
    private final Circle circle = new Circle(RADIUS);

    private final AnimatedColor animatedColorAllowed;
    private final AnimatedColor animatedColorForbidden;

    /**
     * Creates a new default connector skin instance.
     *
     * @param connector the {@link GConnector} the skin is being created for
     */
    public DefaultConnectorSkin(final GConnector connector) {

        super(connector);

        performChecks();
        root.setMinSize(2 * RADIUS, 2 * RADIUS);
        root.setPrefSize(2 * RADIUS, 2 * RADIUS);
        root.setMaxSize(2 * RADIUS, 2 * RADIUS);

        root.setPickOnBounds(false);


        if (getItem().getId() != null && (getItem().getId().equals("buttom")
                || getItem().getId().equals("top"))) {
            rectangle.setManaged(false);
            rectangle.getStyleClass().setAll(STYLE_CLASS_BACKGROUND);
            rectangle.setX(20);
            rectangle.setWidth(60);
            rectangle.setHeight(15);
            if (getItem().getId().equals("buttom")) {
                text.setText(NUMBER_NODE);
                rectangle.setY(-12);
                text.setX(rectangle.getX() + 25);
            } else {
                text.setText(NUMBER_FIRST_COMMAND);
                rectangle.setY(12);
                text.setY(rectangle.getY() + 12);
                text.setX(rectangle.getX() + 11);
            }

            root.getChildren().add(text);
            root.getChildren().add(rectangle);
        } else {
            circle.setManaged(false);
            circle.resizeRelocate(0, 0, 2 * RADIUS, 2 * RADIUS);
            circle.getStyleClass().setAll(STYLE_CLASS_BASE);

            line.setManaged(false);
            drawConnector(line, circle, rectangle, connector);
            if (getItem().getId() != null && getItem().getId().equals("invers")) {
                root.getChildren().add(circle);
            }

            if (connector.getType().equals(DefaultConnectorTypes.LEFT_INPUT)) {
                text.setText("di" + count + "                   00" + count);
                text.setX(rectangle.getX()+10);
            } else {
                text.setText("00" + count + "                   " + "di" + count);
                text.setX(rectangle.getX()-92);
            }
            count++;
            text.setY(rectangle.getY());

            root.getChildren().add(text);
            root.getChildren().add(line);
            root.getChildren().add(rectangle);
        }
        animatedColorAllowed = new AnimatedColor(ALLOWED, Color.WHITE, Color.MEDIUMSEAGREEN, Duration.millis(500));
        animatedColorForbidden = new AnimatedColor(FORBIDDEN, Color.WHITE, Color.TOMATO, Duration.millis(500));
    }


    @Override
    public Node getRoot() {
        return root;
    }

    @Override
    public double getWidth() {
        return 2 * RADIUS;
    }

    @Override
    public double getHeight() {
        return 2 * RADIUS;
    }

    @Override
    public void applyStyle(final GConnectorStyle style) {

        switch (style) {

            case DEFAULT:
                ColorAnimationUtils.removeAnimation(circle);
                circle.pseudoClassStateChanged(PSEUDO_CLASS_FORBIDDEN, false);
                circle.pseudoClassStateChanged(PSEUDO_CLASS_ALLOWED, false);
                break;

            case DRAG_OVER_ALLOWED:
                ColorAnimationUtils.animateColor(circle, animatedColorAllowed);
                circle.pseudoClassStateChanged(PSEUDO_CLASS_FORBIDDEN, false);
                circle.pseudoClassStateChanged(PSEUDO_CLASS_ALLOWED, true);
                break;

            case DRAG_OVER_FORBIDDEN:
                ColorAnimationUtils.animateColor(circle, animatedColorForbidden);
                circle.pseudoClassStateChanged(PSEUDO_CLASS_FORBIDDEN, true);
                circle.pseudoClassStateChanged(PSEUDO_CLASS_ALLOWED, false);
                break;
        }
    }

    /**
     * Draws the given line to have a connector shape.
     *
     * @param connector the connector
     * @param line      the line to be drawn
     */
    public static void drawConnector(final Line line, Circle circle, Rectangle rectangle,
                                     GConnector connector) {
        if (connector.getType().equals(DefaultConnectorTypes.RIGHT_OUTPUT)) {
            if (connector.getId() != null && connector.getId().equals("invers")) {
                line.setStartX(circle.getRadius() + LINE_SPACE);
            } else {
                line.setStartX(circle.getRadius() - 2);
            }
            line.setStartY(circle.getRadius());
            line.setEndX(100.0f);
            line.setEndY(circle.getRadius());

            rectangle.setX(line.getEndX());
            rectangle.setY(line.getEndY() - 3);
            rectangle.setWidth(LINE_SPACE);
            rectangle.setHeight(LINE_SPACE);
        } else {
            if (connector.getId() != null && connector.getId().equals("invers")) {
                line.setStartX(circle.getRadius() - 8);
            } else {
                line.setStartX(circle.getRadius() - 2);
            }
            line.setStartY(circle.getRadius());
            line.setEndX(-96.5f);
            line.setEndY(circle.getRadius());

            rectangle.setX(line.getEndX() - 1);
            rectangle.setY(line.getEndY() - 3);
            rectangle.setWidth(LINE_SPACE);
            rectangle.setHeight(LINE_SPACE);
        }
    }

    /**
     * Draws the given polygon to have a triangular shape.
     *
     * @param type    the connector type
     * @param polygon the polygon to be drawn
     */
    public static void drawTriangleConnector(final String type, final Polygon polygon) {

        switch (type) {

            case DefaultConnectorTypes.TOP_INPUT:
                drawVertical(false, polygon);
                break;

            case DefaultConnectorTypes.TOP_OUTPUT:
                drawVertical(true, polygon);
                break;

            case DefaultConnectorTypes.RIGHT_INPUT:
                drawHorizontal(false, polygon);
                break;

            case DefaultConnectorTypes.RIGHT_OUTPUT:
                drawHorizontal(true, polygon);
                break;

            case DefaultConnectorTypes.BOTTOM_INPUT:
                drawVertical(true, polygon);
                break;

            case DefaultConnectorTypes.BOTTOM_OUTPUT:
                drawVertical(false, polygon);
                break;

            case DefaultConnectorTypes.LEFT_INPUT:
                drawHorizontal(true, polygon);
                break;

            case DefaultConnectorTypes.LEFT_OUTPUT:
                drawHorizontal(false, polygon);
                break;
        }
    }

    /**
     * Draws the polygon for a horizontal orientation, pointing right or left.
     *
     * @param pointingRight {@code true} to point right, {@code false} to point left
     * @param polygon       the polygon to be drawn
     */
    private static void drawHorizontal(final boolean pointingRight, final Polygon polygon) {

        if (pointingRight) {
            polygon.getPoints().addAll(new Double[]{0D, 0D, RADIUS, RADIUS / 2, 0D, RADIUS});
        } else {
            polygon.getPoints().addAll(new Double[]{RADIUS, 0D, RADIUS, RADIUS, 0D, RADIUS / 2});
        }
    }

    /**
     * Draws the polygon for a vertical orientation, pointing up or down.
     *
     * @param pointingUp {@code true} to point up, {@code false} to point down
     * @param polygon    the polygon to be drawn
     */
    private static void drawVertical(final boolean pointingUp, final Polygon polygon) {

        if (pointingUp) {
            polygon.getPoints().addAll(new Double[]{RADIUS / 2, 0D, RADIUS, RADIUS, 0D, RADIUS});
        } else {
            polygon.getPoints().addAll(new Double[]{0D, 0D, RADIUS, 0D, RADIUS / 2, RADIUS});
        }
    }

    /**
     * Checks that the connector has the correct values to be displayed using this skin.
     */
    private void performChecks() {
        if (!DefaultConnectorTypes.isValid(getItem().getType())) {
            LOGGER.error("Connector type '{}' not recognized, setting to 'left-input'.", getItem().getType());
            getItem().setType(DefaultConnectorTypes.LEFT_INPUT);
        }
    }

    @Override
    protected void selectionChanged(boolean isSelected) {
        // Not implemented
    }
}
