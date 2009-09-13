<?php
include_once ('session_check.php');
include_once ('../scripts/myErrorHandler.php');

$wap_title= "mobiKAR - regulamin";
include ("add_head.php");
?>
<h1>Regulamin</h1>
<div>
	<b>Warunki korzystania z portalu mobiKAR.net</b>
	<br/>
	Portal s&#x142;u&#x17c;y do dystrybucji aplikacji mobiKAR jak i piosenek do aplikacji mobiKAR.
	<br/>
	Aby m&#xf3;c w pe&#x142;ni korzysta&#x107; z mobiKAR.net nale&#x17c;y za&#x142;o&#x17c;y&#x107; w&#x142;asne konto.
	<br/>
	Konto u&#x17c;ytkownika identyfikuje si&#x119; tylko identyfikatorem i has&#x142;em. Danych identyfikacyjnych nie wolno udost&#x119;pnia&#x107; innym osobom.
	<br/>
	Dystrybucja produkt&#xf3;w oparta jest na operacjach na zasobach punktowych.
	<br/>
	Konta u&#x17c;ytkownik&#xf3;w mobiKAR.net nape&#x142;niane s&#x105; punktami, kt&#xf3;re mo&#x17c;na kupi&#x107; lub zdoby&#x107; na zasadach promocyjnych opisanych w inny miejscu.
	<br/>
	W przypadku naruszenia przez u&#x17c;ytkownika powy&#x17c;szych warunk&#xf3;w korzystania z mobiKAR.net
	konto u&#x17c;ytkownika zostanie zablokowane.
</div>
<h2>Przejd&#x17a; do:</h2>
<div>
	<a href='help.php<?=$mySID?>'> Tematy pomocy </a>
	<br/>
	<a href='start.php<?=$mySID?>'> Strona g&#x142;&#xf3;wna </a>
</div>

<?php
include ("add_foot.php");
?>