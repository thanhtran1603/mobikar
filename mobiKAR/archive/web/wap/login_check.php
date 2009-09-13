<?php
ob_start();
include_once ('../scripts/myErrorHandler.php');
include_once ('../scripts/Database.php');
include_once ('../scripts/utils.php');
//	print_r($GLOBALS);
$login= NULL;
$password= NULL;
$isAllowed= FALSE;
$uid= NULL;
$sid= NULL;
$reason= "BAD_USER";
trigger_error("isset:".isset ($_GET['l']), E_USER_NOTICE);
if (isset ($_GET['l'])) {
	$login= $_GET['l'];
	$password= $_GET['p'];
	$login = trim(strtolower($login));
	$login = urlencode(htmlentities(htmlspecialchars($login), ENT_QUOTES));
	$password = urlencode(htmlentities(htmlspecialchars($password), ENT_QUOTES));
	trigger_error(" /\ ", E_USER_NOTICE);
	if (isset ($_SERVER['HTTP_USER_AGENT']))
		$browser= $_SERVER['HTTP_USER_AGENT'];
	$db= new Database();
	$rowUser= $db->getUser($login, NULL);
	trigger_error(" \/ ", E_USER_NOTICE);
	if ($rowUser != NULL) {
		$uid= getValFromItem($rowUser['id']);
		if (strcmp($password, getValFromItem($rowUser['password'])) == 0) {
			$isAllowed= TRUE;
			$sid= getCode();
			if ($db->updateUserSid($uid, $sid) == FALSE) {
				$reason= "ERROR";
				$isAllowed= FALSE;
			}
		}
		else {
			$reason= "BAD_PASSWORD";
		}
		trigger_error("login:".$login." password(".$rowUser['password']."):".$password."uid:".$uid." sid".$sid, E_USER_NOTICE);
	}
	$db->destroy();
}
if ($isAllowed) {
	ob_end_clean();
	header("Location: http://".MOBIKAR_SERVER_DOMAIN."/start.php?uid=$uid&sid=$sid");
	return;
}
else {
	ob_end_clean();
	header("Location: http://".MOBIKAR_SERVER_DOMAIN."/login.php?reason=$reason");
	return;
}
?>