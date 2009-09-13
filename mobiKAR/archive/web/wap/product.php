<?php
include_once ('session_check.php');
include_once ('../scripts/myErrorHandler.php');
include_once ('../scripts/utf8ToEntities.php');
include_once ('../scripts/Database.php');
include_once ('../scripts/utils.php');

$isAllowed= FALSE;
$reason= NULL;
$id= NULL;
if (isset ($_GET['id'])) {
	$id= $_GET['id'];
}
trigger_error("uid:".$uid.", id:".$id, E_USER_NOTICE);
$rowUser= NULL;
$product= NULL;
$browser= NULL;
$koszt=0;
if (isset ($_SERVER['HTTP_USER_AGENT']))
	$browser= $_SERVER['HTTP_USER_AGENT'];
if ($uid == null && $kod != null) {
	// ustawiamy user o kodzie 1
	$uid = 1;
}
$product= null;
if ($uid != null) {
	$db= new Database();
	$rowUser= $db->getUser(NULL, $uid);
	if ($rowUser == NULL) {
		$reason= "BAD_USER";
	}
	else {
		$product= $db->getProduct($id);
		if ($product == NULL) {
			$reason= "BAD_PRODUCT";
		}
		else {
			$isAllowed= TRUE;
			// czy produkt był zamawiany przez klienta?
			$rowOrder = $db->getOrder($uid, $id);
			if ($uid == 1 || $rowOrder == NULL)
				$koszt = $product['product']['coins'];
		}
	}
	$db->destroy();
}
$song_formats = array("midi" => "MIDI");

$songs= getValFromItem($product['songs']);
// robie zalozenie, ze sa piosnki we wszytkich formatach
$isHasMidi15k = TRUE;
$isHasMp3 = TRUE;
foreach ($songs as $song) {
	$songId= getValFromItem($song['id']);
	//print("[$songId]".MOBIKAR_PRODUCTS_DIR."songs/$songId.15k.midi");
	if ( ! file_exists(MOBIKAR_PRODUCTS_DIR."songs/$songId.15k.midi")) {
		$isHasMidi15k = FALSE;
	}
	if ( ! file_exists(MOBIKAR_PRODUCTS_DIR."songs/$songId.mp3")) {
		$isHasMp3 = FALSE;
	}
}
if ($isHasMidi15k == TRUE){
	$song_formats["15k.midi"] = "MIDI 15k";
}
if ($isHasMp3 == TRUE){
	$song_formats["mp3"] = "MP3";
}
$app_formats = array(
	"midp20" => "MIDP 2.0",
	"mmapi" => "MIDP 1.0 MMAPI",
//	"mot" => "MIDP 1.0 Motorola",
//	"sam" => "MIDP 1.0 Samsung",
	"sie" => "MIDP 1.0 Siemens",
	);
$checked_song = "midi";
$checked_app = "mmapi";
if (strpos($browser, "Profile/MIDP-2") !== false)
	$checked_app = "midp20";
$wap_title= "mobiKAR - produkt";
include ("add_head.php");
?>
<?php if (!$isAllowed) { ?>
	<h1>Problem</h1>
	<div>
		<b>Brak dost&#x119;pu</b>
	</div>
<?php } else { ?>
	<h1>Produkt o kodzie <?=$id?></h1>
	<div>
		Koszt w &#x17c;etonach: <?=$koszt?>.
	</div>
	<h2>Zawarto&#x15b;&#x107;:</h2>
	<ul>
	<?php
		foreach ($product['songs'] as $song) {
			$title= utf8ToEntities($song['title']);
			$artist= utf8ToEntities($song['artist']);
			echo("<li>$title - $artist</li>\n");
		}
	?>
	</ul>
	<h2>Pobierz</h2>
	<div>
		Automatycznie dopasowana wersja dla Twojego telefonu
	</div>

	<form title="Pobierz" name="Pobierz" action="get.php" method="get">
		<fieldset>
			<input name="uid" value="<?=$uid?>" type="hidden"/>
			<input name="sid" value="<?=$sid?>" type="hidden"/>
			<input name="kod" value="<?=$kod?>" type="hidden"/>
			<input name="id" value="<?=$id?>" type="hidden"/>
			<b>Format piosenki</b>
			<br/>
<?php
			foreach ($song_formats as $key => $val) {
				$checked = "";
				if (strcmp($checked_song, $key) == 0)
					$checked = 'checked="checked"';
				echo("<label><input name=\"song\" type=\"radio\" value=\"$key\" $checked/>$val</label><br/>\n");
			}
?>
			<b>Przeznaczenie aplikacji</b>
			<br/>
<?php
			foreach ($app_formats as $key => $val) {
				$checked = "";
				if (strcmp($checked_app, $key) == 0)
					$checked = 'checked="checked"';
				echo("<label><input name=\"app\" type=\"radio\" value=\"$key\" $checked/>$val</label><br/>\n");
			}
?>
            <input name="Pobierz" type="submit" value="Pobierz"/>
		</fieldset>
	</form>
<?php
/*

	<ul>
		<?php if (strpos($browser, "Profile/MIDP-2") !== false){ ?>
			<li><a href="get.php<?=$mySID?>&amp;id=<?=$id?>&amp;ver=midp20"> Pobierz wersj&#x119; MIDP 2.0 </a></li>
		<?php } else { ?>
			<li><a href="get.php<?=$mySID?>&amp;id=<?=$id?>&amp;ver=mmapi"> Pobierz wersj&#x119; MIDP 1.0 </a></li>
		<?php }?>

	</ul>
	<div>
		Poni&#x17c;ej lista wersji aplikacji wraz z list&#x105; telefon&#xf3;w na nie
	</div>
	<ul>
		<li><a href="get.php<?=$mySID?>&amp;id=<?=$id?>&amp;ver=midp20"> MIDP 2.0 </a>&#160; - &#160;<a href="help.php<?=$mySID?>&amp;id=phones_midp20">Sprawd&#x17a; telefony</a></li>
		<li><a href="get.php<?=$mySID?>&amp;id=<?=$id?>&amp;ver=mmapi"> MIDP 1.0 </a>&#160; - &#160;<a href="help.php<?=$mySID?>&amp;id=phones_mmapi">Sprawd&#x17a; telefony </a></li>
	</ul>
*/

?>
<?php } ?>
<h2>Przejd&#x17a; do:</h2>
<div>
	<a href='start.php<?=$mySID?>'> Strona g&#x142;&#xf3;wna </a>
</div>

<?php
include ("add_foot.php");
?>