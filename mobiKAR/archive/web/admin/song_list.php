<?php
include_once ('../scripts/myErrorHandler.php');
include_once ('Base.php');
$db= new Base();

$heading= "Lista piosenek";
$button= "Edytuj";

$songs = $db->getSongs();
$db->destroy();
?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>mobiKAR - admin - lista piosenek</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
</head>
<body>
	<h1><?=$heading?></h1>
<!--
<?php print_r($GLOBALS); ?>
-->
<?php
foreach ($songs as $song) {
	$id= $song['id'];
	$name = NULL;
	if (isset($song['title']) && isset($song['artist']))
		$name= $song['title'] . " - " . $song['artist'];
	$fileInfo = "";
	$midiName= MOBIKAR_PRODUCTS_DIR . 'songs/' . $id . ".midi";
	if (file_exists($midiName) == FALSE) {
		$fileInfo .= " Brak pliku midi ";
//		trigger_error("brak pliku $midiName", E_USER_NOTICE);
	}
	$mlyrName= MOBIKAR_PRODUCTS_DIR . 'songs/' . $id . ".mlyr";
	if (file_exists($mlyrName) == FALSE) {
		$fileInfo .= " Brak pliku mlyr ";
//		trigger_error("brak pliku $mlyrName", E_USER_NOTICE);
	}
	
	echo "<form  action='song_add.php' method='get'>";
	echo '<input type="hidden" name="id_song" value="'.$id.'">';
	echo "<input type='submit' value=".$id.">";
	echo "&#160; $name";
	if (strlen($fileInfo) > 0){
		echo "<b>$fileInfo</b>";
	}
	echo "<br/></form>\n";

}
?>			
	
	</body>
</html>





