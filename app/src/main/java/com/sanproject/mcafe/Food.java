package com.sanproject.mcafe;

/**
 * Created by sanjay on 12/03/2018.
 */

public class Food {
    private String Food_name;
    private int price;
    private String Quantity;
    private int Total_orders;
    private String Food_Image;
    private String Description;
    private String Type;
    private int discount;
    private String Time;
    private int Total_NoOfTime_Rated;
    private int Sum_of_Ratings;
    private String gm500,gm200,gm1000,gm500_d,gm1000_d,gm200_d;
    private String FoodId;
    private String shape,flavour;
    private String Canteen;
    private String CanteenImage;

    private int Favorite;
    public int getFavorite() {
        return Favorite;
    }

    public int getDiscount() {
        return discount;
    }

    public Food(String food_name, int price, String quantity, int total_orders, String food_Image, String description, String type, int discount, int total_NoOfTime_Rated, int sum_of_Ratings) {
        Food_name = food_name;
        this.price = price;
        Quantity = quantity;
        Total_orders = total_orders;
        Food_Image = food_Image;
        Description = description;
        Type = type;
        this.discount = discount;
        Total_NoOfTime_Rated = total_NoOfTime_Rated;
        Sum_of_Ratings = sum_of_Ratings;
    }

     public Food(){

    }

    public String getCanteen() {
        return Canteen;
    }

    public String getCanteenImage() {
        return CanteenImage;
    }

    public String getFoodId() {
        return FoodId;
    }

    public String getGm500() {
        return gm500;
    }

    public String getShape() {
        return shape;
    }

    public String getFlavour() {
        return flavour;
    }

    public String getGm200() {
        return gm200;
    }

    public String getGm1000() {
        return gm1000;
    }

    public int getSum_of_Ratings() {
        return Sum_of_Ratings;
    }

    public int getTotal_NoOfTime_Rated() {
        return Total_NoOfTime_Rated;
    }

    public String getFood_name() {
        return Food_name;
    }
    public String getQuantity() {
        return Quantity;
    }

    public int getTotal_orders() {return Total_orders;}

    public int getPrice() {
        return price;
    }

    public String getFood_Image() {
        return Food_Image;
    }

    public String getDescription() {
        return Description;
    }

    public String getType() {
        return Type;
    }



    public String getGm500_d() {
        return gm500_d;
    }

    public String getGm1000_d() {
        return gm1000_d;
    }

    public String getGm200_d() {
        return gm200_d;
    }
}


