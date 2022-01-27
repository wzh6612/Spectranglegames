package command;


import gameController.Player;

public interface ClientProtocol extends Protocol {

    

    void connect(Player player);

    void create();

    void join();

    void join(String game);


    void create(String maxPlayers);

    void start();

    void move(String tile, String index);

    void swap(String tile);

    void skip();

    public void skip(String username);

    void leave();

    void disconnect();

    

}
