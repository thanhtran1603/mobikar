<?php
ob_start();
include ("../scripts/myErrorHandler.php");
include ('../scripts/Database.php');
/*
 * Założenia:
 * tag jest unikalny w całym dokumencie
 * tag ma dokladna skladnie tag='
 * value konczy sie apostrofem ' 
 */
function getValFromTag($xml, $tag) {
	$ret= NULL;
	$idx0= strpos($xml, $tag);
	if ($idx0 > 0) {
		$idxTagEnd= $idx0 +strlen($tag) + 2;
		$idx9= strpos($xml, "'", $idxTagEnd);
		$ret= substr($xml, $idxTagEnd, $idx9 - $idxTagEnd);
	}
	return $ret;
}
$postdata= file_get_contents("php://input");
trigger_error('content: '.$postdata, E_USER_NOTICE);
$auth_login= getValFromTag($postdata, 'login');
$auth_password= getValFromTag($postdata, 'password');
$prop_provider= getValFromTag($postdata, "property name='provider' value");
trigger_error('[SERVICE] provider: '.$prop_provider.", login:".$auth_login.", password:".$auth_password, E_USER_NOTICE);
$outsource= NULL;

$db= new Database();
$rowUser= $db->getProvider($prop_provider);
if (isset ($rowUser['outsource'])) {
	$outsource= $rowUser['outsource'];
}
$db->destroy();

if ($outsource != NULL) {
	$arrayURL= parse_url($outsource);
	$host= NULL;
	$path= NULL;
	if (isset ($arrayURL['host']))
		$host= $arrayURL['host'];
	if (isset ($arrayURL['path']))
		$path= $arrayURL['path'];
	$fp= fsockopen($host, 80, $errno, $errstr);
	$post= $postdata;
	$out= "";
	$out .= "POST ".$path." HTTP/1.1\r\n";
	$out .= "Host: ".$host."\r\n";
	$out .= "Keep-Alive: 300\r\n";
	$out .= "Connection: close\r\n";
	$out .= "Content-Type: application/x-www-form-urlencoded\r\n";
	$out .= "Content-Length: ".strlen($post)."\r\n\r\n";
	$out .= $post;
	$response= "";
	fwrite($fp, $out);
	$body= false;
	while (!feof($fp)) {
		$s= fgets($fp, 1024);
		if ($body)
			$response .= $s;
		if ($s == "\r\n")
			$body= true;
	}
	fclose($fp);
	$idxStart= strpos($response, '<');
	if ($idxStart !== FALSE) {
		$response= substr($response, $idxStart);
	}
	$idxEnd= strrpos($response, '>');
	if ($idxEnd !== FALSE) {
		$response= substr($response, 0, $idxEnd +1);
	}
	ob_end_clean();
	header("Content-type: text/xml");
	echo $response;
	return;
}
else {
	ob_end_clean();
	header("Content-type: text/html");
	echo "<?xml version='1.0'?>\n";
	echo "<!DOCTYPE response SYSTEM 'http://mobikar.net/response.dtd'>\n";
	echo "<response>\n";
	//echo "    <song id="0" name="Sz�a dzieweczka do laseczka" artist="piosenka ludowa">\n";
	//echo "        <resource type="lyric" addr="http://mobikar.net/song/123.mlyr"/>\n";
	//echo "        <resource type="melody" addr="http://mobikar.net/song/123.midi"/>\n";
	//echo "        <resource type="background" addr="http://mobikar.net/song/123.png"/>\n";
	//echo "        <resource type="adv-text" addr="http://mobikar.net/adv/43.txt"/>\n";
	//echo "        <resource type="adv-img" addr="http://mobikar.net/adv/43.png"/>\n";
	//echo "    </song>\n";
	//echo "    <list offset="0" limit="11">\n";
	//echo "        <item id="0" name="Sz�a dzieweczka do laseczka - piosenka ludowa"/>\n";
	//echo "        <item id="41" name="S�odkiego mi�ego �ycia - KOMBI"/>\n";
	//echo "    </list>\n";
	echo "    <notice type='ERROR: Service unavailable'>\n";
	echo "        <describe lang='pl'>\n";
	echo "            Problem w obsludze. Prosze sprobowac w innym terminie\n";
	echo "        </describe>\n";
	echo "    </notice>\n";
	echo "</response>\n";
}
?>