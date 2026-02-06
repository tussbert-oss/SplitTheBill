<?php
$host = "localhost";
$user = "root"; // Default XAMPP user
$pass = "";     // Default XAMPP password is empty
$db   = "split_itDB";

$conn = mysqli_connect($host, $user, $pass, $db);

if($conn) {
    echo "Success! Database is connected.";
} else {
    echo "Connection Failed.";
}
?>