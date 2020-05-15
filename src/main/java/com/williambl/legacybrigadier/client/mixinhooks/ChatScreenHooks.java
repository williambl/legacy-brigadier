package com.williambl.legacybrigadier.client.mixinhooks;

import java.util.List;

public interface ChatScreenHooks {
    String getMessage();
    void setMessage(String newMessage);
    void setCompletions(List<String> completions);
}
