<?php
include_once ('pclzip.lib.php');
include_once ('config.php');
function jarCreate($jarName, $id, $product, $ver, $par_song) {
	trigger_error("jarCreate($jarName, $id, $product, $ver, $par_song)", E_USER_NOTICE);
	$id_producttype= null;
	if (isset ($product['product']['id_producttype']))
		$id_producttype= $product['product']['id_producttype'];
	// tworze archiwum
	//delete($jarName);
	// kasowanie plik
	if (file_exists($jarName))
		unlink($jarName);
	$archive= new PclZip($jarName);
	if ($archive == 0) {
		trigger_error("Error (new PclZip): ".$archive->errorInfo(true), E_USER_ERROR);
	}
	$products_dir= MOBIKAR_PRODUCTS_DIR;
	$pack_dir= $products_dir.$id_producttype."/".$ver."/";
	trigger_error("pack_dir: $pack_dir", E_USER_NOTICE);
	$v_list= $archive->add($pack_dir, PCLZIP_OPT_ADD_PATH, "./", PCLZIP_OPT_REMOVE_PATH, $pack_dir);
	if ($v_list == 0) {
		trigger_error("Error (archive->add): ".$archive->errorInfo(true), E_USER_ERROR);
	}
	$songs= null;
	if (isset ($product['songs']))
		$songs= $product['songs'];
	$i= -1;
	foreach ($songs as $song) {
		$i ++;
		$songId= null;
		if (isset ($song['id']))
			$songId= $song['id'];
		$song_file= MOBIKAR_TEMPORARY_DIR . "$i.midi";
		trigger_error("copy($products_dir.songs/$songId.$par_song, $song_file);".$archive->errorInfo(true), E_USER_ERROR);
		copy($products_dir."songs/$songId.$par_song", $song_file);
		$v_list= $archive->add($song_file, PCLZIP_OPT_ADD_PATH, "song", PCLZIP_OPT_REMOVE_PATH, MOBIKAR_TEMPORARY_DIR);
		if ($v_list == 0) {
			trigger_error("Error (archive->add): ".$archive->errorInfo(true), E_USER_ERROR);
		}
		$song_file= MOBIKAR_TEMPORARY_DIR . "$i.mlyr";
		copy($products_dir."songs/$songId.$par_song.mlyr", $song_file);
		$v_list= $archive->add($song_file, PCLZIP_OPT_ADD_PATH, "song", PCLZIP_OPT_REMOVE_PATH, MOBIKAR_TEMPORARY_DIR);
		if ($v_list == 0) {
			trigger_error("Error (archive->add): ".$archive->errorInfo(true), E_USER_ERROR);
		}
	}
	// zmiana nazwy w manifeście
	$list= $archive->extract(PCLZIP_OPT_BY_NAME, "META-INF/MANIFEST.MF", PCLZIP_OPT_EXTRACT_AS_STRING);
	if ($list == 0) {
		trigger_error("ERROR : ".$archive->errorInfo(true));
	}
	$manifest= $list[0]['content'];
	$manifest= str_replace("MIDlet-Name: mobiKAR", "MIDlet-Name: mobiKAR_".$id, $manifest);
	$file= fopen(MOBIKAR_TEMPORARY_DIR . "MANIFEST.MF", "wb");
	fwrite($file, $manifest);
	fclose($file);
	// usuwamy w archiwum starego manifesta
	$v_list= $archive->delete(PCLZIP_OPT_BY_NAME, "META-INF/MANIFEST.MF");
	if ($v_list == 0) {
		trigger_error("Error : ".$archive->errorInfo(true));
	}
	// wgrywany do archiwum nowego Manifesta
	$v_list= $archive->add(PCLZIP_TEMPORARY_DIR."MANIFEST.MF", PCLZIP_OPT_ADD_PATH, "META-INF", PCLZIP_OPT_REMOVE_PATH, MOBIKAR_TEMPORARY_DIR);
	if ($v_list == 0) {
		trigger_error("Error (archive->add): ".$archive->errorInfo(true), E_USER_ERROR);
	}
	// wysyłka w odpowiedzi manifesta
	return $manifest;
	//print_r($pack_dir);
}
/*
$product['product']['id_producttype']= 2;
$product['songs'][0]['id']= 1;
$ret= jarCreate("../get/test.jar", "test", $product);
print_r($ret);
*/
?>