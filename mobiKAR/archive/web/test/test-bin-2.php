<?php
$title = "<empty>";
$music = "<empty>";
$lyrics = "<empty>";
$artist = "<empty>";
$creator = "<empty>";
$version = "<empty>";

$fh = fopen('0.mlyr', 'rb');
$bin = fread($fh, 4);
$tag = $bin;
$bin = fread($fh, 4);
$liczba = unpack('N', $bin);

while (!feof($fh)) {
	$tag_name = fread($fh, 4);
	$bin = fread($fh, 4);
	$array = unpack('N', $bin);
	$tag_size = $array[1];
	$pad = fread($fh, 2);
	$tag_value = fread($fh, $tag_size - 2);
	if (strpos($tag_name, 'TITL') !== FALSE){
		$title = $tag_value;
	}
	else if (strpos($tag_name, 'MUSI') !== FALSE){
		$music = $tag_value;
	}
	else if (strpos($tag_name, 'LYRI') !== FALSE){
		$lyrics = $tag_value;
	}
	else if (strpos($tag_name, 'ARTI') !== FALSE){
		$artist = $tag_value;
	}
	else if (strpos($tag_name, 'CREA') !== FALSE){
		$creator = $tag_value;
	}
	else if (strpos($tag_name, 'VER$') !== FALSE){
		$version = $tag_value;
	}
	else if (strpos($tag_name, 'TEXT') !== FALSE){
		break;
	}
	else if (strpos($tag_name, 'IDXS') !== FALSE){
		break;
	}
}
fclose($fh);
header("Content-type: text/html; charset=utf-8");
echo<<< KONIEC_TEKSTU
<?xml version="1.0"?>
<!DOCTYPE html PUBLIC " - //WAPFORUM//DTD XHTML Mobile 1.0//EN" "http://www.wapforum.org/DTD/xhtml-mobile10.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
 <head>
  <title> Tytuł </title>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" / >
 </head>
 <body>
title:$title
 <br/>
music:$music
 <br/>
lyrics:$lyrics
 <br/>
artist:$artist
 <br/>
creator:$creator
 <br/>
version:$version

 </body>
</html>  
KONIEC_TEKSTU;
?>


