<?php
$text = "Michał";
$fh = fopen('/var/text.out', 'w+b');
$bin = pack('N', 74565);//0x12345);
fwrite($fh, $bin);
$bin = $text;//mb_convert_encoding($text, "UTF-8");
$first_convert = $bin;
fwrite($fh, $bin);
fclose($fh);
$fh = fopen('/var/text.out', 'rb');
$bin = fread($fh, 4);
$liczba = unpack('N', $bin);
$bin = fread($fh, 8192);
$test_decoded = mb_convert_encoding($bin, "UTF-8");
fclose($fh);
header("Content-type: text/html; charset=utf-8");
echo <<< KONIEC_TEKSTU
<?xml version="1.0"?>
<!DOCTYPE html PUBLIC " - //WAPFORUM//DTD XHTML Mobile 1.0//EN" "http://www.wapforum.org/DTD/xhtml-mobile10.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
 <head>
  <title> Tytuł </title>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" / >
 </head>
 <body>
 $text
 <br/>
 test_decoded: $test_decoded
 <br/>
 first_convert: $first_convert
 <br/>
 $bin
 </body>
</html>  
KONIEC_TEKSTU;
?>
