package command;




public interface ServerProtocol extends Protocol {

  

    void respond(String gameList);

    void give(String player, String tiles);

    void turn(String player);

    void move(String player, String tile, String index);

    void swap(String player, String tile);

    void skip(String player);

    void end(String player, String... players);

    void exception(String message);

    

}
