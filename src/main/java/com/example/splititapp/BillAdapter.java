package com.example.splititapp;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.BillViewHolder> {
    private List<Bill> billList;
    private boolean isHistory;

    public BillAdapter(List<Bill> billList, boolean isHistory) {
        this.billList = billList;
        this.isHistory = isHistory;
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bill, parent, false);
        return new BillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
        Bill bill = billList.get(position);
        holder.title.setText(bill.getTitle());
        holder.amount.setText("₱" + bill.getTotal());
        holder.date.setText(bill.getDate());

        int total = bill.getTotalMembers();
        int paid = bill.getPaidMembers();
        holder.progressText.setText(paid + "/" + total);

        if (isHistory) {

            holder.btnRemove.setVisibility(View.GONE);
            holder.itemView.setAlpha(0.75f);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), BillDetailsActivity.class);
                intent.putExtra("BILL_ID", bill.getId());
                intent.putExtra("IS_HISTORY", true);
                v.getContext().startActivity(intent);
                Toast.makeText(v.getContext(), "Viewing Archived Bill (Read-Only)", Toast.LENGTH_SHORT).show();
            });

        } else {
            holder.btnRemove.setVisibility(View.VISIBLE);
            holder.itemView.setAlpha(1.0f);

            holder.btnRemove.setOnClickListener(v -> {
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Remove Bill")
                        .setMessage("Move '" + bill.getTitle() + "' to history?")
                        .setPositiveButton("Remove", (dialog, which) -> {
                            deleteBillFromServer(bill.getId(), position, v);
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), BillDetailsActivity.class);
                intent.putExtra("BILL_ID", bill.getId());
                intent.putExtra("IS_HISTORY", false); // Normal mode
                v.getContext().startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() { return billList.size(); }

    public static class BillViewHolder extends RecyclerView.ViewHolder {
        TextView title, amount, date, progressText, btnRemove;

        public BillViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textTitle);
            amount = itemView.findViewById(R.id.textAmount);
            date = itemView.findViewById(R.id.textDueDate);
            progressText = itemView.findViewById(R.id.textProgress);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }

    private void deleteBillFromServer(String billId, int position, View v) {
        String url = "http://localhost/split_it/delete_bill.php";

        StringRequest deleteRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    billList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, billList.size());
                    Toast.makeText(v.getContext(), "Moved to History", Toast.LENGTH_SHORT).show();
                },
                error -> Log.e("DELETE_ERROR", error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", billId);
                return params;
            }
        };
        Volley.newRequestQueue(v.getContext()).add(deleteRequest);
    }
}