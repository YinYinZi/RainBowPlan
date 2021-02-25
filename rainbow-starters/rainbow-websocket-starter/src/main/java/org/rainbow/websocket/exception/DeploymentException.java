package org.rainbow.websocket.exception;

/**
 * @author K
 * @date 2021/2/24  10:11
 */
public class DeploymentException extends Exception {
    private static final long serialVersionUID = 1L;

    public DeploymentException(String message) {
        super(message);
    }

    public DeploymentException(String message, Throwable cause) {
        super(message, cause);
    }
}
