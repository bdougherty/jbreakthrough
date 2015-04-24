Use port 4567

All info is sent as Strings using InputStream/OutputStream

## Example game: ##
  1. Client connects: sends name
  1. Server responds to both clients with either: 1 or 2 (this value is the team number assigned to the client), then your opponent's name ... for example "1,Joe"
  1. Client waits for turn
  1. Server says to one client: 0 (this should happen at the beginning of each turn to tell client if there is a winner and lets the client know their turn has started. If there is a winner, the server will return a -1 for team 1 win, or -2 for team 2 win)
  1. Client should respond: int x, int y ... for example: "35" (this is the location of the piece that the client is selecting)
  1. Server responds to the one client: either -3 for confirmation of a valid selection or -4 for erroneous input
  1. Client loops through steps 4-5 until there is a valid selection
  1. Client should respond: int x, int y ... for example: "36" (this is the location of the board that the piece will move to, or a new piece to move)
  1. Server responds either -4 (for erroneous input) which is sent to the one client, or a -3 (meaning that you have selected a different piece to move) or a formatted string which is sent to both clients and dictates which spaces have changed
  1. Client loops through steps 6-7 until there is a valid selection. The client will be at step 6 if the server returns -3)
  1. Then the client's turn is over. The opponent will make their move, and the client will wait for the server to send a formatted string for opponent's move. Then the client will start back at step 4
  1. Server should respond : -1 or -2 (game is over), then the connection should close

Notes:

_Format for formatted string:_ int team number, int x, int y. The first section is the position of the piece before the move, the second is the space that piece has been moved to... for example the server would send 034,144 if team 1 successfully moved from 3,4 to 4,4

Server Codes:
  * 0 : begin turn
  * -1 : team one has won
  * -2 : team two has won
  * -3 : confirmation of a valid move
  * -4 : invalid input
  * -5 : other player has disconnected