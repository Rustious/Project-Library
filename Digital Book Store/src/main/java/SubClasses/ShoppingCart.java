package SubClasses;

public class ShoppingCart {
    private String CartbookTitle;
    private String CartbookAuthor;
    private int quantity;
    private double price;

    public ShoppingCart(String CartbookTitle, String CartbookAuthor, int quantity, double price) {
        this.CartbookTitle = CartbookTitle;
        this.CartbookAuthor = CartbookAuthor;
        this.quantity = quantity;
        this.price = price;
    }

    public String getBookTitle() {
        return CartbookTitle;
    }

    public String getBookAuthor() {
        return CartbookAuthor;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if(quantity < 0) {
            System.out.println("Invalid Quantity, cannot be negative");
        }
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price < 0) {
            System.out.println("Price cannot be negative");
        }
        this.price = price;
    }
}
