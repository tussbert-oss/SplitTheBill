<?php
$conn = new mysqli("localhost", "root", "", "split_itdb");

$member_id = $_POST['member_id'];
$amount = $_POST['amount'];

$sql_update = "UPDATE bill_members SET paid_amount = paid_amount + $amount WHERE id = '$member_id'";

if ($conn->query($sql_update) === TRUE) {
    
    $res = $conn->query("SELECT bill_id FROM bill_members WHERE id = '$member_id'");
    $row = $res->fetch_assoc();
    $bill_id = $row['bill_id'];

    $check_sql = "SELECT 
        (SELECT COUNT(*) FROM bill_members WHERE bill_id = '$bill_id') as total_count,
        (SELECT COUNT(*) FROM bill_members WHERE bill_id = '$bill_id' AND ROUND(paid_amount, 2) >= ROUND(individual_amount, 2)) as paid_count";
    
    $check_res = $conn->query($check_sql);
    $counts = $check_res->fetch_assoc();


    if ($counts['total_count'] > 0 && $counts['total_count'] == $counts['paid_count']) {
        $conn->query("UPDATE bills SET status = 1 WHERE id = '$bill_id'");
    }

    echo "Success";
} else {
    echo "Error";
}

$conn->close();
?>