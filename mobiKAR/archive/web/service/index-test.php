<?php
ob_start();
include ("../scripts/myErrorHandler.php");
function getValFromTag($xml, $tag, $start) {
	$ret= NULL;
	$idx0= strpos($xml, $tag, $start);
	if ($idx0 > 0) {
		$idxTagEnd= $idx0 +strlen($tag) + 2;
		$idx9= strpos($xml, "'", $idxTagEnd);
		$ret= substr($xml, $idxTagEnd, $idx9 - $idxTagEnd);
	}
	return $ret;
}
$postdata= file_get_contents("php://input");
trigger_error('content: '.$postdata, E_USER_NOTICE);
$songId= getValFromTag($postdata, '<song id', 0);
$sendXml= "";
$sendXml .= "<?xml version='1.0'?>\n";
$sendXml .= "<!DOCTYPE response SYSTEM 'http://mobikar.net/response.dtd'>\n";
$sendXml .= "<response>\n";
if ($songId != NULL) {
	$sendXml .= "    <song id='0' name='Szla dzieweczka do laseczka' artist='piosenka ludowa'>\n";
	$sendXml .= "        <resource type='melody' addr='http://mobikar.net/get/0.midi'/>\n";
	$sendXml .= "        <resource type='lyric' addr='http://mobikar.net/get/0.mlyr'/>\n";
	$sendXml .= "    </list>\n";	
}
else {
	$sendXml .= "    <list offset='0' limit='11'>\n";
	$sendXml .= "        <item id='0' name='Szla dzieweczka do laseczka - piosenka ludowa'/>\n";
	$sendXml .= "    </list>\n";
}
$sendXml .= "</response>\n";
ob_end_clean();
header("Content-Type: text/xml; charset=utf-8");
header("Content-Length: ".strlen($sendXml));
echo $sendXml;
?>