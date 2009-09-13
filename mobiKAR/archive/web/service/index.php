<?php
ob_start();
include_once ('../scripts/myErrorHandler.php');
include_once ('../scripts/Database.php');
/*
 * Założenia:
 * tag jest unikalny w całym dokumencie
 * tag ma dokladna skladnie tag='
 * value konczy sie apostrofem ' 
 */
function getValFromTag($xml, $tag) {
	$ret= null;
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
$prop_key= getValFromTag($postdata, "property name='key' value");
$prop_version= getValFromTag($postdata, "property name='version' value");
$prop_screenwidth= getValFromTag($postdata, "property name='screen-width' value");
$prop_screenheight= getValFromTag($postdata, "property name='screen-height' value");
$prop_listoffset= getValFromTag($postdata, "<list offset");
$prop_listlimit= getValFromTag($postdata, " limit");
$prop_songid= getValFromTag($postdata, "<song id");
// pierdolona obsluga SDPe
$prop_provider= getValFromTag($postdata, "property name='provider' value");
// obsługa Pulsara
if ($prop_provider == null || strcmp("null", $prop_provider) == 0 || 0 == $prop_provider)
	$prop_provider= "1"; // 1 - Standard
//	$prop_provider= "2"; // 2 - Pulsar
trigger_error('[SERVICE] provider: '.$prop_provider.", login:".$auth_login.", password:".$auth_password, E_USER_NOTICE);
$outsource= null;
$songs = null;
$song = null;
$getType = null;

if ($auth_login != null){
	
	$db= new Database();
	
	if ($prop_provider == "2") {
		$rowUser= $db->getProvider($prop_provider);
		if (isset ($rowUser['outsource'])) {
			$outsource= $rowUser['outsource'];
			if ($outsource != null)
				$getType = "out";
		}
	}
	elseif ($prop_listoffset != null && $prop_listlimit != null){
		trigger_error('[SERVICE] before getUsersSongs', E_USER_NOTICE);
		$songs= $db->getLoginsSongs($auth_login, $prop_listoffset, $prop_listlimit);
		trigger_error('[SERVICE] after getUsersSongs ' . sizeof($songs), E_USER_NOTICE);
		$getType = "list";
	}
	elseif ($prop_songid != null){
		// todo: sprawdzenie czy sie nalezy
		$song = $db->getSong($prop_songid);
		// wystawienie piosenki
		copy(MOBIKAR_PRODUCTS_DIR . "songs/$prop_songid.mlyr", "../wap/get/$prop_songid.mlyr");
		copy(MOBIKAR_PRODUCTS_DIR . "songs/$prop_songid.midi", "../wap/get/$prop_songid.midi");
		if ($song != null)
			$getType = "song";
	}
	
	$db->destroy();
}
// przekierowanie do zewnetrznego zrodla
if ($getType == "out") {
	trigger_error("Getting RESPONSE from $outsource", E_USER_NOTICE);
	$arrayURL= parse_url($outsource);
	$host= null;
	$path= null;
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
	if ($idxStart !== false) {
		$response= substr($response, $idxStart);
	}
	$idxEnd= strrpos($response, '>');
	if ($idxEnd !== false) {
		$response= substr($response, 0, $idxEnd +1);
	}
	ob_end_clean();
	header("Content-type: text/xml");
	echo $response;
	return;
}
elseif($getType == "list") {
	$response= "";
	$response .= "<?xml version='1.0'?>\n";
	$response .=  "<!DOCTYPE response SYSTEM 'http://mobikar.net/response.dtd'>\n";
	$response .=  "<response>\n";
	$response .=  "    <list offset='".$prop_listoffset."' limit='".count($songs)."'>\n";
	foreach ($songs as $song ){
		$id = $song['id'];
		$title = $song['title'];
		$artist = $song['artist'];
		$response .=  "        <item id='".$id."' name='".$title." - ".$artist."'/>\n";
	}
	$response .=  "    </list>\n";
	$response .=  "</response>\n";
	ob_end_clean();
	header("Content-type: text/xml");
	print($response);
	trigger_error("[SERVICE] sent $response", E_USER_NOTICE);
}elseif($getType == "song") {
	ob_end_clean();
	header("Content-type: text/xml");
	echo "<?xml version='1.0'?>\n";
	echo "<!DOCTYPE response SYSTEM 'http://mobikar.net/response.dtd'>\n";
	echo "<response>\n";
	$id= $song['id'];
	$title= $song['title'];
	$artist= $song['artist'];
	
	echo "    <song id='".$id."' name='".$title."' artist='".$artist."'>\n";
	echo "        <resource type='lyric' addr='http://wap.mobikar.net/get/".$id.".mlyr'/>\n";
	echo "        <resource type='melody' addr='http://wap.mobikar.net/get/".$id.".midi'/>\n";
	echo "    </song>\n";
	echo "</response>\n";
} else{
	ob_end_clean();
	header("Content-type: text/xml");
	echo "<?xml version='1.0'?>\n";
	echo "<!DOCTYPE response SYSTEM 'http://mobikar.net/response.dtd'>\n";
	echo "<response>\n";
	echo "    <notice type='ERROR: Service unavailable'>\n";
	echo "        <describe lang='pl'>\n";
	echo "            Problem w obsludze. Prosze sprobowac w innym terminie\n";
	echo "        </describe>\n";
	echo "    </notice>\n";
	echo "</response>\n";
}	
?>