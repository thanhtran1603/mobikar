<?php
$postdata= file_get_contents("php://input");
if ($postdata != FALSE) {
	header("Content-type: text/plain");
	echo "Otrzymane w zapytaniu POST[".$postdata."]";
}
else {
	$host= $_SERVER["SERVER_NAME"];
	$fp= fsockopen($host, 80, $errno, $errstr);
	$post= "Ala ma kota";
	$out= "";
	$out .= "POST ".$_SERVER["PHP_SELF"]." HTTP/1.1\r\n";
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
	echo $response;
}
?>

