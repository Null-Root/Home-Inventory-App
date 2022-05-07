<?php
// Import PHPMailer classes into the global namespace
// These must be at the top of your script, not inside a function
use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\SMTP;
use PHPMailer\PHPMailer\Exception;

// Load Composer's autoloader
require 'vendor/autoload.php';

function MailerInstance()
{
    $Host = "smtp.gmail.com";
    $Username = "HouseholdInventoryApp@gmail.com";
    $Password = "mkjdvqvhbmxnfxuw";

    // Instantiation and passing `true` enables exceptions
    $mail = new PHPMailer(true);
    
    //Server settings
    //$mail->SMTPDebug = SMTP::DEBUG_SERVER;                    // Enable verbose debug output
    $mail->isSMTP();                                            // Send using SMTP
    $mail->Host       = $Host;                                  // Set the SMTP server to send through
    $mail->SMTPAuth   = true;                                   // Enable SMTP authentication
    $mail->Username   = $Username;                              // SMTP username
    $mail->Password   = $Password;                              // SMTP password
    $mail->SMTPSecure = PHPMailer::ENCRYPTION_STARTTLS;         // Enable TLS encryption; `PHPMailer::ENCRYPTION_SMTPS` encouraged
    $mail->Port       = 587;                                    // TCP port to connect to, use 465 for `PHPMailer::ENCRYPTION_SMTPS` above

    return $mail;
}

function EmailSendCode($Name, $Email, $Code)
{

    $mail = MailerInstance();

    //Recipients
    $mail->setFrom('App@email.com', 'Household Inventory App');
    $mail->addAddress($Email, 'User');                          // Add a recipient

    // Content
    $TopMessage = "_";
    $ConfirmCodeMsg = sprintf("This is your confirmation code: %s", $Code);
    $BottomMessage = "** This is a system-generated message, please do not reply **";

    $MessageContent_HTML = "<p>" . $TopMessage . "</p>" . "<br>" . $ConfirmCodeMsg . "<br><br>" . "<p> <strong>" . $BottomMessage . "</strong> </p>";
    $MessageContent_Plain = $TopMessage . "; " . $ConfirmCodeMsg . "; " . $BottomMessage;

    $mail->isHTML(true);                                        // Set email format to HTML
    $mail->Subject = 'Hello ' . $Name;

    $mail->Body    = $MessageContent_HTML;
    $mail->AltBody = $MessageContent_Plain;

    $mail->send();
}


?>