<?php
include ("../scripts/myErrorHandler.php");
include_once ("Base.php");
$type= 1;
if (isset ($_GET['type'])) {
	$type= $_GET['type'];
}
$heading= "Lista produktów o typie ".$type;
$button= "Edytuj";
$db= new Base();
$products= $db->getProducts($type);
$contents= NULL;
foreach ($products as $product) {
	$id= $product['id'];
	$content= $db->getContents($id);
	$contents[$id]= $content;
}
$db->destroy();
?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>mobiKAR - admin - lista produktów</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
</head>
<body>
	<h1><?=$heading?></h1>
<!--
<?php print_r($GLOBALS); ?>
-->
<?php

foreach ($products as $product) {
	$id= $product['id'];
	$name= "";
	$fileInfo= NULL;
	$dir= MOBIKAR_PRODUCTS_DIR;
	$midiName= $dir . 'songs/' . $id . ".midi";
	if (file_exists($midiName) == FALSE) {
		$fileInfo= "Brak pliku midi ";
	}
	else {
		$fileInfo .= " Plik [$midiName] zmodyfikowany ".date("Y-m-d H:i:s", filemtime($midiName));
		$fileInfo .= ", rozmiar ".filesize($midiName)." bajtów";
	}	
	$mlyrName= $dir . 'songs/' . $id . ".mlyr";
	if (file_exists($mlyrName) == FALSE) {
		$fileInfo .= "Brak pliku mlyr ";
	}
	else {
		$fileInfo .= " Plik [$mlyrName] zmodyfikowany ".date("Y-m-d H:i:s", filemtime($mlyrName));
		$fileInfo .= ", rozmiar ".filesize($mlyrName)." bajtów";
	}	
	$content= NULL;
	if (isset ($contents[$id])) {
		$content= $contents[$id];
		foreach ($content as $item) {
			if (isset ($item['id']) && isset ($item['title']) && isset ($item['artist'])) {
				$name .= "({$item['id']}) {$item['title']} - {$item['artist']}<br/>";
			}
		}
	}
		// nieużywane
	$fileInfo = "";
	
	echo "<form  action='product_add.php' method='get'>";
	echo '<input type="hidden" name="id_product" value="'.$id.'">';
	echo "<input type='submit' value='".$id."'>";
	echo "&#160; $fileInfo<br/>";
	echo "&#160; $name<br/>";
	echo "</form>\n";
}
?>			
	
	</body>
</html>






