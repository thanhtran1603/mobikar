<?php
include_once ("../scripts/config.php");
$contentType= "application/vnd.wap.xhtml+xml";
foreach(unserialize(MOBIKAR_BROWSER_HTML) as $a){
	// strstr zwraca FALSE jak nie znajdzie szukanego ciągu
	if (strstr($_SERVER['HTTP_USER_AGENT'], $a) != FALSE) {
		$contentType= "text/html";
		break;
	}
}
header("Content-type: $contentType");
echo <<< KONIEC_TEKSTU
<?xml version="1.0"?>
<!DOCTYPE html PUBLIC " - //WAPFORUM//DTD XHTML Mobile 1.0//EN" "http://www.wapforum.org/DTD/xhtml-mobile10.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>$wap_title</title>
<meta name="keywords" content="mobile KARAOKE mobilne telefon java j2me aplikacja multimedia multimedialna piosenka tekst śpiew"/>
<link href="layout.css" rel="stylesheet" type="text/css" title="fon"/>
</head>
<body>
<div class="areaHead"><img src="img/mobikar-logo.gif" alt="mobiKAR - mobile KARAOKE"/></div>
KONIEC_TEKSTU;
?>


