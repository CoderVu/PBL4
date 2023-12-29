/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatapplication.ChatManager;

import java.util.LinkedList;
import java.util.List;

public class ChatManager {

    private List<ChatHandler> friendListChat;

    public ChatManager() {
        friendListChat = new LinkedList<>();
    }

    public ChatHandler findChatByUser(String username) {
        for (ChatHandler user : friendListChat) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null; // Handle the case when no chat is found
    }

    public void addChat(ChatHandler chatHandler) {
        this.friendListChat.add(chatHandler);
    }

    public List<ChatHandler> getFriendListChat() {
        return this.friendListChat;
    }

    public ChatHandler getFriendChatAt(int index) {
        if (index >= 0 && index < friendListChat.size()) {
            return friendListChat.get(index);
        }
        return null; // Handle index out of bounds gracefully
    }

    public ChatHandler getFriendChatByUser(String user) {
        for (ChatHandler handler : friendListChat) {
            if (handler.getUsername().equals(user)) {
                return handler;
            }
        }
        return null; // Trả về null nếu không tìm thấy
    }

    public void addMessageToChat(int index, String text) {
        ChatHandler chatHandler = getFriendChatAt(index);
        if (chatHandler != null) {
            chatHandler.getChat().append(text);
        }
    }
}
