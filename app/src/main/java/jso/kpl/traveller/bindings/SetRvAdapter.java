package jso.kpl.traveller.bindings;

import android.util.Log;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

//RecyclerView에 Adapter 적용
public class SetRvAdapter {
    @BindingAdapter({"setAdapter"})
    public static void bindRecyclerViewAdapter(@NotNull RecyclerView recyclerView, RecyclerView.Adapter<?> adapter) {

        Log.d("TAG.SetAdapter", "Adapter");

        recyclerView.setLayoutManager( new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(adapter);
    }
}
