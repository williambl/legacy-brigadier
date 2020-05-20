package com.williambl.legacybrigadier.client.mixinhooks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;

@Environment(EnvType.CLIENT)
public interface ChatScreenHooks {
    String getMessage();
    void setMessage(String newMessage);
    void setCompletions(List<String> completions);
}
