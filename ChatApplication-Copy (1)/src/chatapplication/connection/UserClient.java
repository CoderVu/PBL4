/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatapplication.connection;

/**
 *
 * @author root
 */
public class UserClient {
    
    private String username;
    private ClientHandler clientHandler;
    
    public UserClient(String username, ClientHandler clientHandler){
        this.username = username;
        this.clientHandler = clientHandler;
    }
    public String getUsername(){
        return username;
    }
    public ClientHandler getClient(){
        return clientHandler;
    }
}
