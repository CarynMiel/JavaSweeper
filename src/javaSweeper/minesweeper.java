package javaSweeper;

import java.lang.Math;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class minesweeper extends JFrame implements MouseListener{	
	// key
	public static final char flag = 'F';
	public static final char blank = ' ';
	public static final char unknown = '?';
	public static final char bomb = 'X';
	public static final int cellSize = 35;
	
	// board attributes
	double density;
	int x, y, bombs, flags;
	char[][] board, key;
	boolean alive = true;
	boolean started = false;
	boolean gameEnd = false;
	boolean inLoop = false;
	
	
	// visual attributes
	Map<String, JButton> buttons = new HashMap<>();
	JPanel gamePanel, scorePanel, endPanel;
	JButton close = new JButton("close");
	JButton newGame; 
	JFrame end;
	JLabel totalBombs, totalFlags, bombsLeft;
	
	// Constructor for the mine sweeper game
	public minesweeper (int x, int y, double density) {
		this.x = x;
		this.y = y;
		this.density = density;
		
		// creates the players board with unknowns
		board = new char[y][x];
		for (int i=0; i<y; i++) {
			Arrays.fill(board[i], unknown);
		}
		
		// creates the key board with no bombs
		key = new char[y][x];
		for (int i=0; i<y; i++) {
			Arrays.fill(key[i], blank);
		} 
		
		// places bombs in the key board
		// this.generate();
		// we don't generate the board yet so that player can't lose on the first turn
	
	
	}	
	
	// Creates the gui of the game
	public void make () {
		this.setTitle("JavaSweeper");
		this.setBackground(Color.decode("#144709"));
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // lets you exit the program by closing the window
				
		scorePanel = new JPanel(new GridLayout(1, 4));
		scorePanel.setBackground(palet.primaryColor);
		
		totalBombs = new JLabel("Total Bombs: " + String.valueOf(bombs));
		totalFlags = new JLabel("Total Flags: " + String.valueOf(flags));
		bombsLeft = new JLabel("Bombs Left: " + String.valueOf(bombs - flags));	
		
		totalBombs.setForeground(palet.secondaryColor);
		totalFlags.setForeground(palet.secondaryColor);
		bombsLeft.setForeground(palet.secondaryColor);
		
		totalBombs.setFont(new Font("Comfortaa", Font.PLAIN, 20));
		totalFlags.setFont(new Font("Comfortaa", Font.PLAIN, 20));
		bombsLeft.setFont(new Font("Comfortaa", Font.PLAIN, 20));
		
		totalBombs.setHorizontalAlignment(JLabel.CENTER);
		totalFlags.setHorizontalAlignment(JLabel.CENTER);
		bombsLeft.setHorizontalAlignment(JLabel.CENTER);
		
		newGame = new JButton("New Game");
		newGame.setForeground(palet.secondaryColor);
		newGame.setBackground(palet.primaryColor);
		newGame.setBorder(BorderFactory.createRaisedBevelBorder());
		newGame.setFont(new Font("Comfortaa", Font.PLAIN, 20));
		newGame.addMouseListener(this);

		
		scorePanel.add(totalBombs);
		scorePanel.add(totalFlags);
		scorePanel.add(bombsLeft);
		scorePanel.add(newGame);
		
		// setting up a scrolling pane in case the board is too big
		// setting up a panel with a gridlayout
		gamePanel = new JPanel(new GridLayout(this.y, this.x, 0, 0));
		gamePanel.setBackground(palet.primaryColor);				
		JScrollPane scroll = new JScrollPane(gamePanel);

		gamePanel.setPreferredSize(new Dimension(cellSize*this.x + 50, cellSize*this.y + 50));

		scorePanel.setBorder(BorderFactory.createLineBorder(palet.primaryColor, 25));
		
		gamePanel.setBorder(BorderFactory.createLineBorder(palet.primaryColor, 25));
		scroll.setBorder(BorderFactory.createLineBorder(palet.primaryColor, 15));
		scroll.setBackground(palet.secondaryColor);
		
		this.add(scorePanel, BorderLayout.NORTH);
		this.add(scroll, BorderLayout.CENTER);
		
		this.setSize(1080, 720);
		
		this.setResizable(true);
		
		// makes buttons for every tile of the board
		for (int i=0; i<y; i++) {
			for (int j=0; j<x; j++) {
				JButton button = new JButton();
				button.setText(String.valueOf(board[i][j]));
				button.addMouseListener(this);
				button.setFont(new Font("Comfortaa", Font.BOLD, 24));
				button.setBorder(BorderFactory.createRaisedBevelBorder());
				button.setSize(50, 50);
				gamePanel.add(button);			
				// creates a button in the dictionary with key of the index location
				buttons.put(String.valueOf(i)+", "+String.valueOf(j), button);
			}
		}
		
		// displays the board correctly
		this.update();
		this.setVisible(true);
		
	}
	
	// updates the visuals of the game
	public void update () {
		totalBombs.setText("Total Bombs: " + String.valueOf(bombs));
		totalFlags.setText("Total Flags: " + String.valueOf(flags));
		bombsLeft.setText("Bombs Left: " + String.valueOf(bombs - flags));
		
		if (this.isWin() || !alive && !gameEnd) {
			endGame();
		} else {
			// iterates through all the cells of the board
			for (int i=0; i<y; i++) {
				for (int j=0; j<x; j++) {
					// makes the coords of the cell into a string for the key of the button
					String coord = String.valueOf(i) + ", " + String.valueOf(j);
					JButton button = buttons.get(coord);
					
					// checks if the game is alive to determine what board to display
					if (!gameEnd) {
						button.setText(String.valueOf(board[i][j]));
					} else {
						button.setText(String.valueOf(key[i][j]));
					}
					
					// colors the buttons depending on the state of the tile
					if (button.getText().equals(String.valueOf(unknown))) {
						button.setBackground(palet.unknownTileColor);
						button.setForeground(palet.unknownTileColor);
					} else if (button.getText().equals(String.valueOf(bomb))) {
						button.setBackground(palet.bombTileColor);
						button.setForeground(palet.flagTileColor);
					} else if (button.getText().equals(String.valueOf(flag))) {
						button.setBackground(palet.flagTileColor);
						button.setForeground(palet.bombTileColor);
					} else if (button.getText().equals(String.valueOf(blank))) {
						button.setBackground(palet.blankTileColor);
					} else {
						if (this.flagsMatch(j, i)) {
							button.setBackground(palet.flaggedTileColor);
						} else {
							button.setBackground(palet.numberedTileColor);

						}
						if (button.getText().equals("1")) {
							button.setForeground(palet.colorOne);
						} else if (button.getText().equals("2")) {
							button.setForeground(palet.colorTwo);
						} else if (button.getText().equals("3")) {
							button.setForeground(palet.colorThree);
						} else if (button.getText().equals("4")) {
							button.setForeground(palet.colorFour);
						} else if (button.getText().equals("5")) {
							button.setForeground(palet.colorFive);
						} else if (button.getText().equals("6")) {
							button.setForeground(palet.colorSix);
						} else if (button.getText().equals("7")) {
							button.setForeground(palet.colorSeven);
						} else if (button.getText().equals("8")) {
							button.setForeground(palet.colorEight);
						}
					}
				}
			}
		}
	}

	// does something when the mouse is pressed
	@ Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == close || e.getSource() == newGame) {
			menu another = new menu();
			another.setup();
			this.dispose();
			if (e.getSource() == close) {
				end.dispose();
			}
		} 
		
		if (!gameEnd) {
			// iterates through all the cells to find what button was pressed
			for (int i=0; i<y; i++) {
				for (int j=0; j<x; j++) {
					String coord = String.valueOf(i) + ", " + String.valueOf(j);
					// if the button is left clicked it attempts to open the tile
					if (e.getSource() == buttons.get(coord) && e.getButton() == MouseEvent.BUTTON1) {
						// System.out.println("Button " + coord + " was left clicked");
						this.open(j, i);
					// if the button is right clicked it places a flag
					} else if (e.getSource() == buttons.get(coord) && e.getButton() == MouseEvent.BUTTON3) {
						// System.out.println("Button " + coord + " was right clicked");
						this.flag(j, i);
					}
				}
			}
			this.update();
		}
	}
	
	public void noGuessStart() {
		boolean containsBlank = false;
		// a list of indecies in the format y, x, y, x, ...
		// contians the indecies of the blank tiles
		ArrayList<int[]> blanks = new ArrayList<>();
		this.make();
		this.generate();
		
		// checking if there is a blank in the key and keeps track of how many there are
		for (int i=0; i<this.y; i++) {
			for (int j=0; j<this.x; j++) {
				if (key[i][j] == blank) {
					containsBlank = true;
					int[] index = new int[] {i, j};
					blanks.add(index);
				}
			}
		}
		
		// if there is blank tiles
		if (containsBlank) {
			int randBlank = (int) (Math.random() * (blanks.size()));
			int randY = blanks.get(randBlank)[0];
			int randX = blanks.get(randBlank)[1];
			open(randX, randY);
		// no blank tiles
		} else {
			while (true) {
				int randY = (int) (Math.random() * (this.y));
				int randX = (int) (Math.random() * (this.x));
				if (key[randY][randX] != bomb) {
					open(randX, randY);
					break;
				}
			}
		}
		
		this.update();
		
	}
	
	// places bombs and number hints in the key board
	public void generate () {
		// first it places the bombs
		
		// makes it so that in every cell there is a 
		// density percent chance that that cell will 
		// have a bomb in it
		if (density < 1) {
			for (int i=0; i<this.y; i++) {
				for (int j=0; j<this.x; j++) {
					if (Math.random() <= density) {
						key[i][j] = bomb;
						bombs++;
					} 
				} 
			}
		} else {
			// randomly picks a cell and adds a bomb there
			// continues until number of bombs is equal to 
			// desired density
			while (bombs < density) {
				int randX = (int) ((this.x)*Math.random());
				int randY = (int) ((this.y)*Math.random());
				// also makes sure the starting point is not a bomb
				if (key[randY][randX] != bomb) {
					key[randY][randX] = bomb;
					bombs++;
				} 
			} 
		}
		
		// going through each cell to determine what number it should be 
		for (int i=0; i<this.y; i++) {
			for (int j=0; j<this.x; j++) {
				// skipping any cells that are bombs themselves
				if (key[i][j] != bomb) {
					// creates a list of indecies for the surrounding cells
					int[] surrounding = getSurroundings(j, i);
					// resets the count for each cell
					int count = 0;
					// iterates through surrounding cells
					for (int k=0; k<surrounding.length; k+=2) {
						int cellY = surrounding[k];
						int cellX = surrounding[k+1];
						// if the position in the key is a bomb
						if (key[cellY][cellX] == bomb) {
							count ++;
						}
						
						// if there is a bomb near the cell
						if (count > 0) {
							// converts the int to a string then gets the char at index 0
							key[i][j] = String.valueOf(count).charAt(0);
						}
					}				
				}
			}
		}
		// notes that the game was started and prints the key to console
		// System.out.println(this.visualize(this.key));
		started = true;
	}
	
	
	
	// places bombs in the key board avoiding the location x, y	
	public void generate (int x, int y) {
		// first it places the bombs
		
		// makes it so that in every cell there is a 
		// density percent chance that that cell will 
		// have a bomb in it
		if (density < 1) {
			for (int i=0; i<this.y; i++) {
				for (int j=0; j<this.x; j++) {
					// also makes sure the starting point is not a bomb
					if (i == y && j == x) {
						
					} else if (Math.random() <= density) {
						key[i][j] = bomb;
						bombs++;
					} 
				} 
			}
		} else {
			// randomly picks a cell and adds a bomb there
			// continues until number of bombs is equal to 
			// desired density
			while (bombs < density) {
				int randX = (int) ((this.x)*Math.random());
				int randY = (int) ((this.y)*Math.random());
				// also makes sure the starting point is not a bomb
				if (key[randY][randX] != bomb) {
					key[randY][randX] = bomb;
					bombs++;
					if (randX == x && randY == y) {
						bombs--;
						key[randY][randX] = blank;
					}
				} 
			} 
		}
		
		// going through each cell to determine what number it should be 
		for (int i=0; i<this.y; i++) {
			for (int j=0; j<this.x; j++) {
				// skipping any cells that are bombs themselves
				if (key[i][j] != bomb) {
					// creates a list of indecies for the surrounding cells
					int[] surrounding = getSurroundings(j, i);
					// resets the count for each cell
					int count = 0;
					// iterates through surrounding cells
					for (int k=0; k<surrounding.length; k+=2) {
						int cellY = surrounding[k];
						int cellX = surrounding[k+1];
						// if the position in the key is a bomb
						if (key[cellY][cellX] == bomb) {
							count ++;
						}
						
						// if there is a bomb near the cell
						if (count > 0) {
							// converts the int to a string then gets the char at index 0
							key[i][j] = String.valueOf(count).charAt(0);
						}
					}				
				}
			}
		}
		// notes that the game was started and prints the key to console
		// System.out.println(this.visualize(this.key));
		started = true;
	} 	
	
	// returns a list of indecies [y, x, y, x,...] of the surrounding tiles
	public int[] getSurroundings (int x, int y) {
		// creates and returns a list with all the surrounding indecies
		int[] cells;
		
		// length of list varies depending on the location of the tile
		// centered at the top left corner
		if (x == 0 && y == 0) {
			cells = new int[] {y, x+1, y+1, x, y+1, x+1};
		// centered at the top right
		} else if (x+1 == this.x && y == 0) {
			cells = new int[] {y, x-1, y+1, x-1, y+1, x};
		// centered at the bottom left corner
		} else if (x == 0 && y+1 == this.y) {
			cells = new int[] {y-1, x, y-1, x+1, y, x+1};
		} else if (x+1 == this.x && y+1 == this.y) {
			// centered at the bottom right corner
			cells = new int[] {y-1, x-1, y-1, x, y, x-1};
		} else if (x == 0) {
			// centered on the left edge
			cells = new int[] {y-1, x, y-1, x+1, y, x+1, y+1, x, y+1, x+1};
		} else if (x+1 == this.x) {
			// centered on the right edge
			cells = new int[] {y-1, x-1, y-1, x, y, x-1, y+1, x-1, y+1, x};
		} else if (y == 0) {
			// centered on the top edge
			cells = new int[] {y, x-1, y, x+1, y+1, x-1, y+1, x, y+1, x+1};
		} else if (y+1 == this.y) {
			// centered on the bottom edge
			cells = new int[] {y-1, x-1, y-1, x, y-1, x+1, y, x-1, y, x+1};
		} else {
			// centered away from edges
			cells = new int[] {y-1, x-1, y-1, x, y-1, x+1, y, x-1, y, x+1, y+1, x-1, y+1, x, y+1, x+1};
		}
		
		return cells;
	}
	
	// tries to open surrounding tiles
	public void openAround(int x, int y) {
		inLoop  = true;
		int[] around = getSurroundings(x, y);
		
		for (int i=0; i<around.length; i+=2) {
			int yIn = around[i];
			int xIn = around[i+1];
			open(xIn, yIn);
		}
		inLoop = false;
	}
	
	// returns true if the number of bombs matches the number on the tile
	public boolean flagsMatch(int x, int y) {
		int[] around = getSurroundings(x, y);
		int count = 0, num = 0;
		try {
			num = Integer.parseInt(String.valueOf(board[y][x]));
		} catch (Exception e) {
			return false;
		}
		
		// System.out.println("Num = " + String.valueOf(num));
		
		for (int i=0; i<around.length; i+=2) {
			int yIn = around[i];
			int xIn = around[i+1];
			if (board[yIn][xIn] == flag) {
				count++;
				// System.out.println("Flag found");
			}
		}
		
		// System.out.println("Count = " + String.valueOf(count));
		
		return count == num;
	}
		
	// player tries to open the tile at board[y][x], or kills them if its a bomb
	public void open (int x, int y) {
		if (!started) {
			generate(x, y);
		}
		
		// the do no things
		// board tile is blank
		if (board[y][x] == blank) {
			// System.out.println("A revealed blank tile was pressed");
		// board tile is flag
		} else if (board[y][x] == flag) {
			// System.out.println("A flag tile was pressed");
		// board tile is number (flagsMatch: false)
		} else if (board[y][x] != unknown && !this.flagsMatch(x,y)) {
			// System.out.println("A number tile with not enough flags was pressed");
		
		// the do some things	
		// board tile is unknown and key tile is bomb
		} else if (board[y][x] == unknown && key[y][x] == bomb) {
			board[y][x] = key[y][x];
			alive = false;
			endGame();
			
		// board tile is unknown and the key tile is a blank
		} else if (board[y][x] == unknown && key[y][x] == blank) {
			board[y][x] = key[y][x];
			openAround(x, y);
		
		// board tile is unknown and not a bomb or blank so its a number
		} else if (board[y][x] == unknown){
			board[y][x] = key[y][x];
		
		// board tile is a number (flagsMatch: true)
		} else if (this.flagsMatch(x, y) && !inLoop) {
			board[y][x] = key[y][x];
			openAround(x, y);
		} 
		
		
		// this.update();
	}
	
	public void flag (int x, int y) {
		if (board[y][x] == unknown) {
			board[y][x] = flag;
			flags++;
		} else if (board[y][x] == flag) {
			board[y][x] = unknown;
			flags--;
		}
		this.update();
	}
	
	public boolean isWin() {
		for (int i=0; i<y; i++) {
			for (int j=0; j<x; j++) {
				if(key [i][j] != bomb && board[i][j] == unknown) {
					return false;
				} 
			}
		}
		return true;
	}
	
	public void endGame() {
		gameEnd = true;
		this.forcedUpdate();
		end = new JFrame("Game Over!");
		endPanel = new JPanel(new GridLayout(2, 1));
		
		end.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		end.setSize(300, 200);
				
		if (this.isWin()) {
			endPanel.add(new JLabel("You Win!"));
		} else {
			endPanel.add(new JLabel("You Lose!"));
		}
		close.addMouseListener(this);
		
		endPanel.setBorder(BorderFactory.createLineBorder(Color.white, 15));
		
		endPanel.add(close);
		
		end.add(endPanel);
		
		end.setVisible(true);
	}
	
	public void forcedUpdate() {
		// iterates through all the cells of the board
		for (int i=0; i<y; i++) {
			for (int j=0; j<x; j++) {
				// makes the coords of the cell into a string for the key of the button
				String coord = String.valueOf(i) + ", " + String.valueOf(j);
				JButton button = buttons.get(coord);
				
				// checks if the game is alive to determine what board to display
				if (isWin()) {
					button.setText(String.valueOf(board[i][j]));
				} else {
					button.setText(String.valueOf(key[i][j]));
				}
							
				// colors the buttons depending on the state of the tile
				if (button.getText().equals(String.valueOf(unknown))) {
					button.setBackground(palet.unknownTileColor);
					button.setForeground(palet.unknownTileColor);
				} else if (button.getText().equals(String.valueOf(bomb))) {
					button.setBackground(palet.bombTileColor);
					button.setForeground(palet.flagTileColor);
				} else if (button.getText().equals(String.valueOf(flag))) {
					button.setBackground(palet.flagTileColor);
					button.setForeground(palet.bombTileColor);
				} else if (button.getText().equals(String.valueOf(blank))) {
					button.setBackground(palet.blankTileColor);
				} else {
					if (flagsMatch(j, i)) {
						button.setBackground(palet.flaggedTileColor);
					} else {
						button.setBackground(palet.numberedTileColor);
					}
					
					if (button.getText().equals("1")) {
						button.setForeground(palet.colorOne);
					} else if (button.getText().equals("2")) {
						button.setForeground(palet.colorTwo);
					} else if (button.getText().equals("3")) {
						button.setForeground(palet.colorThree);
					} else if (button.getText().equals("4")) {
						button.setForeground(palet.colorFour);
					} else if (button.getText().equals("5")) {
						button.setForeground(palet.colorFive);
					} else if (button.getText().equals("6")) {
						button.setForeground(palet.colorSix);
					} else if (button.getText().equals("7")) {
						button.setForeground(palet.colorSeven);
					} else if (button.getText().equals("8")) {
						button.setForeground(palet.colorEight);
					}
				}
			}
		}
	}
	
	// visualize a board 
	// map can be either the key board or the player's board
	public String visualize (char[][] map) {
		/* 
		 * Printing format 
		 * +---+---+---+---+---+---+
		 * |   | 0 | 1 | 2 | 3 | 4 |
		 * +---+---+---+---+---+---+
		 * | 0 | ? |   |   |   |   |
		 * +---+---+---+---+---+---+
		 * | 1 |   |   |   |   |   |
		 * +---+---+---+---+---+---+
		 * | 2 |   |   | F |   |   |
		 * +---+---+---+---+---+---+
		 * | 3 |   | X |   |   |   |
		 * +---+---+---+---+---+---+
		 * | 4 |   |   |   |   |   |
		 * +---+---+---+---+---+---+
		 */
		
		String visual = "+";
		// creates +---+---+---+ lines
		for (int i=0; i<this.x+1; i++) {
			visual += "---+";
		} visual += "\n|   | ";
		
		// creates | 0 | 1 | 2 |... lines
		for (int i=0; i<this.x; i++) {
			visual += i%10;
			visual += " | ";
		} // creates +---+---+---+ lines
		for (int i=0; i<this.y; i++) {
			visual += "\n+";
			for (int j=0; j<this.x+1; j++) {
				visual += "---+";
			} // creates | i | X |   | ? | lines 
			visual += "\n| ";
			visual += i%10;
			visual += " | ";
			for (int j=0; j<this.x; j++) {
				visual += map[i][j];
				visual += " | ";
			}
		}// creates +---+---+---+ lines
		visual += "\n+";
		for (int i=0; i<this.x+1; i++) {
			visual += "---+";
		}
		
		return visual + "\n\n\n";
	}
	// unused things that eclipse needs or it throws an error
	// required for jframe
	private static final long serialVersionUID = 7030105158810910316L;

	// required for mouse listener
	@ Override
	// checks the curser left an element
	public void mouseExited(MouseEvent e) {
		
	}
	@ Override
	// checks the curser hovered over an element
	public void mouseEntered(MouseEvent e) {
		
	}
	@ Override
	// checks that the button was clicked up
	public void mouseReleased(MouseEvent e) {
		
	} 
	@ Override
	// checks that the button was pressed down
	public void mousePressed(MouseEvent e) {
		
	}
} // end of mine sweeper class