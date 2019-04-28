package de.lambdamoo.gta.client.dialog;

public interface MyDialogListener {
    /**
     * @param result
     * @return true when everything is ok and the dialog should be closed
     */
    boolean onPerform(Result result);

    public enum Result {Ok, Yes, No}
}
