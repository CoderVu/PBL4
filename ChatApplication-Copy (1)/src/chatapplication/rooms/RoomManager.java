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
public class RoomManager {

    private ArrayList<Room> rooms;

    public RoomManager() {
        rooms = new ArrayList<>();
    }

    public Room getRoomBySlt(int select) {
        for (Room room : rooms) {
            if (select == room.getsltRoom()) {
                return room;
            }
        }
        return null;
    }
    
    public Room getRoomById(int id) {
        for (Room room : rooms) {
            if (id == room.getIdROom()) {
                return room;
            }
        }
        return null;
    }

    public void addRoom(int slt, int idroom, String roomName) {
        rooms.add(new Room(slt, idroom, roomName));
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }
    // Existing methods to add, get, and find rooms.

//    public void userJoinRoom(String roomName, String username) {
//        Room room = getRoomByName(roomName);
//        if (room != null) {
//            room.addUser(username);
//        }
//    }

    public void userLeaveRoom(String roomName, String username) {
//        Room room = getRoomByName(roomName);
//        if (room != null) {
//            room.removeUser(username);
//        }
    }

}
