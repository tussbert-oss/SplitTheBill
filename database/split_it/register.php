<?php
$host = "localhost";
$user = "root";
$pass = "";
$db   = "split_itdb";

$conn = mysqli_connect($host, $user, $pass, $db);

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $fullname = $_POST['fullname'];
    $email     = $_POST['email'];
    $password  = $_POST['password'];
    $birthdate = $_POST['birthdate'];

    $sql = "INSERT INTO users (fullname, email, password, birthdate) 
            VALUES ('$fullname', '$email', '$password', '$birthdate')";

    if (mysqli_query($conn, $sql)) {
        echo "Registration Successful";
    } else {
        echo "Error: " . mysqli_error($conn);
    }
}
mysqli_close($conn);
?>