<?php
include_once ('../scripts/myErrorHandler.php');
include_once ('Base.php');
$db= new Base();
$songs= $db->getSongs();
$productSongs= NULL;
$id_product= NULL;
if (isset ($_GET['id_product'])) {
	$id_product= $_GET['id_product'];
}
if (isset ($_POST['id_product'])) {
	$id_product= $_POST['id_product'];
}
$id_producttype= 1;
if (isset ($_GET['id_producttype'])) {
	$id_producttype= $_GET['id_producttype'];
}
$coins= 5;
if (isset ($_GET['coins'])) {
	$coins= $_GET['coins'];
}
$choices= NULL;
foreach ($songs as $song) {
	$id= $song['id'];
	if (isset ($_GET["song_$id"])) {
		$choices[$id]= $id;
	}
}
$stan= null;
if (count($_GET) == 0 && count($_POST) == 0)
	$stan= "BEFORE_ADD_NEW";
if (count($_GET) != 0) {
	if (isset ($_GET['id_product']))
		$stan= "VIEW";
	if (count($_GET) > 1)
		$stan= "CHANGE";
}
$heading= null;
$button= null;
trigger_error("stan:$stan", E_USER_NOTICE);
if (strcmp($stan, "BEFORE_ADD_NEW") == 0) {
	$heading= "Dodaj produkt";
	$button= "Dodaj";
}
elseif ($stan == "VIEW") {
	$heading= "Edycja produktu o id $id_product";
	$button= "Zmieñ";
}
elseif ($stan == "CHANGE") {
	if ($id_product == NULL) {
		$id_product= $db->addProduct($id_producttype, $coins);
		trigger_error(" nowy id_product:$id_product", E_USER_NOTICE);
		$heading= "Dodano produktu o id $id_product";
		$button= "Zmieñ";
	}
	if ($id_product != NULL) {
		if ($id_producttype != NULL && $coins != NULL) {
			$db->setProduct($id_product, $id_producttype, $coins);
			$db->cleanConSongProduct($id_product);
			foreach ($choices as $choice) {
				$id_song= $choice;
				$db->addConSongProduct($id_song, $id_product);
			}
			if ($heading == null){
				$heading= "Zmieniono produktu o id $id_product";
				$button= "Zmieñ";
			}
		}
	}
	if ($heading == null){
		$heading= "Edycja produktu o id $id_product";
		$button= "Zmieñ";
	}
}
// edycja
$fileInfo= "";
if ($id_product != NULL) {
	$productSongs= $db->getProductSongs($id_product);
	// wywali³em info o produkcie
	$product= $db->getProduct($id_product);
	$id_producttype= $product['id_producttype'];
	$coins= $product['coins'];
	$choices= NULL;
	foreach ($productSongs as $song) {
		//		if (isset ($song['id'])) {
		$id_song= $song['id'];
		$choices["$id_song"]= $id_song;
		//trigger_error("id_song: $id_song", E_USER_NOTICE);
		// infa o pliczkach
		$midiName= MOBIKAR_PRODUCTS_DIR . 'songs/' . $id_song . ".midi";
		//trigger_error("midiName: $midiName", E_USER_NOTICE);
		if (file_exists($midiName) == FALSE) {
			$fileInfo .= "<br/> Brak pliku midi ";
		}
		else {
			$fileInfo .= "<br/> Plik [$midiName] zmodyfikowany ".date("Y-m-d H:i:s", filemtime($midiName));
			$fileInfo .= ", rozmiar ".filesize($midiName)." bajtów";
		}	
		$mlyrName= MOBIKAR_PRODUCTS_DIR . 'songs/' . $id_song . ".mlyr";
		//trigger_error("mlyrName: $mlyrName", E_USER_NOTICE);
		if (file_exists($mlyrName) == FALSE) {
			$fileInfo .= "<br/> Brak pliku mlyr ";
		}
		else {
			$fileInfo .= "<br/> Plik [$mlyrName] zmodyfikowany ".date("Y-m-d H:i:s", filemtime($mlyrName));
			$fileInfo .= ", rozmiar ".filesize($mlyrName)." bajtów";
		}
		
	}
	//	}	
}
$db->destroy();
/*
 * Created on 2005-05-27
 *
 * To change the template for this generated file go to
 * Window - Preferences - PHPeclipse - PHP - Code Templates
 */
?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>mobiKAR - admin - dodaj product</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
</head>
<body>
	<h1><?=$heading?></h1>

<!-- nie ma wgrywania ca³ych archiwów
	
	<form action="product_add.php" method="post" enctype="multipart/form-data">
<?php

if ($id_product != NULL)
	echo '<input type="hidden" name="id_product" value="'.$id_product.'">';
?>
		Wyœlij plik MIDI: <input type="file" name="file_midi"><br>
		<input type="submit" name="submit_midi" value="Wyœlij">
	</form>
	<br/>
-->	
	<?=$fileInfo?>	
	<br/>
<!--
<?php print_r($GLOBALS); ?>
-->
		<form  action="product_add.php" method="get">
<?php

if ($id_product != NULL)
	echo '<input type="hidden" name="id_product" value="'.$id_product.'">';
?>
		<select name="id_producttype" id="id_producttype">
			<option value="1" <?=($id_producttype==1?"selected":"")?>>1 - sama piosenka</option>
			<option value="2" <?=($id_producttype==2?"selected":"")?>>2 - singiel</option>
			<option value="3" <?=($id_producttype==3?"selected":"")?>>3 - paczka</option>
		</select>
		Wartoœæ punktowa	<input type="text" name="coins"  maxlength="1" <?=($coins!=NULL?'value="'.$coins.'"':'')?> >
		<br/>
  
<?php

foreach ($songs as $song) {
	$id= $song['id'];
	$name= NULL;
	if (isset ($song['title']) && isset ($song['artist']))
		$name= $song['title']." - ".$song['artist'];
	$checked= "";
	if (isset ($choices[$id])) {
		$checked= 'checked="true"';
	}
	echo "&#160; $name<input type='checkbox' name='song_".$id."' value='".$id."' $checked/>;";
}
?>			
			<br/>
			<input type="submit"  value="<?=$button?>">
		</form>
	
	</body>
</html>