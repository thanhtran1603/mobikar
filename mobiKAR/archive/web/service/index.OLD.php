<?php
ob_start();
include ("../scripts/myErrorHandler.php");
$postdata = file_get_contents("php://input");
trigger_error('content: '.$postdata, E_USER_NOTICE);
$sendXml = "";
$sendXml .= "<?xml version='1.0'?>\n";
$sendXml .= "<!DOCTYPE response SYSTEM 'http://mobikar.net/response.dtd'>\n";
$sendXml .= "<response>\n";
$sendXml .= "    <notice type='ERROR: Service unavailable'>\n";
$sendXml .= "        <describe lang='pl'>\n";
$sendXml .= "            Problem w obsłudze. Proszę spróbowac w innym terminie\n";
$sendXml .= "        </describe>\n";
$sendXml .= "    </notice>\n";
$sendXml .= "</response>\n";
ob_end_clean();
header( "Content-Type: text/xml; charset=utf-8" );
header( "Content-Length: ".strlen($sendXml) );
echo $sendXml;
?>