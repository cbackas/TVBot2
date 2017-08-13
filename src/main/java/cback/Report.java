package cback;

import sx.blah.discord.handle.obj.IMessage;

public class Report {
    private IMessage message;
    private Exception e;

    private Report(IMessage message, Exception e) {
        this.message = message;
        this.e = e;
    }

    public IMessage getMessage() {
        return message;
    }

    public void setMessage(IMessage message) {
        this.message = message;
    }

    public Exception getException() {
        return e;
    }

    public void setException(Exception e) {
        this.e = e;
    }
}
