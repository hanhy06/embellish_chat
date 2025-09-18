package com.hanhy06.embellish_chat.util;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class Metadata {
    public static MutableText metadata(MutableText context){
        HoverEvent hoverEvent = new HoverEvent.ShowText(Text.literal(
                Timestamp.timeStamp() + "\nClick to copy to clipboard"
        ));

        ClickEvent clickEvent = new ClickEvent.CopyToClipboard(context.getString());

        return context.styled(style ->  style.withHoverEvent(hoverEvent).withClickEvent(clickEvent));
    }
}
