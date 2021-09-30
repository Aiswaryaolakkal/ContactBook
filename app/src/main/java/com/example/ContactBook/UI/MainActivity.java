package com.example.ContactBook.UI;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.example.ContactBook.Data.Local.DatabaseClient;
import com.example.ContactBook.Data.Local.LocalDb;
import com.example.ContactBook.Model.Contactitem;
import com.example.ContactBook.Data.Remote.GetDataService;
import com.example.ContactBook.Data.Remote.RetrofitClientInstance;
import com.example.ContactBook.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class  MainActivity extends AppCompatActivity {
    RecyclerView itemRecyclerView;
    ContactAdapter contactAdapter;
    boolean isViewWithList ;
    private ImageView listviewIV;
    private TextView noitemTextView;

    private int offset = 0, limit = 20;
    private boolean isLoading = false;
    private boolean hasMoreItems = false;

    ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("  Contact Book  ");

        progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.plz_wait));
        isViewWithList = true;
        itemRecyclerView = (RecyclerView) findViewById(R.id.item_Rv);
        noitemTextView = (TextView)findViewById(R.id.no_item_tv) ;


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        itemRecyclerView.setLayoutManager(linearLayoutManager);
        contactAdapter = new ContactAdapter(getApplicationContext());

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(onitemswipe);
        itemTouchHelper.attachToRecyclerView(itemRecyclerView);
        itemRecyclerView.addOnScrollListener(mOnScrollListener);

        checkInternetConnection();



    }
    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (recyclerView.getAdapter().getItemCount() != 0) {
                int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1) {
                    if (!isLoading && !hasMoreItems) {
                        offset += limit;
                        _getDataServer();
                    }
                }
            }
        }

    };
    private void checkInternetConnection() {
        ConnectivityManager mgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = mgr.getActiveNetworkInfo();

        if (netInfo != null) {
            _getDataServer();
        } else {
            //No internet
            _loadDataFromLocalDB();
            Toast.makeText(MainActivity.this, getString(R.string.plz_chk_net), Toast.LENGTH_SHORT).show();

        }
    }

    private void _loadDataFromLocalDB() {
        DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().taskDao().getAll().
                subscribe(new DisposableSingleObserver<List<Contactitem>>() {
                    @Override
                    public void onSuccess(@NonNull List<Contactitem> contactitems) {
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                        itemRecyclerView.setLayoutManager(linearLayoutManager);
                        for(int i = 0; i< contactitems.size(); i++) {
                            Contactitem item = contactitems.get(i);
//                            inserttoLocalDB(item);
                            contactAdapter.addItem(item);
                        }
                        itemRecyclerView.setAdapter(contactAdapter);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                });
    }

    View.OnClickListener onViewTypeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            isViewWithList = !isViewWithList;
        setLitingLayout(isViewWithList);
        }
    };

    private void setLitingLayout(boolean isViewWithList) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        itemRecyclerView.setLayoutManager(linearLayoutManager);
        itemRecyclerView.setAdapter(contactAdapter);
    }

    ItemTouchHelper.SimpleCallback onitemswipe = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN | ItemTouchHelper.UP) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            Toast.makeText(getApplicationContext(), getString(R.string.item_deleted), Toast.LENGTH_SHORT).show();
            int position = viewHolder.getAdapterPosition();
            int id= contactAdapter.getId(position);
            contactAdapter.remove(position);
            contactAdapter.notifyItemRemoved(position);

            Single.fromCallable(() -> DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                    .taskDao().deleteById(id)).subscribeOn(Schedulers.io()).subscribe();

        }
    };
    private void _getDataServer() {
        hasMoreItems = true;
        progress.show();
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        String url =RetrofitClientInstance.BASE_URL+"users?page="+offset+"&limit="+limit;
        Log.v("test",url);
        Call<List<Contactitem>> call = service.getAllData(url);
        call.enqueue(new Callback<List<Contactitem>>() {
            @Override
            public void onResponse(Call<List<Contactitem>> call, Response<List<Contactitem>> response) {
                hasMoreItems = false;
                if (response.body().size() < limit) {
                    isLoading = true;
                }
                    for (int i = 0; i < response.body().size(); i++) {
                        Contactitem item = response.body().get(i);
                        inserttoLocalDB(item);
                        contactAdapter.addItem(item);
                    }

                setLitingLayout(isViewWithList);
                progress.dismiss();


            }

            @Override
            public void onFailure(Call<List<Contactitem>> call, Throwable t) {
                progress.dismiss();

            }
        });
    }
    private void inserttoLocalDB(Contactitem item) {
        LocalDb data = new LocalDb();
        data.setId(item.getId());
        data.setName(item.getName());
        data.setUsername(item.getUsername());
        data.setEmail(item.getEmail());
        data.setPhone(item.getPhone());
        data.setWebsite(item.getWebsite());
        Single.fromCallable(() -> DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                .taskDao().insert(data)).subscribeOn(Schedulers.io()).subscribe();

    }

}