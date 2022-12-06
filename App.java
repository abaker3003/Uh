import java.util.*;
import java.io.FileInputStream;
import java.io.IOException;

public class Main {
    //variable declared for the MarketSpace class
    static MarketSpace marketspace = MarketSpace.getInstance();

    public static void main(String[] args) throws IOException{
        String filename = "products.txt";
        Scanner userInput = new Scanner(System.in);
        //calls countLines() method
        int lineAmt = countLines(filename);
        marketspace.loadProducts(filename, lineAmt);
        boolean cont = true;
        System.out.print("Hi, ");
        int opt = 0;
        int cart = 0;
        do {
            beginningMenu();
            do {
                opt = validInput(userInput);
                if (opt <= 0 || opt >= 6) {
                    System.out.println("That's not an option.");
                    beginningMenu();
                    cont = false;
                }
            } while (!cont);
            if (opt == 5){
                System.out.println("Bye!");
                return;
            }
            switch (opt) {
                // Buy a computer
                case 1 -> {
                    marketspace.createCart(cart);
                    int compOpt = -1;
                    do {
                        System.out.println("Current build: " + marketspace.currentBuild(cart));
                        System.out.println("Pick a product to add:");
                        marketspace.listOfProducts();
                        boolean valid = true;
                        do {
                            compOpt = validInput(userInput);
                            if (compOpt > 9 || compOpt < 1) {
                                System.out.println("Not a product option. Try again.");
                                valid = false;
                            } else {
                                valid = true;
                            }
                        } while (!valid);
                        if (compOpt == 9) {
                            break;
                        }
                        marketspace.addToCart(cart, compOpt - 1);
                    } while (compOpt != 9);
                    cart++;
                    break;
                }
                // See my shopping cart
                case 2 -> {
                    marketspace.viewCart();
                    break;
                }

                // Sort by Order ID (Descending Order)
                case 3 -> {
                    marketspace.sort(1);
                    break;
                }
                // Sort by Price (Descending Order)
                case 4 -> {
                    marketspace.sort(2);
                    break;
                }


            }
        } while (cont);

    }
    public static int countLines(String filename) throws IOException {
        //file variable
        FileInputStream newFile = new FileInputStream(filename);
        //scanner variable for file
        Scanner scnr = new Scanner(newFile);
        int numLines = 1;
        //skips the first line of file(header)
        scnr.nextLine();
        //loops when a next line is detected
        while (scnr.hasNextLine()) {
            //adds 1 to numLines when next line is detected
            numLines++;
            //continues to next line in file
            scnr.nextLine();
        }
        //returns the number of lines found in file
        return numLines;
    }
    public static void beginningMenu() {
        System.out.println("what would you like to do?");
        System.out.println("1: Buy a computer");
        System.out.println("2: See my shopping cart");
        System.out.println("3: Sort by order ID (Descending order)");
        System.out.println("4: Sort by order price (Descending order)");
        System.out.println("5: Quit");
    }
    public static int validInput(Scanner userInput) {
        int opt = -1;
        boolean cont = true;
        do {
            try {
                opt = userInput.nextInt();
                cont = true;
            } catch (Exception e) {
                System.out.println("Please input a number");
                cont = false;
                userInput.nextLine();
            }

        } while (!cont);
        return opt;
    }

}
class MarketSpace {

    private static MarketSpace instance = new MarketSpace();
    private List<Computer> cart = new ArrayList<>();
    private Map<String, Double> products = new LinkedHashMap<String, Double>();
    private List<Integer> orderIDs = new ArrayList<>();
    private SortStrategy sortStrategy;

    private MarketSpace() {}

