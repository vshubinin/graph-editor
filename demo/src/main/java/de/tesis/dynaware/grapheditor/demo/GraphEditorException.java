package de.tesis.dynaware.grapheditor.demo;

public class GraphEditorException extends RuntimeException {
    public GraphEditorException() {
        super();
    }

    public GraphEditorException(String message) {
        super(message);
    }

    public GraphEditorException(String message, Throwable cause) {
        super(message, cause);
    }

    public GraphEditorException(Throwable cause) {
        super(cause);
    }
}
