<?php
function getParameter($par, $default = null) {
	return (isset ($_GET[$par]) ? $_GET[$par] : (isset ($_POST[$par]) ? $_POST[$par] : $default));
}
function getValFromItem($par, $default = null) {
	return (isset ($par) ? $par : $default);
}
function array_remove($array, $key) {
	$newArray = array ();
	foreach ($array as $key2 => $obj) {
		if ($key2 == $key)
			continue;
		array_push($newArray, $obj);
	}
	return $newArray;
}
function getCode(){
	$code = strtolower(substr(md5(rand()), 0, 8));
	if (ctype_digit(substr($code, 0, 1)) == true)
		return getCode();
	if (strpos($code, "0") !== false)
		return getCode();
	if (strpos($code, "o") !== false)
		return getCode();
	if (strpos($code, "1") !== false)
		return getCode();
	if (strpos($code, "l") !== false)
		return getCode();
	return $code;
};

function str_makerand ($minlength, $maxlength, $useupper, $usespecial, $usenumbers)
{
/*
Author: Peter Mugane Kionga-Kamau
http://www.pmkmedia.com

Description: string str_makerand(int $minlength, int $maxlength, bool $useupper, bool $usespecial, bool $usenumbers)
returns a randomly generated string of length between $minlength and $maxlength inclusively.

Notes:
- If $useupper is true uppercase characters will be used; if false they will be excluded.
- If $usespecial is true special characters will be used; if false they will be excluded.
- If $usenumbers is true numerical characters will be used; if false they will be excluded.
- If $minlength is equal to $maxlength a string of length $maxlength will be returned.
- Not all special characters are included since they could cause parse errors with queries.

Modify at will.
*/
    $charset = "abcdefghijklmnopqrstuvwxyz";
    if ($useupper)   $charset .= "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    if ($usenumbers) $charset .= "0123456789";
    if ($usespecial) $charset .= "~@#$%^*()_+-={}|][";   // Note: using all special characters this reads: "~!@#$%^&*()_+`-={}|\\]?[\":;'><,./";
    if ($minlength > $maxlength) $length = mt_rand ($maxlength, $minlength);
    else                         $length = mt_rand ($minlength, $maxlength);
    for ($i=0; $i<$length; $i++) $key .= $charset[(mt_rand(0,(strlen($charset)-1)))];
    return $key;
}

