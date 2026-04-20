package io.github.hanhy06.embellishchat.util;

public class MessageBlockedException extends RuntimeException{
    public MessageBlockedException() {}

    public MessageBlockedException(String message) {
        super(message);
    }

    public MessageBlockedException(String message, Throwable cause){
        super(message,cause);
    }

    public MessageBlockedException(String message, Object args){
        super(String.format(message,args));
    }
}
