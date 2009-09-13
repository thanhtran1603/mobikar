<?php
ob_start();
include_once ('../scripts/myErrorHandler.php');
$msg = $_GET['msg'];
$user = $_GET['user'];
$uid = $_GET['uid'];
$sid = $_GET['sid'];
$kod = $_GET['kod'];

$ua = $_SERVER['HTTP_USER_AGENT'];
$ra = $_SERVER['REMOTE_ADDR'];
$proxy = empty($_SERVER['HTTP_X_FORWARDED_FOR']) ? '' : "\nHTTP_X_FORWARDED_FOR: ".$_SERVER['HTTP_X_FORWARDED_FOR'];

mail ('info@mobikar.net', 'Komentarz z wap.mobikar.net', "uid:$uid\nsid:$sid\nkod:$kod\nuser:$user\n$ua\nREMOTE_ADDR: $ra$proxy\nmsg:\n---\n$msg\n---\n", "From: noreply@mobikar.net\nX-Mailer: PHP/" . phpversion());

ob_end_clean();
header("Location: http://".MOBIKAR_SERVER_DOMAIN."/start.php".$mySID);
return;
?>