    public static MarketSpace getInstance() {
        return instance;
    }
    public void loadProducts(String filename, int numOfProducts) throws IOException{
        //file variable
        FileInputStream newFile = new FileInputStream(filename);
        //scanner variable for file
        Scanner scnr = new Scanner(newFile);
        while (scnr.hasNextLine() && numOfProducts > 0) {
            //line in file
            String currLine = scnr.nextLine();
            //splits line by ", " and stores in token array
            String[] token = currLine.split(",");
            this.products.put(token[0], Double.parseDouble(token[1]));
            numOfProducts--;
        }

    }
    public void listOfProducts(){
        Set<String> productList = this.products.keySet();
        Iterator<String> p = productList.iterator();
        int num = 1;
        while (p.hasNext()) {
            String product = p.next();
            System.out.println(num + ". " + product + " $" + this.products.get(product));
            num++;
        }
        System.out.println(num + ". Done");
    }
    public void createCart(int currCart) {
        Computer defaultComp = new ComputerImpl();
        defaultComp.setOrderIDNum(generateOrderID());
        defaultComp.setOrderID(generateOrderID());
        cart.add(currCart, defaultComp);
    }
    public void addToCart(int currCart, int productOption) {
        String product = getProduct(productOption);
        double productPrice = getPrice(product);
        Computer newPart = cart.get(currCart);
        newPart.setDescription(product);
        newPart.setPrice(productPrice);
        cart.set(currCart, newPart);

    }
    public int generateOrderID(){
        Random rndm = new Random();
        boolean valid = true;
        int orderID = 0;
        do {
            orderID = rndm.nextInt(100) + 1;
            if (this.orderIDs.contains(orderID)) {
                valid = false;
            }
        } while (!valid);
        orderIDs.add(orderID);
        return orderID;
    }
    public String getProduct(int index) {
        Set<String> productList = this.products.keySet();
        Iterator<String> p = productList.iterator();
        String product = "";
        while (p.hasNext()) {
            product = p.next();
            if (index == 0) {
                break;
            }
            index--;
        }
        return product;
    }
    public double getPrice(String product) {
        return this.products.get(product);
    }
    public void viewCart(){
        if (cart.isEmpty()){
            System.out.println("No Items In Cart");
        }
        else {
            System.out.println(view_Cart());
        }
    }
    public String currentBuild(int cart){
        Computer currentOrder = this.cart.get(cart);
        String print = currentOrder.getDescription() + ", and the total price is $" + currentOrder.getPrice();
        return print;

    }
    public String view_Cart(){
        String cart = "[";
        int size = this.cart.size();
        for (int i = 0; i < size; i++) {
            Computer order = this.cart.get(i);
            cart += order.getOrderID() + ": ";
            cart += order.getDescription() + " ";
            cart += "$" + order.getPrice();
            if (i < size - 1) {
                cart += ",\n";
            }
        }
        return cart + "]";
    }
    public void sort(int option){
        if (this.cart.isEmpty()) {
            System.out.println("No Items In Cart");
        }
        else if (option == 1) {
            sortStrategy = new SortByOrderID();
            List<Computer> newCart = sortStrategy.sort(this.cart);
            Collections.copy(this.cart,newCart);
        }
        else {
            sortStrategy = new SortByPrice();
            Collections.copy(this.cart,sortStrategy.sort(cart));
        }

    }
}
interface SortStrategy {
    public List<Computer> sort(List<Computer> cart);
}
class SortByOrderID implements SortStrategy {

    @Override
    public List<Computer> sort(List<Computer> cart) {
        int size = cart.size();
        for (int i = 1; i < size; i++) {
            Computer build1 = cart.get(i);
            int j = i - 1;

            while (j >= 0  && Integer.compare(cart.get(j).getOrderIDNum(),build1.getOrderIDNum()) < 0) {
                cart.set(j + 1, cart.get(j));
                j--;
            }
            cart.set(j + 1, build1);
        }
        return cart;
    }

}

class SortByPrice implements SortStrategy {

    @Override
    public List<Computer> sort(List<Computer> cart) {
        int size = cart.size();
        List<Computer> newCart = new ArrayList<>(cart);
        Collections.copy(newCart, cart);
        for (int i = 1; i < size; i++) {
            Computer build1 = newCart.get(i);
            int j = i - 1;

            while (j >= 0 && newCart.get(j).getPrice().compareTo(build1.getPrice()) < 0) {
                newCart.set(j + 1, newCart.get(j));
                j -= 1;
            }
            newCart.set(j + 1, build1);
        }
        return newCart;
    }
}
interface Computer {
	
    // getters
    String getDescription();
    Double getPrice();
    String getOrderID();
    int getOrderIDNum();
	
	// setters
    void setDescription(String newProduct);
    void setPrice(double newPrice);
    void setOrderID(int orderID);
    void setOrderIDNum(int orderIDNum);


}
class ComputerImpl implements Computer {
	
    // attriutes with default values
    String description = "Default Computer";
    double price = 700.0;
    String orderID = "OrderID@";
    int orderIDNum = 0;
	
	// getters
    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public Double getPrice() {
        return this.price;
    }

    @Override
    public String getOrderID() {
        return this.orderID;
    }
    @Override
    public int getOrderIDNum() {
        return this.orderIDNum;
    }

    // setters
    @Override
    public void setDescription(String newProduct) {
        this.description += " + " + newProduct;
    }

    @Override
    public void setPrice(double newPrice) {
        this.price += newPrice;
    }

    @Override
    public void setOrderID(int orderID) {
        this.orderID += orderID;
    }
    @Override
    public void setOrderIDNum(int orderIDNum) {
        this.orderIDNum += orderIDNum;
    }
}
abstract class ComputerDecorator extends ComputerImpl {
    protected Computer decoratedComputer;

    public ComputerDecorator (Computer decoratedComputer) {
        this.decoratedComputer = decoratedComputer;
    }
	
	// getters
    @Override
    public String getDescription() {
        return decoratedComputer.getDescription();
    }

    @Override
    public Double getPrice() {
        return decoratedComputer.getPrice();
    }

    @Override
    public String getOrderID() {
        return decoratedComputer.getOrderID();
    }
    @Override
    public int getOrderIDNum() {
        return decoratedComputer.getOrderIDNum();
    }
}
class Component extends ComputerDecorator{

    public Component(Computer decoratedComputer) {
        super(decoratedComputer);
    }
	
    // setters
    @Override
    public void setDescription(String newProduct) {
        decoratedComputer.setDescription(newProduct);
    }

    @Override
    public void setPrice(double newPrice) {
        decoratedComputer.setPrice(newPrice);
    }

    @Override
    public void setOrderID(int orderID) {
        decoratedComputer.setOrderID(orderID);
    }
    @Override
    public void setOrderIDNum(int orderIDNum) {
        decoratedComputer.setOrderIDNum(orderIDNum);
    }
}