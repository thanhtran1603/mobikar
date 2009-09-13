<?php
include_once ('pclzip.lib.php');
include_once ('config.php');
/**
 * createMidlet tworzy w miejscu wskazywanym przez midletFileName dwa pliki: jad i jar
 * zawierających nazwy plików piosenek zawartych w tablicy arrSongFiles
 * Nazwy plików mają by rozszerzeń ".midi", ".mlyr". Rozszerzenia te zostaną automatycznie dodane
 * musicType przyjmuje wartości: "midi", "15k.midi", "mp3", ...
 * appType przyjmuje wartości: "midp20", "mmapi", "mot", "sam", "sie", ...
 * id_producttype przyjmuje wartosci 2 - singiel, 3 - paczka
 *
 */
function getJadFromCreatedJar($midletFileName, $midletName, $id_producttype, $appType, $musicType, $arrSongFiles) {
	//}, $jarName, $id, $product, $ver, $par_song) {
	trigger_error("createMidlet($midletFileName, $midletName, $id_producttype, $appType, $musicType, $arrSongFiles)", E_USER_NOTICE);
	// tworze archiwum
	$jarName = $midletFileName . ".jar";
	// kasowanie pliku
	if (file_exists($jarName))
		unlink($jarName);
	$archive = new PclZip($jarName);
	if ($archive == 0) {
		trigger_error("Error (new PclZip): " . $archive->errorInfo(true), E_USER_ERROR);
	}
	$products_dir = MOBIKAR_PRODUCTS_DIR;
	$pack_dir = $products_dir . $id_producttype . "/" . $appType . "/";
	trigger_error("pack_dir: $pack_dir", E_USER_NOTICE);
	$v_list = $archive->add($pack_dir, PCLZIP_OPT_ADD_PATH, "./", PCLZIP_OPT_REMOVE_PATH, $pack_dir);
	if ($v_list == 0) {
		trigger_error("Error (archive->add): " . $archive->errorInfo(true), E_USER_ERROR);
	}
	$i = -1;
	foreach ($arrSongFiles as $songFileName) {
		$i++;
		$songFileNameSource = $songFileName . '.' . $musicType;
		$songFileNameArchived = MOBIKAR_TEMPORARY_DIR . "$i.midi";
		trigger_error("copy($songFileNameSource, $songFileNameArchived);" . $archive->errorInfo(true), E_USER_ERROR);
		copy($songFileNameSource, $songFileNameArchived);
		$v_list = $archive->add($songFileNameArchived, PCLZIP_OPT_ADD_PATH, "song", PCLZIP_OPT_REMOVE_PATH, MOBIKAR_TEMPORARY_DIR);
		if ($v_list == 0) {
			trigger_error("Error (archive->add): " . $archive->errorInfo(true), E_USER_ERROR);
		}
		$songFileNameSource = $songFileName . '.' . $musicType . ".mlyr";
		$songFileNameArchived = MOBIKAR_TEMPORARY_DIR . "$i.mlyr";
		trigger_error("copy($songFileNameSource, $songFileNameArchived);" . $archive->errorInfo(true), E_USER_ERROR);
		copy($songFileNameSource, $songFileNameArchived);
		$v_list = $archive->add($songFileNameArchived, PCLZIP_OPT_ADD_PATH, "song", PCLZIP_OPT_REMOVE_PATH, MOBIKAR_TEMPORARY_DIR);
		if ($v_list == 0) {
			trigger_error("Error (archive->add): " . $archive->errorInfo(true), E_USER_ERROR);
		}
	}
	// zmiana nazwy w manifeście
	$list = $archive->extract(PCLZIP_OPT_BY_NAME, "META-INF/MANIFEST.MF", PCLZIP_OPT_EXTRACT_AS_STRING);
	if ($list == 0) {
		trigger_error("ERROR : " . $archive->errorInfo(true));
	}
	$manifest = $list[0]['content'];
	if ($midletName != NULL) {
		$manifest = str_replace("MIDlet-Name: mobiKAR", "MIDlet-Name: " . $midletName, $manifest);
		$file = fopen(MOBIKAR_TEMPORARY_DIR . "MANIFEST.MF", "wb");
		fwrite($file, $manifest);
		fclose($file);
		// usuwamy w archiwum starego manifesta
		$v_list = $archive->delete(PCLZIP_OPT_BY_NAME, "META-INF/MANIFEST.MF");
		if ($v_list == 0) {
			trigger_error("Error : " . $archive->errorInfo(true));
		}
		// wgrywany do archiwum nowego Manifesta
		$v_list = $archive->add(PCLZIP_TEMPORARY_DIR . "MANIFEST.MF", PCLZIP_OPT_ADD_PATH, "META-INF", PCLZIP_OPT_REMOVE_PATH, MOBIKAR_TEMPORARY_DIR);
		if ($v_list == 0) {
			trigger_error("Error (archive->add): " . $archive->errorInfo(true), E_USER_ERROR);
		}
	}
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