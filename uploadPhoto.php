<?php

if (isset($_POST['userName']) && isset($_POST['userImage'])) {

    // Get user data
    $userName = $_POST['userName'];
    $userImage = $_POST['userImage'];

    // Set image storage path
    $target_path = "UserImageFile";
    $imageStore = rand() . "_" . time() . ".jpeg";
    $target_path = $target_path . "/" . $imageStore;

    // Save the image file
    if (file_put_contents($target_path, base64_decode($userImage)) === false) {
        echo "Server Error...";
    } else {
        // Include database connection
        include 'connection.php';

        if ($con) {
            // Secure query using prepared statements
            $sql = "UPDATE Registration SET imageUrl = ? WHERE userName = ?";
            $stmt = mysqli_prepare($con, $sql);
            
            if ($stmt) {
                mysqli_stmt_bind_param($stmt, "ss", $imageStore, $userName);
                $result = mysqli_stmt_execute($stmt);

                if ($result) {
                    echo "Successfully updated";
                } else {
                    echo "Update error: " . mysqli_error($con);
                }

                // Close statement
                mysqli_stmt_close($stmt);
            } else {
                echo "SQL statement error";
            }
        } else {
            echo "Connection failed";
        }

        // Close the database connection
        mysqli_close($con);
    }
} else {
    echo "File required";
}

?>
