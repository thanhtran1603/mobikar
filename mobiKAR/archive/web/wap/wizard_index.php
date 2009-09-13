<?php
include_once ('../../../scripts/config.php');
// należy odczytac nazwe katalogu i przekazac ja jako parametr
$path = split("/", $_SERVER['PHP_SELF']);
$dir = $path[count($path)-2];
header("Location: http://".MOBIKAR_SERVER_DOMAIN."/wizard_product.php?code=$dir");
?>