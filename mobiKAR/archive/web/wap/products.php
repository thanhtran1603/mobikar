<?php
include_once ('session_check.php');
include_once ('../scripts/myErrorHandler.php');
include_once ('../scripts/utf8ToEntities.php');
include_once ('../scripts/Database.php');

$tid= NULL;
if (isset ($_GET['tid'])) {
	$tid= $_GET['tid'];
}
$cid= NULL;
$isHasProducts= FALSE;
$products=NULL;
$productName=null;
$categoryName=null;
if (isset ($_GET['cid'])) {
	$cid= $_GET['cid'];
	$db= new Database();
	if ($cid == -1)
		$categoryName="Nowo&#x15b;ci"; 
	else
		$categoryName= utf8ToEntities($db->getCategoryName($cid));
		
	$products= $db->getProducts($tid, $cid);
	if ($products != NULL) {
		$isHasProducts= TRUE;
		if ($tid == 2)
			$productName = "Single";
		else
			$productName = "Paczki";
	}
	$db->destroy();
}

$wap_title= "mobiKAR - kategorie";
include ("add_head.php");
?>
<h1><?=$productName?></h1>
<h2><?=$categoryName?></h2>
<ul>
	<?php
		foreach($products as $product ){
			$id = utf8ToEntities($product['id']);
			$title = utf8ToEntities($product['title']);
			$artist = utf8ToEntities($product['artist']);
			echo '<li><a href="product.php'.$mySID.'&amp;id='.$id.'">'.$id.'. '.$title.' - '.$artist.'</a></li>'."\n";
		}
	?>
</ul>
<h2>Przejd&#x17a; do:</h2>
<div>
	<a href="categories.php<?=$mySID?>&amp;tid=<?=$tid?>"> Kategorie </a>
	<br/>
	<a href='start.php<?=$mySID?>'> Strona startowa </a>
</div>
<?php
include ("add_foot.php");
?>