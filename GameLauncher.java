import javax.swing.*; 
public class GameLauncher { 
    public GameLauncher() { 
        String[] options = {"Host Game", "Join Game"}; 
        int choice = JOptionPane.showOptionDialog( 
            null, 
            "Do you want to HOST or JOIN a game?", 
            "Game Launcher", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.QUESTION_MESSAGE, 
            null, 
            options, 
            options[0] 
        ); 
        if (choice == 0) { 
            hostGame(); 
        } else if (choice == 1) { 
            joinGame(); 
        } 
    } 
    private void hostGame() { 
        try { 
            new Thread(() -> { 
                try { 
                    GameServer.main(new String[]{}); 
                } catch (Exception e) { 
                    e.printStackTrace(); 
                } 
            }).start(); 
            Thread.sleep(1000); 
            String serverIP = java.net.InetAddress.getLocalHost().getHostAddress(); 
            JOptionPane.showMessageDialog( 
                null, 
                "Server started!\n\nYour IP Address: " + serverIP + "\nPort: 12345\n\nShare this IP with your friend!", 
                "Server Information", 
                JOptionPane.INFORMATION_MESSAGE 
            ); 
            connectToServer("localhost", 1); 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } 
    } 
    private void joinGame() { 
        String ip = JOptionPane.showInputDialog( 
            null, 
            "Enter Server IP Address:", 
            "Join Game", 
            JOptionPane.QUESTION_MESSAGE 
        ); 
        if (ip != null && !ip.trim().isEmpty()) { 
            connectToServer(ip.trim(), 2); 
        } 
    } 
    private void connectToServer(String ip, int expectedPlayerId) { 
        try { 
            java.net.Socket socket = new java.net.Socket(ip, 12345); 
            java.io.BufferedReader in = new java.io.BufferedReader( 
                new java.io.InputStreamReader(socket.getInputStream()) 
            ); 
            java.io.PrintWriter out = new java.io.PrintWriter(socket.getOutputStream(), true); 
            System.out.println("Connected as Player " + expectedPlayerId); 
            GameFrame frame = new GameFrame(800, 600, expectedPlayerId, socket, in, out); 
            frame.setUpGUI(); 
            new Thread(() -> { 
                try { 
                    String msg; 
                    while ((msg = in.readLine()) != null) { 
                        frame.handleServerMessage(msg); 
                    } 
                } catch (Exception e) { 
                    e.printStackTrace(); 
                } 
            }).start(); 
        } catch (Exception e) { 
            JOptionPane.showMessageDialog(null, "Failed to connect to server: " + e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE); 
            e.printStackTrace(); 
        } 
    } 
}