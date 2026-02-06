<?php
$host = "localhost";
$user = "root";
$pass = "";
$db   = "split_itdb";

$conn = mysqli_connect($host, $user, $pass, $db);

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $email    = $_POST['email'];
    $password = $_POST['password'];

    $email = mysqli_real_escape_string($conn, $email);
    $password = mysqli_real_escape_string($conn, $password);

    $sql = "SELECT * FROM users WHERE email='$email' AND password='$password'";
    $result = mysqli_query($conn, $sql);

    if (mysqli_num_rows($result) > 0) {
        $row = mysqli_fetch_assoc($result);
        
        echo json_encode([
            "status" => "success",
            "message" => "Login Successful",
            "id" => $row['id'] 
        ]);
    } else {
        echo json_encode([
            "status" => "error",
            "message" => "Invalid Email or Password"
        ]);
    }
}
mysqli_close($conn);
?>