<?php
include_once ('session_check.php');
include_once ('../scripts/myErrorHandler.php');

$wap_title= "mobiKAR - p&#x142;atno&#x15b;&#x107;";
include ("add_head.php");
?>
<h1>P&#x142;atno&#x15b;&#x107;</h1>
<div>
	&#x17b;etony s&#x105; jedynym &#x15b;rodkiem p&#x142;atniczym w mobiKAR.net. 
	<br/>
	Jeden &#x17c;eton upowa&#x17c;nia do wykupienia jednej piosenki.
	<br/>
	Sprzeda&#x17c; &#x17c;eton&#xf3;w realizowana jest przy wsp&#xf3;&#x142;pracy z serwisem AllPay.pl, 
	kt&#xf3;ry umo&#x17c;liwia p&#x142;atno&#x15b;&#x107; poprzez SMS, przelewy bankowe czy p&#x142;atno&#x15b;ci kart&#x105;.
	<br/>
	Po dokonaniu p&#x142;atno&#x15b;ci, AllPay.pl wysy&#x142;a specjalny kod, kt&#xf3;ry przeznaczony jest do zasilania konta &#x17c;etonami.
</div>
<h2>SMS Premium</h2>
<div>
	Aby zakupi&#x107; SMSem &#x17b;eton, wy&#x15b;lij SMSa o tre&#x15b;ci AP.MK na numer 75068. Koszt SMSa to 6,10 z&#x142;.
	<br/>
	Aby zakupi&#x107; SMSem trzy &#x17b;etony, wy&#x15b;lij SMSa o tre&#x15b;ci AP.MKP na numer 79068. Koszt SMSa to 10.98 z&#x142;.
</div>
<h2>Przelewy</h2>
<div>
	Aby zakupi&#x107; &#x17b;eton ta&#x144;sz&#x105; p&#x142;atno&#x15b;ci&#x105; przelewem (3,99 z&#x142;), wejd&#x17a; na szyfrowan&#x105; stron&#x119; 
	<a href="https://ssl.allpay.pl/?id=3595&amp;code=mk">AllPay.pl</a> i podaj swoje dane do transakcji.
	<br/>
	Analogicznie, chc&#x105;c wykupi&#x107; r&#xf3;wnie&#x17c; taniej trzy &#x17b;etony (6,99 z&#x142;) , wejd&#x17a; na szyfrowan&#x105; stron&#x119; 
	<a href="https://ssl.allpay.pl/?id=3595&amp;code=mkp">AllPay.pl</a> 
	w celu przeprowadzenia transakcji.
</div>
<h2>Przejd&#x17a; do:</h2>
<div>
	<a href='reg.php<?=$mySID?>'> Regulamin </a>
	<br/>
	<a href='start.php<?=$mySID?>'> Strona g&#x142;&#xf3;wna </a>
</div>

<?php
include ("add_foot.php");
?>