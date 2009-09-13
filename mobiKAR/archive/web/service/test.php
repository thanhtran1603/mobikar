<?php
include ("../scripts/myErrorHandler.php");
$serviceFile= "/service/index2.php";
$post= "";
$post .= "<!DOCTYPE request SYSTEM 'http://mobikar.net/request.dtd'>";
$post .= "<request>";
$post .= "    <language name='pl'/>";
$post .= "    <authorization login='+48507062270' password='ala ma kota'/>";
$post .= "    <browser user-agent='MOT-V980/bla bla bla'/>";
$post .= "    <property name='provider' value='2'/>";
$post .= "    <property name='key' value='0'/>";
$post .= "    <property name='screen-width' value='176'/>";
$post .= "    <property name='screen-height' value='202'/>";
$post .= "    <property name='version' value='0.0.30'/>";
$post .= "    <get>";
//$post .="        <song id='123'/>";
$post .= "        <list offset='0' limit='11'/>";
$post .= "    </get>";
$post .= "</request>";
$host= $_SERVER["SERVER_NAME"];
$fp= fsockopen($host, 80, $errno, $errstr);
$out= "";
$out .= "POST ".$serviceFile." HTTP/1.1\r\n";
$out .= "Host: ".$host."\r\n";
$out .= "Keep-Alive: 300\r\n";
$out .= "Connection: keep-alive\r\n";
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
echo $response;
?>