<?php
header('Content-Type: application/json');

$conn = new mysqli("localhost", "root", "", "split_itdb");

if ($conn->connect_error) {
    die(json_encode(["status" => "error", "message" => "Connection failed"]));
}

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $user_id   = $conn->real_escape_string($_POST['user_id']);
    $fullname  = $conn->real_escape_string($_POST['fullname']);
    $email     = $conn->real_escape_string($_POST['email']);
    $birthdate = $conn->real_escape_string($_POST['birthdate']);
    $password  = $conn->real_escape_string($_POST['password']); 

    $sql = "UPDATE users SET fullname='$fullname', email='$email', birthdate='$birthdate', password='$password' WHERE id='$user_id'";

    if ($conn->query($sql) === TRUE) {
        echo json_encode(["status" => "success", "message" => "Profile updated successfully"]);
    } else {
        echo json_encode(["status" => "error", "message" => "Update failed: " . $conn->error]);
    }
} else {
    echo json_encode(["status" => "error", "message" => "Invalid request method"]);
}

$conn->close();
?>