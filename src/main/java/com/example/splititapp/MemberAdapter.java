package com.example.splititapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {

    private List<Member> memberList;
    private OnPayClickListener listener;

    public interface OnPayClickListener {
        void onPayClick(Member member);
    }

    public MemberAdapter(List<Member> memberList, OnPayClickListener listener) {
        this.memberList = memberList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        Member member = memberList.get(position);
        holder.name.setText(member.getName());

        String balanceText = "Paid: ₱" + member.getPaidAmount() + " / ₱" + member.getTotalAmount();
        holder.balance.setText(balanceText);

        if (member.getPaidAmount() >= member.getTotalAmount()) {
            holder.balance.setTextColor(android.graphics.Color.parseColor("#4CAF50"));
            holder.btnPay.setVisibility(View.GONE);
        } else {
            holder.balance.setTextColor(android.graphics.Color.parseColor("#73000000"));
            holder.btnPay.setVisibility(View.VISIBLE);
        }

        holder.btnPay.setOnClickListener(v -> listener.onPayClick(member));
    }

    @Override
    public int getItemCount() { return memberList.size(); }

    public static class MemberViewHolder extends RecyclerView.ViewHolder {
        TextView name, balance;
        Button btnPay;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.memberName);
            balance = itemView.findViewById(R.id.memberBalance);
            btnPay = itemView.findViewById(R.id.btnPay);
        }
    }
}