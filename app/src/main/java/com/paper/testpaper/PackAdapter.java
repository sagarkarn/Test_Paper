package com.paper.testpaper;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.paper.testpaper.models.QuestionPack;
import com.paper.testpaper.viewmodels.PackViewModel;

import java.util.List;

class PackAdapter extends RecyclerView.Adapter<PackAdapter.PackViewHolder> {
    private final Context context;
    private List<QuestionPack> questionPacks;

    PackAdapter(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public PackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_pack,parent,false);
        new RemoveDuplicateAsync(questionPacks,new ViewModelProvider((ViewModelStoreOwner) context).get(PackViewModel.class)).execute();
        return new PackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PackViewHolder holder, int position) {
        final QuestionPack questionPack = questionPacks.get(position);
        Log.d("QuestionFragment",questionPack.id);
        holder.viewName.setText(questionPack.name);
        if(questionPack.isAnswered){
            holder.viewCheckBtn.setEnabled(true);
            holder.viewCheckBtn.setVisibility(View.VISIBLE);
        }
        else{
            holder.viewCheckBtn.setEnabled(false);
            holder.viewCheckBtn.setVisibility(View.GONE);
        }
        holder.viewName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("id",questionPack.id);
                Navigation.findNavController(holder.itemView).navigate(R.id.action_mainPageFragment_to_questionFragment,bundle);
            }
        });
        holder.viewCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("id",questionPack.id);
                Navigation.findNavController(holder.itemView).navigate(R.id.action_mainPageFragment_to_resultFragment,bundle);
            }
        });

    }

    @Override
    public int getItemCount() {
        return questionPacks == null?0:questionPacks.size();
    }
    public void setAdapter(List<QuestionPack> questionPacks){
        this.questionPacks = questionPacks;
        notifyDataSetChanged();
    }

    static class PackViewHolder extends RecyclerView.ViewHolder {
        TextView viewName;
        ImageButton viewCheckBtn;
        PackViewHolder(@NonNull View itemView) {
            super(itemView);
            viewName = itemView.findViewById(R.id.pack_name);
            viewCheckBtn = itemView.findViewById(R.id.pack_completed);
        }
    }

    private static class RemoveDuplicateAsync extends AsyncTask<Void,Void,Void> {
        List<QuestionPack> questionPacks;
        PackViewModel viewModel;
        RemoveDuplicateAsync(List<QuestionPack> questionPacks, PackViewModel viewModel){
            this.questionPacks = questionPacks;
            this.viewModel = viewModel;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            for(int i = 0; i < questionPacks.size(); i++){
                QuestionPack questionPack = questionPacks.get(i);
                String name = questionPack.name;
                for(int j = i+1; j < questionPacks.size(); j++){
                    QuestionPack questionPack1 = questionPacks.get(j);
                    String name1 = questionPack1.name;
                    if(name.equalsIgnoreCase(name1)){
                        viewModel.delete(questionPack1);
                    }
                }
            }
            return null;
        }
    }
}
