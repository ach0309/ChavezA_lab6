package me.Chavez_Lab6.Lab6;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.Chavez_Lab6.Lab6.db.DatabaseOperations;
import me.Chavez_Lab6.Lab6.db.Stock;

import android.util.Log;
import android.view.View;
import android.content.Intent;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "_MainActivity_";
    private Button insertStockButton;
    private Button updateStockButton;
    private Button searchAllStocksButton;
    private Button clearStocksButton;
    private PortfolioViewModel portfolioViewModel;
    private LiveData<List<Stock>> allStocks;

    private Observable<Stock> observable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        insertStockButton = findViewById(R.id.main_insert_stock_button);
        updateStockButton = findViewById(R.id.main_update_stock_button);
        searchAllStocksButton = findViewById(R.id.main_search_stock_button);
        clearStocksButton = findViewById(R.id.main_clear_button);

        portfolioViewModel = new ViewModelProvider(this).get(PortfolioViewModel.class);
        allStocks = portfolioViewModel.getAllStocks();

        portfolioViewModel.getAllStocks().observe(this,
                new Observer<List<Stock>>() {
                    @Override
                    public void onChanged(List<Stock> stocks) {
                        for (Stock stock : stocks) {
                            if (!allStocks.getValue().contains(stock)){
                                allStocks.getValue().add(stock);
                            }
                        }
                    }
                });

        insertStockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inn1 = getIntent();
                inn1 = new Intent(MainActivity.this, InsertNewStockActivity.class);
                startActivity(inn1);
            }
        });

        updateStockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inn1 = getIntent();
                inn1 = new Intent(MainActivity.this, UpdateStockActivity.class);
                startActivity(inn1);
            }
        });

        searchAllStocksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inn1 = getIntent();
                inn1 = new Intent(MainActivity.this, StockInfoActivity.class);
                startActivity(inn1);
            }
        });

        clearStocksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(Stock stock : allStocks.getValue()) {
                    stock.databaseOperations = DatabaseOperations.INSERT;
                    observable = io.reactivex.Observable.just(stock);
                    io.reactivex.Observer<Stock> observer = getStockObserver(stock);

                    observable
                            .observeOn(Schedulers.io())
                            .subscribe(observer);
                }
            }
        });

    }

    private io.reactivex.Observer<Stock> getStockObserver(Stock stock) { // OBSERVER
        return new io.reactivex.Observer<Stock>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe");
            }

            @Override
            public void onNext(@NonNull Stock stock) {
                portfolioViewModel.getPortfolioDatabase().stockDao().delete(stock);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "Item deleted.");
                new CustomAlert("All Stocks Deleted").show(getSupportFragmentManager(),TAG);            }
        };
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}