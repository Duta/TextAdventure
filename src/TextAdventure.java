import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
public class TextAdventure {
	private static final String
		WELCOME_MESSAGE = "Welcome to ZombiesAdventure! Try not to die. ~Daejo",
		PROMPT_MESSAGE = "What do you want to do?",
		INPUT_PREFIX = "> ",
		EXIT_MESSAGE = "Seeya. Thanks for playing ZombiesAdventure!";
	private static final String[]
		MOVEMENT_WORDS = {
			"go",
			"move",
			"walk",
			"run",
			"amble",
			"head",
			"spring"
		},
		NORTH_WORDS = {
			"north",
			"n",
			"up"
		},
		EAST_WORDS = {
			"east",
			"e",
			"right"
		},
		SOUTH_WORDS = {
			"south",
			"s",
			"down"
		},
		WEST_WORDS = {
			"west",
			"w",
			"left"
		},
		PRINT_WORDS = {
			"print",
			"p",
			"show",
			"check"
		},
		INVENTORY_WORDS = {
			"inv",
			"inventory",
			"bag"
		},
		STOP_WORDS = {
			"stop",
			"exit"
		};
	private Room currentRoom;
	private BufferedReader b;
	private boolean running;
	private Inventory inventory;

	public static void main(String[] args) {
		// Set up some example rooms
		Room kitchen = new KitchenRoom();
		Room bedroom = new BedroomRoom();
		Room bathroom = new BathroomRoom();
		bedroom.setNorth(kitchen);
		kitchen.setSouth(bedroom);
		bathroom.setEast(bedroom);
		bedroom.setWest(bathroom);

		// Start game
		new TextAdventure(kitchen);
	}

	public TextAdventure(Room startRoom) {
		currentRoom = startRoom;
		running = true;
		inventory = new Inventory();

		b = new BufferedReader(new InputStreamReader(System.in));

		// Print a welcome message
		System.out.println(WELCOME_MESSAGE);

		// Start the main game loop
		while(running) {
			// Print the current room's description
			currentRoom.printDescription();
			// Print all the current items' descriptions
			currentRoom.printItems();
			// Print possible directions
			currentRoom.printDirections();

			// Prompt the user for input
			System.out.println(PROMPT_MESSAGE);
			System.out.print(INPUT_PREFIX);
			String userInput = "";
			try {
				userInput = b.readLine();
			} catch(IOException e) {
				// If reading from the input
				// stream fails, exit
				System.err.println("Error while reading user input.");
				System.err.println("Exiting...");
				System.exit(1);
			}

			// Parse the user's input
			parseInput(userInput);
		}

		// Print a goodbye message
		System.out.println(EXIT_MESSAGE);
	}

	private void parseInput(String input) {
		// Turn all whitespace into single spaces
		input = input.replaceAll("\\s+", " ");

		// Remove any leading/trailing whitespace
		input = input.trim();

		// Split the input into an array of words
		String[] words = input.split(" ");

		// Time to work out what we're meant to be doing

		// If there aren't any words, do nothing
		if(words.length == 0) {
			return;
		}

		// Check for move north
		if(matchesAny(words[0], NORTH_WORDS)
		|| (words.length > 1
		&&  matchesAny(words[0], MOVEMENT_WORDS)
		&&  matchesAny(words[1], NORTH_WORDS))) {
			if(currentRoom.canGoNorth()) {
				// If we can go north, go north
				currentRoom = currentRoom.getNorth();
				System.out.println("You go north.");
			} else {
				// If we can't, tell the player
				System.out.println("Can't move north...");
			}
			return;
		}

		// Check for move east
		if(matchesAny(words[0], EAST_WORDS)
		|| (words.length > 1
		&&  matchesAny(words[0], MOVEMENT_WORDS)
		&&  matchesAny(words[1], EAST_WORDS))) {
			if(currentRoom.canGoEast()) {
				// If we can go east, go east
				currentRoom = currentRoom.getEast();
				System.out.println("You go east.");
			} else {
				// If we can't, tell the player
				System.out.println("Can't move east...");
			}
			return;
		}

		// Check for move south
		if(matchesAny(words[0], SOUTH_WORDS)
		|| (words.length > 1
		&&  matchesAny(words[0], MOVEMENT_WORDS)
		&&  matchesAny(words[1], SOUTH_WORDS))) {
			if(currentRoom.canGoSouth()) {
				// If we can go south, go south
				currentRoom = currentRoom.getSouth();
				System.out.println("You go south.");
			} else {
				// If we can't, tell the player
				System.out.println("Can't move south...");
			}
			return;
		}

		// Check for move west
		if(matchesAny(words[0], WEST_WORDS)
		|| (words.length > 1
		&&  matchesAny(words[0], MOVEMENT_WORDS)
		&&  matchesAny(words[1], WEST_WORDS))) {
			if(currentRoom.canGoWest()) {
				// If we can go west, go west
				currentRoom = currentRoom.getWest();
				System.out.println("You go west.");
			} else {
				// If we can't, tell the player
				System.out.println("Can't move west...");
			}
			return;
		}

		// Check for pick up
		if(words.length > 2
		&& "pick up".equalsIgnoreCase(words[0] + " " + words[1])) {
			String itemName = words[2];
			for(int i = 3; i < words.length; i++) {
				itemName += " " + words[i];
			}
			if(currentRoom.containsItem(itemName)) {
				// If the item is in the
				// room, pick it up
				Item item = currentRoom.removeItem(itemName);
				// and add it to the inventory
				inventory.add(item);

				System.out.println("You pick up the " + itemName + ".");
			} else {
				// Tell the player that we
				// can't find that item
				System.out.println("That item isn't available");
			}
			return;
		}

		// Check for print inventory
		if(matchesAny(words[0], INVENTORY_WORDS)
		|| (words.length > 1
		&& matchesAny(words[0], PRINT_WORDS)
		&& matchesAny(words[1], INVENTORY_WORDS))) {
			System.out.println("Inventory:");
			if(inventory.isEmpty()) {
				System.out.println(" - Nothing!");
			} else {
				inventory.printItems(" - %s\n");
			}
			return;
		}

		// Check for a stop command
		if(matchesAny(words[0], STOP_WORDS)) {
			// Stop the game
			running = false;
			return;
		}

		// If we've got to this point,
		// we haven't managed to work
		// out what the user wants to do
		System.out.println("I didn't understand that command...");
	}

	private static boolean matchesAny(String word, String[] possibilities) {
		// For each possibility
		for(int i = 0; i < possibilities.length; i++) {
			String possibility = possibilities[i];
			// If it matches, then return true
			if(word.equalsIgnoreCase(possibility)) {
				return true;
			}
		}

		// If it didn't match any, return false
		return false;
	}
}