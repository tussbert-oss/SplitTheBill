<?php
$conn = new mysqli("localhost", "root", "", "split_itdb");

$bill_id = $_POST['id'];

// Change status to 1 (Archived) instead of deleting
$sql = "UPDATE bills SET status = 1 WHERE id = '$bill_id'";

if ($conn->query($sql) === TRUE) {
    echo "Archived Successfully";
} else {
    echo "Error: " . $conn->error;
}
$conn->close();
?>