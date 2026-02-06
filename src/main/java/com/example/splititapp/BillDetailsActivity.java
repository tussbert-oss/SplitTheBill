package com.example.splititapp;

import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BillDetailsActivity extends AppCompatActivity {

    private RecyclerView rvMembers;
    private MemberAdapter adapter;
    private List<Member> memberList = new ArrayList<>();
    private String billId;
    private TextView detailTitle, detailTotal, detailRemaining;
    private boolean isHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_details);

        billId = getIntent().getStringExtra("BILL_ID");
        isHistory = getIntent().getBooleanExtra("IS_HISTORY", false);

        rvMembers = findViewById(R.id.rvMemberss);
        detailTitle = findViewById(R.id.detailTitles);
        detailTotal = findViewById(R.id.detailTotals);
        detailRemaining = findViewById(R.id.detailRemainings);

        rvMembers.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.imageButtonBack2).setOnClickListener(v -> finish());

        if (isHistory) {
            adapter = new MemberAdapter(memberList, member -> {
                Toast.makeText(this, "Archive record: Payments cannot be modified.", Toast.LENGTH_SHORT).show();
            });
            detailTitle.setText("Bill History (Read-Only)");
        } else {
            adapter = new MemberAdapter(memberList, this::showPayDialog);
        }

        rvMembers.setAdapter(adapter);
        loadMembers();
    }

    private void loadMembers() {
        String url = "http://10.0.2.2/split_it/get_members.php?bill_id=" + billId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        memberList.clear();
                        double totalAmount = 0;
                        double totalRemaining = 0;

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            Member member = new Member(
                                    obj.getString("id"),
                                    obj.getString("person_name"),
                                    obj.getDouble("individual_amount"),
                                    obj.getDouble("paid_amount")
                            );
                            memberList.add(member);
                            totalAmount += member.getTotalAmount();
                            totalRemaining += (member.getTotalAmount() - member.getPaidAmount());
                        }

                        detailTotal.setText("Total: ₱" + String.format("%.2f", totalAmount));
                        detailRemaining.setText("Remaining: ₱" + String.format("%.2f", totalRemaining));
                        adapter.notifyDataSetChanged();

                        if (!isHistory) {
                            checkIfBillCompleted(totalRemaining);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "JSON Error", Toast.LENGTH_SHORT).show();
                    }
                }, error -> Toast.makeText(this, "Connection Error", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void showPayDialog(Member member) {
        if (isHistory) return;

        EditText etAmount = new EditText(this);
        etAmount.setHint("Enter amount (e.g. 50.00)");
        etAmount.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);

        new AlertDialog.Builder(this)
                .setTitle("Add Payment for " + member.getName())
                .setView(etAmount)
                .setPositiveButton("Pay", (dialog, which) -> {
                    String input = etAmount.getText().toString().trim();

                    if (input.isEmpty()) {
                        Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double amountToPay;

                    try {
                        amountToPay = Double.parseDouble(input);
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!input.matches("^\\d+(\\.\\d{1,2})?$")) {
                        Toast.makeText(this, "Amount can have at most 2 decimal places", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double remainingBalance = member.getTotalAmount() - member.getPaidAmount();

                    if (amountToPay <= 0) {
                        Toast.makeText(this, "Amount must be greater than 0", Toast.LENGTH_SHORT).show();
                    }
                    else if (amountToPay > remainingBalance) {
                        Toast.makeText(this, "Amount exceeds remaining balance (₱" + String.format("%.2f", remainingBalance) + ")", Toast.LENGTH_LONG).show();
                    }
                    else {
                        updatePaymentInDB(member.getId(), String.valueOf(amountToPay));
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private void updatePaymentInDB(String memberId, String amount) {
        String url = "http://localhost/split_it/update_payment.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(this, "Payment Updated", Toast.LENGTH_SHORT).show();
                    loadMembers();
                }, error -> Toast.makeText(this, "Payment update failed", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("member_id", memberId);
                params.put("amount", amount);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(postRequest);
    }

    private void checkIfBillCompleted(double remainingTotal) {
        if (remainingTotal <= 0 && !memberList.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Bill Completed! 🎉")
                    .setMessage("Everyone has paid. This bill has been automatically moved to History.")
                    .setCancelable(false)
                    .setPositiveButton("OK", (dialog, which) -> {
                        new Handler().postDelayed(this::finish, 500);
                    })
                    .show();
        }
    }
}
