<?php
include_once ('../scripts/myErrorHandler.php');
include_once ('../scripts/Database.php');
include_once ('../scripts/utils.php');
// zamiast check_session u&#x17c;ywany lokalnie zmiennej
$mySID = null;
$code = null;
if (isset ($_GET['code']))
	$code = strtoupper($_GET['code']);
$pid = null;
$isAllowed = false;
$reason_text = null;
if ($code != null) {
	$db = new Database();
	// $pid - numer produktu
	$rowPayment = $db->getPayment($code);
	trigger_error("rowPayment: ".$rowPayment, E_USER_NOTICE);
	if ($rowPayment == null) {
		$reason = "<br/>Podany kod jest nieprawid&#x142;owy";
	} else {
		$id_user = $rowPayment['id_user'];
		if ($id_user != null && $id_user > 0){
			$reason = "<br/>Podany kod jest aktualny";
		}
		else{
			$mySID = "?kod=$code";
			$isAllowed = true;
		}
	}
	$db->destroy();
}
trigger_error("reason: ".$reason, E_USER_NOTICE);

$wap_title = "mobiKAR - Pobieranie";

include ("add_head.php");
?>
<?php if ($isAllowed == false){ ?>
<h1>Wprowad&#x17a; kod</h1>
	<div>
		<?php if ($reason != null) print("$reason<br/>"); ?>
		Aby pobra&#x107; aplikacj&#x105; wprowad&#x17a; zakupiony kod
	</div>
	<div style="text-align: center">
		<form title="Sprawdz" name="Sprawdz" action="getnow.php" method="get">
			<fieldset>
				Kod:  <input name="code" size="8" maxlength="8" type="text"/>
				<br/>
				<input name="submit" type="submit" value="Sprawdz"/>
			</fieldset>
		</form>
	</div>
<?php } elseif ($isAllowed == true){ ?>
		<h1>Pobieraj</h1>
		Dzi&#x119;kujemy za korzystanie z naszego serwisu. Przejd&#x17a;
		<br/>
		<a href="start.php<?=$mySID?>">dalej</a>
		<br/>
		by pobra&#x107; aplikacje mobiKAR z wybran&#x105; piosenk&#x105;
		<br/>
<?php } ?>
<h2>Przejd&#x17a; do:</h2>
<div>
	<a href='reg.php<?=$mySID?>'> Regulamin </a>
	<br/>
	<a href='index.php<?=$mySID?>'> Strona startowa </a>
</div>

<?php
include ("add_foot.php");
?>