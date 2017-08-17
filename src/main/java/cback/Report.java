package cback;

import sx.blah.discord.handle.obj.IMessage;

public class Report {
    private IMessage message;
    private Exception e;

    public Report(IMessage message, Exception e) {
        this.message = message;
        this.e = e;
    }

    public IMessage getMessage() {
        return message;
    }

    public Exception getException() {
        return e;
    }
}
