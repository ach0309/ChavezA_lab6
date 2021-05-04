package me.Chavez_Lab6.Lab6;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.Chavez_Lab6.Lab6.db.Stock;

public class UpdateStockActivity extends AppCompatActivity {

    private EditText nameEdit, priceEdit, allTimePriceEdit, capEdit;
    private Button updateButton;
    private PortfolioViewModel portfolioViewModel;
    private LiveData<List<Stock>> allStocks;
    private Observable<Stock> observable;
    private static String TAG = "_UpdateStockActivity_";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_stock);

        nameEdit = findViewById(R.id.update_stock_name_text);
        priceEdit = findViewById(R.id.update_stock_price_text);
        allTimePriceEdit = findViewById(R.id.update_stock_alltime_price_text);
        capEdit = findViewById(R.id.update_stock_market_cap_text);
        updateButton = findViewById(R.id.update_button);

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

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEdit.getText().toString();
                Double price = Double.parseDouble(priceEdit.getText().toString());
                Double allTime = Double.parseDouble(allTimePriceEdit.getText().toString());
                Double marketCap = Double.parseDouble(capEdit.getText().toString());

                Stock stock = new Stock(name, price, allTime, marketCap);
                observable = io.reactivex.Observable.just(stock);
                io.reactivex.Observer<Stock> observer = getStockObserver(stock);

                observable
                        .observeOn(Schedulers.io())
                        .subscribe(observer);

                if (isStockInDatabase(stock.name)) {
                    getStockObserver(stock);
                    new CustomAlert("Successfully Updated Stock").show(getSupportFragmentManager(), TAG);
                } else {
                    new CustomAlert("Stock Not Found in DB").show(getSupportFragmentManager(),TAG);
                }
            }
        });
    }

    private boolean isStockInDatabase(String name) {
        boolean inDB = false;
        for (Stock stock : allStocks.getValue()) {
            if (name.equals(stock.name)) {
                inDB = true;
                break;
            }
        }
        return inDB;
    }

    private io.reactivex.Observer<Stock> getStockObserver(Stock stock) { // OBSERVER
        return new io.reactivex.Observer<Stock>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe");
            }

            @Override
            public void onNext(@NonNull Stock stock) {
                portfolioViewModel.getPortfolioDatabase().stockDao().update(stock);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "Stock Updated");
            }
        };
    }
}