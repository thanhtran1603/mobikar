<?php
include_once ("../scripts/config.php");
class Base {
	var $dbLink;
	function Base() {
		$this->dbLink= mysql_connect(MOBIKAR_SERVER_HOST, MOBIKAR_SERVER_USER, MOBIKAR_SERVER_PASSWORD) or trigger_error('Could not connect: '.mysql_error(), E_USER_ERROR);
		mysql_select_db(MOBIKAR_SERVER_BASE, $this->dbLink) or trigger_error('Nie mozna wybrać bazy danych: '.mysql_error(), E_USER_ERROR);
		mysql_query("SET NAMES 'utf8'", $this->dbLink) or trigger_error('Query failed: '.mysql_error()."SQL: $query", E_USER_ERROR);
	}
	function destroy() {
		if ($this->dbLink != NULL)
			mysql_close($this->dbLink);
	}
	function getCategories() {
		trigger_error("getCategories()", E_USER_NOTICE);
		$ret= NULL;
		$query= 'SELECT DISTINCT dic_category.id, dic_category.name';
		$query .= ' FROM dic_category';
		$result= mysql_query($query, $this->dbLink) or trigger_error('Query failed: '.mysql_error()."SQL: $query", E_USER_ERROR);
		$i= 0;
		while ($row= mysql_fetch_assoc($result))
			$ret[$i ++]= $row;
		mysql_free_result($result);
		trigger_error("getCategories():".$ret, E_USER_NOTICE);
		return $ret;
	}
	function addSong($title, $artist, $music, $lyrics, $creator, $version, $score){
		trigger_error("addSong(".$title.', '.$artist.', '.$music.', '.$lyrics.', '.$creator.', '.$version.', '.$score.")", E_USER_NOTICE);
		$ret= NULL;
		$query = "INSERT INTO tab_song (title, artist, music, lyrics, creator, version, score)";
		$query .= " VALUES('".$title."','".$artist."','".$music."','".$lyrics."','".$creator."','".$version."',$score)";
		mysql_query($query, $this->dbLink) or trigger_error('Query failed: '.mysql_error()."SQL: $query", E_USER_ERROR);
		$ret= mysql_insert_id();
		return $ret;
	}
	function setSong($id_song, $title, $artist, $music, $lyrics, $creator, $version, $score){
		trigger_error("setSong(".$id_song.', '.$title.', '.$music.', '.$lyrics.', '.$creator.', '.$version.', '.$score.")", E_USER_NOTICE);
		$ret= NULL;
		$query = "UPDATE tab_song SET title='".$title."', artist='".$artist."', music='".$music."', lyrics='".$lyrics."', creator='".$creator."', version='".$version."', score=$score WHERE id=$id_song";
		trigger_error("query:".$query, E_USER_NOTICE);
		$ret = mysql_query($query, $this->dbLink) or trigger_error('Query failed: '.mysql_error()."SQL: $query", E_USER_ERROR);
		return $ret;
	}
	function addConSongCategory($id_song, $id_category){
		trigger_error("addConSongCategory(".$id_song.', '.$id_category.")", E_USER_NOTICE);
		$ret= NULL;
		$query = "INSERT INTO con_song_category (id_song, id_category)";
		$query .= " VALUES($id_song,$id_category)";
		$ret = mysql_query($query, $this->dbLink) or trigger_error('Query failed: '.mysql_error()."SQL: $query", E_USER_ERROR);
		return $ret;
	}
	function cleanConSongCategory($id_song){
		trigger_error("addConSongCategory(".$id_song.")", E_USER_NOTICE);
		$ret= NULL;
		$query = "DELETE FROM con_song_category WHERE id_song = $id_song";
		$ret = mysql_query($query, $this->dbLink) or trigger_error('Query failed: '.mysql_error()."SQL: $query", E_USER_ERROR);
		return $ret;
	}
	function getSong($id_song) {
		trigger_error("getSong(".$id_song.")", E_USER_NOTICE);
		$ret= NULL;
		$query= "SELECT * FROM tab_song WHERE id=$id_song";
		$result= mysql_query($query, $this->dbLink) or trigger_error('Query failed: '.mysql_error()."SQL: $query", E_USER_ERROR);
		if ($row= mysql_fetch_assoc($result))
			$ret= $row;
		mysql_free_result($result);
		trigger_error("getSong():".$ret, E_USER_NOTICE);
		return $ret;
	}
	function getSongCategories($id_song) {
		trigger_error("getSongCategories(".$id_song.")", E_USER_NOTICE);
		$ret= NULL;
		$query= "SELECT id_category FROM con_song_category WHERE id_song=$id_song";
		$result= mysql_query($query, $this->dbLink) or trigger_error('Query failed: '.mysql_error()."SQL: $query", E_USER_ERROR);
		$i= 0;
		while ($row= mysql_fetch_assoc($result))
			$ret[$i ++]= $row;
		mysql_free_result($result);
		trigger_error("getSongCategories():".$ret, E_USER_NOTICE);
		return $ret;
	}	
	function getSongs() {
		trigger_error("getSongs()", E_USER_NOTICE);
		$ret= NULL;
		$query= 'SELECT *';
		$query .= ' FROM tab_song';
		$result= mysql_query($query, $this->dbLink) or trigger_error('Query failed: '.mysql_error()."SQL: $query", E_USER_ERROR);
		$i= 0;
		while ($row= mysql_fetch_assoc($result))
			$ret[$i ++]= $row;
		mysql_free_result($result);
		trigger_error("getSongs():".$ret, E_USER_NOTICE);
		return $ret;
	}	
	function getProducts($productTypeId) {
		trigger_error("getProducts(".$productTypeId.")", E_USER_NOTICE);
		$ret= NULL;
		$query= 'SELECT * FROM tab_product WHERE id_producttype = '.$productTypeId;
		$result= mysql_query($query, $this->dbLink) or trigger_error('Query failed: '.mysql_error()."SQL: $query", E_USER_ERROR);
		$i= 0;
		while ($row= mysql_fetch_assoc($result))
			$ret[$i ++]= $row;
		mysql_free_result($result);
		trigger_error("getProducts():".$ret, E_USER_NOTICE);
		return $ret;
	}	
	function getContents($productId) {
		trigger_error("getContents(".$productId.")", E_USER_NOTICE);
		$ret= NULL;
		$query= 'SELECT DISTINCT tab_song.id, tab_song.title, tab_song.artist';
		$query.= ' FROM con_song_product, tab_song ';
		$query.= ' WHERE con_song_product.id_product = '.$productId;
		$query.= ' AND con_song_product.id_song = tab_song.id' ;
		$result= mysql_query($query, $this->dbLink) or trigger_error('Query failed: '.mysql_error()."SQL: $query", E_USER_ERROR);
		$i= 0;
		while ($row= mysql_fetch_assoc($result))
			$ret[$i ++]= $row;
		mysql_free_result($result);
		trigger_error("getContents():".$ret, E_USER_NOTICE);
		return $ret;
	}	
	function getProductSongs($id_product) {
		trigger_error("getProductSongs(".$id_product.")", E_USER_NOTICE);
		$ret= NULL;
		$query= "SELECT DISTINCT id_song AS id FROM con_song_product WHERE id_product=$id_product";
		$result= mysql_query($query, $this->dbLink) or trigger_error('Query failed: '.mysql_error()."SQL: $query", E_USER_ERROR);
		$i= 0;
		while ($row= mysql_fetch_assoc($result))
			$ret[$i ++]= $row;
		mysql_free_result($result);
		trigger_error("getProductSongs():".$ret, E_USER_NOTICE);
		return $ret;
	}
	function addProduct($id_producttype, $coins){
		trigger_error("addProduct(".$id_producttype.', '.$coins.")", E_USER_NOTICE);
		$ret= NULL;
		$query = "INSERT INTO tab_product (id_producttype, coins)";
		$query .= " VALUES($id_producttype, $coins)";
		mysql_query($query, $this->dbLink) or trigger_error('Query failed: '.mysql_error()."SQL: $query", E_USER_ERROR);
		$ret= mysql_insert_id();
		return $ret;
	}
	function setProduct($id_product, $id_producttype, $coins){
		trigger_error("setProduct(".$id_product.', '.$id_producttype.', '.$coins.")", E_USER_NOTICE);
		$ret= NULL;
		$query = "UPDATE tab_product SET id_producttype=$id_producttype, coins=$coins WHERE id=$id_product";
		$ret = mysql_query($query, $this->dbLink) or trigger_error('Query failed: '.mysql_error()."SQL: $query", E_USER_ERROR);
		return $ret;
	}	
	function getProduct($id_product) {
		trigger_error("getProduct(".$id_product.")", E_USER_NOTICE);
		$ret= NULL;
		$query= "SELECT * FROM tab_product WHERE id=$id_product";
		$result= mysql_query($query, $this->dbLink) or trigger_error('Query failed: '.mysql_error()."SQL: $query", E_USER_ERROR);
		if ($row= mysql_fetch_assoc($result))
			$ret= $row;
		mysql_free_result($result);
		trigger_error("getProduct():".$ret, E_USER_NOTICE);
		return $ret;
	}	
	function addConSongProduct($id_song, $id_product){
		trigger_error("addConSongProduct(".$id_song.', '.$id_product.")", E_USER_NOTICE);
		$ret= NULL;
		$query = "INSERT INTO con_song_product (id_song, id_product)";
		$query .= " VALUES($id_song,$id_product)";
		$ret = mysql_query($query, $this->dbLink) or trigger_error('Query failed: '.mysql_error()."SQL: $query", E_USER_ERROR);
		return $ret;
	}
	function cleanConSongProduct($id_product){
		trigger_error("cleanConSongProduct(".$id_product.")", E_USER_NOTICE);
		$ret= NULL;
		$query = "DELETE FROM con_song_product WHERE id_product = $id_product";
		$ret = mysql_query($query, $this->dbLink) or trigger_error('Query failed: '.mysql_error()."SQL: $query", E_USER_ERROR);
		return $ret;
	}
};
?>