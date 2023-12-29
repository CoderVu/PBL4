package chatapplication.ChatManager;

public class ChatHandler {
    private  String username;
    private  StringBuilder chat;

    public ChatHandler(String username, StringBuilder chat) {
        if (username == null || chat == null) {
            throw new IllegalArgumentException("Username and chat must not be null.");
        }
        this.username = username;
        this.chat = new StringBuilder(chat); // Tạo một bản sao phòng thủ
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public StringBuilder getChat() {
        // Trả về một bản sao của StringBuilder chat để đảm bảo tính không thay đổi
        return new StringBuilder(chat);
    }

    @Override
    public String toString() {
        return "ChatHandler{username='" + username + "', chat='" + chat.toString() + "'}";
    }
    // Phương thức này sẽ thêm nội dung vào StringBuilder
    public void appendToChat(String text) {
        this.chat.append(text);
    }
}
