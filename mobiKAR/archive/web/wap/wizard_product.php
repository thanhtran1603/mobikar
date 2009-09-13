<?php
include_once ('../scripts/myErrorHandler.php');
include_once ('../scripts/utf8ToEntities.php');
include_once ('../scripts/Database.php');
include_once ('../scripts/utils.php');

// TODO: dodac wyświetlanie tytly piosenki

$par_code = getParameter('code');


$isAllowed = FALSE;
$reason = NULL;

trigger_error("code:" . $par_code, E_USER_NOTICE);

$browser = getValFromItem($_SERVER['HTTP_USER_AGENT']);

$song_formats = array (
	"midi" => "MIDI"
);
$app_formats = array (
	"midp20" => "MIDP 2.0",
	"mmapi" => "MIDP 1.0 MMAPI",
		//"mot" => "MIDP 1.0 Motorola",
		//"sam" => "MIDP 1.0 Samsung",
		//"sie" => "MIDP 1.0 Siemens",


);

// sprawdzenie, czy katalog jest poprawny
//print("\nfile_exists(get/$par_code/song.midi\n");
if (file_exists("get/$par_code/song.midi") && file_exists("get/$par_code/song.midi.mlyr")) {
	// jedziemy dalej

	$isAllowed = TRUE;
}

$checked_song = "midi";
$checked_app = "mmapi";
if (strpos($browser, "Profile/MIDP-2") !== false)
	$checked_app = "midp20";
$wap_title = "mobiKAR - produkt";
include ("add_head.php");
?>
<?php if (!$isAllowed) { ?>
	<h1>Problem</h1>
	<div>
		<b>Brak dost&#x119;pu</b>
	</div>
<?php } else { ?>
	<h1>KARAOKE dla Ciebie<?=$id?></h1>
	<div>
	Aby pobra&#263; przygotowan&#261; piosenk&#281;, sprawd&#378; wersj&#281; aplikacji i pobierz
j&#261; na telefon wybieraj&#261;c przycisk "Pobierz".


	</div>
	<h2>Pobierz</h2>
	<div>
		Automatycznie dopasowana wersja dla Twojego telefonu
	</div>

	<form title="Pobierz" name="Pobierz" action="wizard_get.php" method="get">
		<fieldset>
			<input name="code" value="<?=$par_code?>" type="hidden"/>
			<b>Format piosenki</b>
			<br/>
<?php


foreach ($song_formats as $key => $val) {
	$checked = "";
	if (strcmp($checked_song, $key) == 0)
		$checked = 'checked="checked"';
	echo ("<label><input name=\"song\" type=\"radio\" value=\"$key\" $checked/>$val</label><br/>\n");
}
?>
			<b>Przeznaczenie aplikacji</b>
			<br/>
<?php


foreach ($app_formats as $key => $val) {
	$checked = "";
	if (strcmp($checked_app, $key) == 0)
		$checked = 'checked="checked"';
	echo ("<label><input name=\"app\" type=\"radio\" value=\"$key\" $checked/>$val</label><br/>\n");
}
?>
            <input name="Pobierz" type="submit" value="Pobierz"/>
		</fieldset>
	</form>
<?php } ?>
<h2>Przejd&#x17a; do:</h2>
<div>
	<a href="help.php?id=phones_midp20">Poczytaj o wersjach</a>
	<br/>
	<a href='index.php'> Strona g&#x142;&#xf3;wna </a>
</div>

<?php


include ("add_foot.php");
?>