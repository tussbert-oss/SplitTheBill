<?php
$conn = new mysqli("localhost", "root", "", "split_itdb");

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}


$sql = "SELECT b.*, 
        (SELECT COUNT(*) FROM bill_members WHERE bill_id = b.id) as total_members,
        (SELECT COUNT(*) FROM bill_members WHERE bill_id = b.id AND paid_amount >= individual_amount AND individual_amount > 0) as paid_members
        FROM bills b 
        WHERE b.status = 1 
        OR (
            (SELECT COUNT(*) FROM bill_members WHERE bill_id = b.id AND paid_amount >= individual_amount AND individual_amount > 0) = (SELECT COUNT(*) FROM bill_members WHERE bill_id = b.id)
            AND (SELECT COUNT(*) FROM bill_members WHERE bill_id = b.id) > 0
        )
        ORDER BY b.id DESC";

$result = $conn->query($sql);
$bills = array();

if ($result->num_rows > 0) {
    while($row = $result->fetch_assoc()) {
        $bills[] = $row;
    }
}

echo json_encode($bills);
$conn->close();
?>