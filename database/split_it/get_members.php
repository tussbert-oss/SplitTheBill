<?php
$conn = new mysqli("localhost", "root", "", "split_itdb");


if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}


$bill_id = isset($_GET['bill_id']) ? $_GET['bill_id'] : 0;

$sql = "SELECT id, person_name, individual_amount, paid_amount 
        FROM bill_members 
        WHERE bill_id = '$bill_id'";

$result = $conn->query($sql);
$members = array();

if ($result) {
    while($row = $result->fetch_assoc()) {
        $members[] = $row;
    }
}


echo json_encode($members);

$conn->close();
?>