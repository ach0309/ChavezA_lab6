package me.Chavez_Lab6.Lab6.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Stock {

    public Stock(@NonNull String name, double price, double allTimeHigh, double marketCap) {
        this.name = name;
        this.price = price;
        this.allTimeHigh = allTimeHigh;
        this.marketCap = marketCap;
    }

    @PrimaryKey @NonNull
    public String name;

    @ColumnInfo
    public double price;

    @ColumnInfo
    public double allTimeHigh;

    @ColumnInfo
    public double marketCap;

    @Ignore
    public DatabaseOperations databaseOperations;
}
