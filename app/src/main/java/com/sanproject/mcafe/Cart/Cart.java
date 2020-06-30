package com.sanproject.mcafe.Cart;

public class Cart {
    public String Food_Image;
    public String Food_name;
    public String Type;
    public int price;
    public String Quantity;

    public Cart(String food_Image, String food_name, String type, int price, String quantity) {
        Food_Image = food_Image;
        Food_name = food_name;
        Type = type;
        this.price = price;
        Quantity = quantity;
    }
}
