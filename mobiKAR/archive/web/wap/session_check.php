<?php
include_once ('../scripts/config.php');
include_once ('../scripts/myErrorHandler.php');
include_once ('../scripts/Database.php');
$uid = null;
$sid = null;
$kod = null;
$isAllowed = false;
$rowUser = null;
//trigger_error("_GET['uid']: $_GET['uid'] _GET['sid']: $_GET['sid']", E_USER_NOTICE);
if (isset ($_GET['uid']) && (isset ($_GET['sid']))) {
	$uid = $_GET['uid'];
	$sid = $_GET['sid'];
	trigger_error("uid: $uid sid: $sid", E_USER_NOTICE);
	if (isset ($_SERVER['HTTP_USER_AGENT']))
		$browser = $_SERVER['HTTP_USER_AGENT'];
	$db = new Database();
	$rowUser = $db->getUser(null, $uid);
	if ($rowUser != null) {
		if ((strcmp($sid, $rowUser['sid']) == 0) && (strcmp($browser, $rowUser['browser']) == 0) AND (MOBIKAR_SESSION_MAXTIME > $rowUser['delta'])) {
			$isAllowed = true;
			$kod = null;
		}
	}
	$db->destroy();
	
} elseif (!isset ($_GET['kod'])) {
		$kod = "0";
		$isAllowed = true;
	}
if (!$isAllowed && isset ($_GET['kod'])) {
	$kod = $_GET['kod'];
	$isAllowed = true;
}
$mySID = "";
if ($uid != null && $sid != null)
	$mySID = "?uid=$uid&amp;sid=$sid";
elseif ($kod != null) 
	$mySID = "?kod=$kod";

if (!$isAllowed) {
	header("Location: http://".MOBIKAR_SERVER_DOMAIN."/login.php?reason=BAD_SESSION");
	return;
}
?>