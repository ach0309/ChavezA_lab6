package me.Chavez_Lab6.Lab6;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;

import java.util.List;

import io.reactivex.Observable;
import me.Chavez_Lab6.Lab6.db.Stock;

public class StockInfoActivity extends AppCompatActivity {

    private EditText nameEdit;
    private Button searchButton;
    private TextView price, allTimePrice, cap;
    private PortfolioViewModel portfolioViewModel;
    private LiveData<List<Stock>> allStocks;
    private Observable<Stock> observable;
    private static String TAG = "_StockInfoActivity_";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_info);

        nameEdit = findViewById(R.id.info_stock_name_text_edit);
        searchButton = findViewById(R.id.info_search_button);
        price = findViewById(R.id.info_price_text);
        allTimePrice = findViewById(R.id.info_all_time_text);
        cap = findViewById(R.id.info_cap_text);

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

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEdit.getText().toString();
                for (Stock stock : allStocks.getValue()) {
                    if (name.equals(stock.name)) {
                        price.setText(String.valueOf(stock.price));
                        allTimePrice.setText(String.valueOf(stock.allTimeHigh));
                        cap.setText(String.valueOf(stock.marketCap));
                        return;
                    }
                }
                new CustomAlert("Stock not found in DB").show(getSupportFragmentManager(),TAG);
            }
        });
    }

}