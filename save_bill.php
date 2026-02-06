<?php
$conn = new mysqli("localhost", "root", "", "split_it");

if ($conn->connect_error) {
    die("DB Error");
}

$today = date("Y-m-d");

$sql = "SELECT id, title FROM bills
        WHERE due_date = '$today'
        AND is_paid = 0
        AND notified = 0
        LIMIT 1";

$result = $conn->query($sql);

if ($result->num_rows > 0) {
    $row = $result->fetch_assoc();

    echo json_encode([
        "due" => true,
        "title" => $row["title"]
    ]);

    // mark as notified
    $conn->query(
        "UPDATE bills SET notified = 1 WHERE id = " . $row["id"]
    );
} else {
    echo json_encode(["due" => false]);
}
?>