function packData(&$array, $pos1_24bit, $pos2_16bit, $pos3_16bit, $pos4_6bit, $pos5_1bit) {
/*
 * MSB - big endian
 *    24 bit time start      16 bit time long 16b sylIdx     6b   1
 * |<----------------------><---------------><-------------><---->x
 * |1111111111111111111111111111111111111111111111111111111111111110
 * +<------------------------------><------------------------------>
 *        $pack1                           $pack2
 */
	$arr64 = array ();
	for ($i = 0; $i < 64; $i++) {
		$arr64[$i] = 0;
	}
	//print_r($arr64);
	$arrIdx = 0;
	for ($i = 23; $i >= 0; $i--) {
		//print("1 << $i:" . (1 << $i) . " &: " . ((1 << $i) & $pos1_24bit) . "\n");
		if (((1 << $i) & $pos1_24bit) != 0) {
			$arr64[$arrIdx] = 1;
		}
		$arrIdx ++;
	}
	//print_r($arr64);
	for ($i = 15; $i >= 0; $i--) {
		if (((1 << $i) & $pos2_16bit) != 0) {
			$arr64[$arrIdx] = 1;
		}
		$arrIdx ++;
	}
	//print_r($arr64);
	for ($i = 15; $i >= 0; $i--) {
		if (((1 << $i) & $pos3_16bit) != 0) {
			$arr64[$arrIdx] = 1;
		}
		$arrIdx ++;
	}
	//print_r($arr64);
	for ($i = 5; $i >= 0; $i--) {
		if (((1 << $i) & $pos4_6bit) != 0) {
			$arr64[$arrIdx] = 1;
		}
		$arrIdx ++;
	}
	//print_r($arr64);
	for ($i = 0; $i >= 0; $i--) {
		if (((1 << $i) & $pos5_1bit) != 0) {
			$arr64[$arrIdx] = 1;
		}
		$arrIdx ++;
	}
	//print_r($arr64);
	// stworzenie dwóch licz 32 bit
	$pack1 = 0;
	$pack2 = 0;
	for ($i=0; $i<32; $i++){
		if ($arr64[$i] == 1){
			$pack1 |= 1 << (31 - $i);
		}
	}
	for ($i=32; $i<64; $i++){
		if ($arr64[$i] == 1){
			$pack2 |= 1 << (31 - $i-32);
		}
	}
	// dodanie do tablicy
	array_push($array, $pack1);
	array_push($array, $pack2);
}
function unpackData($pack1, $pack2) {
	$result = array ();
	$arr64 = array ();
	for ($i = 0; $i < 64; $i++) {
		$arr64[$i] = 0;
	}
	//print_r($arr64);
	$arrIdx = 0;
//	print ("UNPACK dodawanie pack1 " . $pack1 . "\n");
	for ($i = 31; $i >= 0; $i--) {
//		print ("1 << $i:" . (1 << $i) . " &: " . ((1 << $i) & $pack1) . "\n");
		if (((1 << $i) & $pack1) != 0) {
			$arr64[$arrIdx] = 1;
		}
		$arrIdx++;
	}
	//print_r($arr64);
//	print ("UNPACK dodawanie pack2 " . $pack2 . "\n");
	for ($i = 31; $i >= 0; $i--) {
		if (((1 << $i) & $pack2) != 0) {
			$arr64[$arrIdx] = 1;
		}
		$arrIdx++;
	}
	//print_r($arr64);

	$arrIdx = 0;
	$pos1_24bit = 0;
	for ($i = 23; $i>=0; $i--) {
		if ($arr64[$arrIdx] == 1) {
			$pos1_24bit |= (1 << $i);
		}
		$arrIdx++;
	}
	//printf("pos1_24bit %d [%X]\n" , $pos1_24bit,  $pos1_24bit);
	$pos2_16bit = 0;
	for ($i = 15; $i>=0; $i--) {
		if ($arr64[$arrIdx] == 1) {
			$pos2_16bit |= (1 << $i);
		}
		$arrIdx++;
	}
	//printf("pos2_16bit %d [%X]\n" , $pos2_16bit,  $pos2_16bit);

	$pos3_16bit = 0;
	for ($i = 15; $i>=0; $i--) {
		if ($arr64[$arrIdx] == 1) {
			$pos3_16bit |= (1 << $i);
		}
		$arrIdx++;
	}
	//printf("pos3_16bit %d [%X]\n" , $pos3_16bit,  $pos3_16bit);

	$pos4_6bit = 0;
	for ($i = 5; $i>=0; $i--) {
		if ($arr64[$arrIdx] == 1) {
			$pos4_6bit |= (1 << $i);
		}
		$arrIdx++;
	}
	//printf("pos4_6bit %d [%X]\n" , $pos4_6bit,  $pos4_6bit);

	$pos5_1bit = 0;
	for ($i = 0; $i>=0; $i--) {
		if ($arr64[$arrIdx] == 1) {
			$pos5_1bit |= (1 << $i);
		}
		$arrIdx++;
	}
	//printf("pos5_1bit %d [%X]\n" , $pos5_1bit,  $pos5_1bit);

	$result['pos1_24bit'] = $pos1_24bit;
	$result['pos2_16bit'] = $pos2_16bit;
	$result['pos3_16bit'] = $pos3_16bit;
	$result['pos4_6bit'] = $pos4_6bit;
	$result['pos5_1bit'] = $pos5_1bit;

//	print_r($result);
	return $result;
}

?>
