<?php
include ("../scripts/myErrorHandler.php");
$id_song= NULL;
if (isset ($_GET['id_song'])) {
	$id_song= $_GET['id_song'];
}
if ($id_song != null){
	$file=MOBIKAR_PRODUCTS_DIR . "songs/$id_song.mlyr";
	$filesize=filesize($file);
header( "Content-type: text/plain; charset=utf-8" );
//header( "Content-type: text/plain" );
//header( "Content-length: $filesize");
readfile($file);	
}
?>
