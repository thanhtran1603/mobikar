<?php
ob_start();
include_once ('../scripts/myErrorHandler.php');
include_once ('../scripts/utf8ToEntities.php');
include_once ('../scripts/create_midlet.php');
include_once ('../scripts/utils.php');
include_once ('../scripts/create_midlet.php');

$isAllowed = FALSE;
$reason = NULL;
$par_code = getParameter('code');
$par_app = getParameter('app');
$par_song = getParameter('song');
$browser = getValFromItem($_SERVER['HTTP_USER_AGENT']);

// TODO: sprawdzenie czy wszystko jets OK
$isAllowed = TRUE;

$reason_text = NULL;
if (strcmp($reason, "BAD_USER") == 0) {
	$reason_text = "B&#x142;&#x119;dny identyfikator u&#x17c;ytkownika: $uid";
}
elseif (strcmp($reason, "TOOCHEAP_CODE") == 0) {
	$reason_text = "U&#x17c;yty kod nie jest w&#x142;a&#x15b;ciwy dla wybranego produktu";
}
elseif (strcmp($reason, "BAD_CODE") == 0) {
	$reason_text = "B&#x142;&#x119;dny kod $kod";
}
elseif (strcmp($reason, "BAD_PRODUCT") == 0) {
	$reason_text = "B&#x142;&#x119;dny identyfikator produktu: $id";
}
elseif (strcmp($reason, "LOW_SCORE") == 0) {
	$reason_text = "Brak wystarczaj&#x105;cych &#x15b;rodk&#xf3;w na koncie ";
}
if ($reason != null)
	 trigger_error("reason_text:".$reason_text, E_USER_NOTICE);
trigger_error("isAllowed:".$isAllowed, E_USER_NOTICE);
if ($isAllowed) {
	$path = "get/$par_code";
	$midletFileName = $path . "/mobiKAR";
	// nazwa ma nie byc modyfikowania czyli == NULL
	$midletName = NULL;
	$id_producttype = 2; //singielek
	$appType = $par_app; //"midp20";
	$musicType = $par_song; //"midi";
	$arrSongFiles = array($path . "/song");
	$manifest = getJadFromCreatedJar($midletFileName, $midletName, $id_producttype, $appType, $musicType, $arrSongFiles);
	$buf = trim($manifest);
	$buf .= "\nMIDlet-Jar-Size: ".filesize($path . "/mobiKAR.jar");
	$buf .= "\nMIDlet-Jar-URL: http://".MOBIKAR_SERVER_DOMAIN. "/" . $path . "/mobiKAR.jar";
	$file = fopen($path . "/mobiKAR.jad", "wb");
	fwrite($file, $buf);
	fclose($file);
	ob_end_clean();
	header("Location: http://".MOBIKAR_SERVER_DOMAIN. "/" . $path . "/mobiKAR.jad");
	return;
} else {
	ob_end_clean();
	$wap_title = "mobiKAR - pobierz";
	include ("add_head.php");
?>
	<h1>Problem</h1>
	<div>
		<b>Brak dost&#x119;pu do produktu</b>
		<br/>
		<?php if ($reason_text != NULL) echo "$reason_text<br/>"; ?>
	</div>
	<h2>Przejd&#x17a; do:</h2>
	<div>
		<a href='start.php<?=$mySID?>'> Strona g&#x142;&#xf3;wna </a>
	</div>
<?php } ?>

<?php


	include ("add_foot.php");
?>