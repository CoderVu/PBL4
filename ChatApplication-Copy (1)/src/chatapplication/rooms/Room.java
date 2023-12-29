    /*
     * To change this license header, choose License Headers in Project Properties.
     * To change this template file, choose Tools | Templates
     * and open the template in the editor.
     */
    package chatapplication.rooms;

    import java.util.ArrayList;

    /**
     *
     * @author root
     */
    public class Room {
        private int idroom;
        private String room;
        private int slt;
        private ArrayList<String> users;
        private StringBuilder chat;

        public Room(int slt, int idroom, String room){
            this.idroom = idroom;
            this.room = room;
            this.slt = slt;
            users = new ArrayList<>();
            chat = new StringBuilder();
        }
        
        public int getsltRoom(){
            return slt;
        }
        
        public int getIdROom(){
            return idroom;
        }
        public String getRoom() {
            return room;
        }
        public void setChat(StringBuilder chat){
            this.chat = chat;
        }
        public StringBuilder getChat(){
            return chat;
        }
        public void setRoom(String room) {
            this.room = room;
        }

        public ArrayList<String> getUsers() {
            return users;
        }

        public void setUsers(ArrayList<String> users) {
            this.users = users;
        }
        
        public void addUser(String username) {
            if (!users.contains(username)) {
                users.add(username);
            }
        }

        public void removeUser(String username) {
            users.remove(username);
        }


    }
