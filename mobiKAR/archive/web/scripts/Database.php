<?php
include_once ('config.php');
include_once ('utils.php');
class Database {
	var $dbLink;
	function Database() {
		$this->dbLink = mysql_connect(MOBIKAR_SERVER_HOST, MOBIKAR_SERVER_USER, MOBIKAR_SERVER_PASSWORD) or trigger_error('Could not connect: ' . mysql_error(), E_USER_ERROR);
		mysql_select_db(MOBIKAR_SERVER_BASE, $this->dbLink) or trigger_error('Nie mozna wybrać bazy danych: ' . mysql_error(), E_USER_ERROR);
		mysql_query("SET NAMES 'utf8'", $this->dbLink) or trigger_error('Query failed: ' . mysql_error() . "SQL: SET NAMES 'utf8'", E_USER_ERROR);
		// nie znam się na tych referencjach
		mysql_query("SET FOREIGN_KEY_CHECKS=0", $this->dbLink) or trigger_error('Query failed: ' . mysql_error() . "SQL: SET FOREIGN_KEY_CHECKS=0", E_USER_ERROR);

	}
	function destroy() {
		if ($this->dbLink != NULL)
			mysql_close($this->dbLink);
	}
	function addCodeToPayment($code, $idPaymentType, $idProvider) {
		$code = strtoupper($code);
		$query = "INSERT INTO tab_payment (code, id_paymenttype, id_provider)";
		$query .= " VALUES('$code', $idPaymentType, $idProvider)";
		$result = mysql_query($query, $this->dbLink) or trigger_error('Query failed: ' . mysql_error() . "SQL: $query", E_USER_ERROR);
	}
	function addUser($login, $password) {
		trigger_error("addUser($login, $password)", E_USER_NOTICE);
		$ret = NULL;
		$code = getCode();
		// TODO: sprawdzic, czy kod nie powtarza sie
		$query = "INSERT INTO tab_user (login,password)";
		$query .= " VALUES('" . $login . "', '" . $password . "')";
		mysql_query($query, $this->dbLink) or trigger_error('Query failed: ' . mysql_error() . "SQL: $query", E_USER_ERROR);
		$ret = $this->getUser($login, null);
		return $ret;
	}
	function getUser($login, $userId) {
		trigger_error("getUser(" . $login . ", " . $userId . ")", E_USER_NOTICE);
		$ret = NULL;
		$query = 'SELECT id,login,password,0 AS coins,added,counter,recently,sid,browser,secure,';
		$query .= ' UNIX_TIMESTAMP(CURRENT_TIMESTAMP()) - UNIX_TIMESTAMP(recently) AS delta';
		if ($userId != NULL)
			$query .= " FROM tab_user WHERE id=" . $userId;
		else
			$query .= " FROM tab_user WHERE login='" . $login . "'";
		trigger_error("query:" . $query, E_USER_NOTICE);
		$result = mysql_query($query, $this->dbLink) or trigger_error('Query failed: ' . mysql_error() . "SQL: $query", E_USER_ERROR);
		if ($result != FALSE) {
			if ($row = mysql_fetch_assoc($result)) {
				if ($userId == null)
					$userId = (isset ($row['id']) ? $row['id'] : 0);
				$ret = $row;
			}
			mysql_free_result($result);
		}
		if ($userId != null)
			$ret['coins'] = $this->getUserCoins($userId);
		trigger_error("getUser():ret[coins]: ". $ret['coins'], E_USER_NOTICE);
		trigger_error("getUser():$ret", E_USER_NOTICE);
		return $ret;
	}
	function getUserCoins($userId) {
		trigger_error("getUserCoins(" . $userId . ")", E_USER_NOTICE);
		$ret = NULL;
		$query = "SELECT SUM(tab_paymenttype.coins) - subquery.used as coins";
		$query .= " FROM tab_paymenttype, tab_payment, ";
		$query .= " ( SELECT SUM(tab_product.coins) AS used ";
		$query .= "  FROM tab_product, tab_order ";
		$query .= "  WHERE tab_order.id_user = " . $userId;
		$query .= "  AND tab_order.id_product = tab_product.id";
		$query .= " ) AS subquery";
		$query .= " WHERE tab_payment.id_user = " . $userId;
		$query .= " AND tab_payment.id_paymenttype = tab_paymenttype.id";
		trigger_error("query:" . $query, E_USER_NOTICE);
		$result = mysql_query($query, $this->dbLink) or trigger_error('Query failed: ' . mysql_error() . "SQL: $query", E_USER_ERROR);
		if ($result != FALSE) {
			if ($row = mysql_fetch_assoc($result))
				$ret = (isset ($row['coins']) ? $row['coins'] : 0);
			mysql_free_result($result);
		}
		trigger_error("getUserCoins($userId):" . $ret, E_USER_NOTICE);
		return $ret;
	}
	function updateUserSid($id, $sid) {
		$ret = NULL;
		$browser = NULL;
		if (isset ($_SERVER['HTTP_USER_AGENT']))
			$browser = $_SERVER['HTTP_USER_AGENT'];
		$query = "UPDATE `tab_user` SET `recently` = NOW( ) ,`sid` = '$sid', ";
		$query .= " `browser` = '$browser', counter = counter+1 WHERE `id` = $id";
		$ret = mysql_query($query, $this->dbLink) or trigger_error('Query failed: ' . mysql_error() . "SQL: $query", E_USER_ERROR);
		return $ret;
	}
	/*
	 * użytkownik o id = 1 jest specjalnym użytkownikiem
	 * i określa on wsdzystkie pobrania niezarejestrowanych 
	 */
	function updateUserAccount($id, $code) {
		trigger_error("updateUserAccount(" . $id . ", " . $code . ")", E_USER_NOTICE);
		$ret = FALSE;
		$query = "SELECT tab_payment.id AS id";
		$query .= " FROM tab_payment, tab_paymenttype";
		$query .= " WHERE tab_payment.code = '$code'";
		$query .= " AND tab_payment.id_user IS NULL";
		$query .= " AND tab_payment.id_paymenttype = tab_paymenttype.id";
		trigger_error("query:" . $query, E_USER_NOTICE);
		$result = mysql_query($query, $this->dbLink) or trigger_error('Query failed: ' . mysql_error() . "SQL: $query", E_USER_ERROR);
		if ($result != FALSE) {
			if ($row = mysql_fetch_assoc($result)) {
				$paymentId = $row['id'];
				mysql_free_result($result);
				$query = "UPDATE `tab_payment` SET id_user = $id, activated = NOW() WHERE `id` = $paymentId";
				mysql_query($query, $this->dbLink) or trigger_error('Query failed: ' . mysql_error() . "SQL: $query", E_USER_ERROR);
				$ret = $paymentId;
			}
		}
		trigger_error("updateUserAccount():" . $ret, E_USER_NOTICE);
		return $ret;
	}
	function getCategoryName($categoryId) {
		trigger_error("getCategoryName($categoryId)", E_USER_NOTICE);
		$query = "SELECT name";
		$query .= " FROM dic_category WHERE id=" . $categoryId . "";
		$result = mysql_query($query, $this->dbLink) or trigger_error('Query failed: ' . mysql_error() . "SQL: $query", E_USER_ERROR);
		if ($result != FALSE) {
			if ($row = mysql_fetch_assoc($result)) {
				$ret = $row['name'];
			}
		}
		trigger_error("getCategoryName():$ret", E_USER_NOTICE);
		return $ret;
	}
	function getCategories($productTypeId) {
		trigger_error("getCategories(" . $productTypeId . ")", E_USER_NOTICE);
		$ret = NULL;
		$query = 'SELECT DISTINCT dic_category.id, dic_category.name';
		$query .= ' FROM dic_category, tab_product, con_song_category, con_song_product';
		$query .= ' WHERE tab_product.id_producttype = ' . $productTypeId;
		$query .= ' AND tab_product.id = con_song_product.id_product';
		$query .= ' AND con_song_product.id_song = con_song_category.id_song LIMIT 0, 30';
		$result = mysql_query($query, $this->dbLink) or trigger_error('Query failed: ' . mysql_error() . "SQL: $query", E_USER_ERROR);
		$i = 0;
		while ($row = mysql_fetch_assoc($result))
			$ret[$i++] = $row;
		mysql_free_result($result);
		trigger_error("getCategories():" . $ret, E_USER_NOTICE);
		return $ret;
	}
	function getProducts($productTypeId, $categoryId, $count = 30) {
		$ret = NULL;
		$query = 'SELECT DISTINCT tab_product.id, tab_song.title, tab_song.artist, tab_song.music, tab_song.lyrics';
		$query .= ' FROM tab_song, tab_product, con_song_product, con_song_category';
		$query .= ' WHERE con_song_category.id_song = tab_song.id';
		if ($categoryId != -1)
			$query .= ' AND con_song_category.id_category = ' . $categoryId;
		$query .= ' AND tab_product.id_producttype = ' . $productTypeId;
		$query .= ' AND tab_product.id = con_song_product.id_product';
		$query .= ' AND con_song_product.id_song = con_song_category.id_song ';
		$query .= ' ORDER BY tab_product.id DESC LIMIT 0, ' . $count;
		$result = mysql_query($query, $this->dbLink) or trigger_error('Query failed: ' . mysql_error() . "SQL: $query", E_USER_ERROR);
		$i = 0;
		while ($row = mysql_fetch_assoc($result))
			$ret[$i++] = $row;
		mysql_free_result($result);
		trigger_error("getCategories():" . $ret, E_USER_NOTICE);
		return $ret;
	}
	function getProduct($productId) {
		trigger_error("getProduct(" . $productId . ")", E_USER_NOTICE);
		$ret = NULL;
		$product = NULL;
		$query = 'SELECT id_producttype,coins';
		$query .= " FROM tab_product WHERE id=" . $productId;
		$result = mysql_query($query, $this->dbLink) or trigger_error('Query failed: ' . mysql_error() . "SQL: $query", E_USER_ERROR);
		if ($result != FALSE) {
			if ($row = mysql_fetch_assoc($result)) {
				$product = $row;
			}
			mysql_free_result($result);
		}
		$songs = NULL;
		if ($product != NULL) {
			$query = 'SELECT DISTINCT tab_song.id, tab_song.title, tab_song.artist, tab_song.music, tab_song.lyrics';
			$query .= ' FROM tab_song, con_song_product';
			$query .= ' WHERE con_song_product.id_product = ' . $productId;
			$query .= ' AND con_song_product.id_song = tab_song.id';
			$result = mysql_query($query, $this->dbLink) or trigger_error('Query failed: ' . mysql_error() . "SQL: $query", E_USER_ERROR);
			$i = 0;
			while ($row = mysql_fetch_assoc($result))
				$songs[$i++] = $row;
			mysql_free_result($result);
			$ret['product'] = $product;
			$ret['songs'] = $songs;
			trigger_error("getProduct():" . $ret, E_USER_NOTICE);
		}
		return $ret;
	}
	/*
	 * addOrder ma dodać zamówienie a wcześniej ma znaleźć odpowiednią płatność,
	 * płatność na jeden produkt nie jest składana, tzn, że zamówienie na produkt 
	 * może wiązać się tylko z jedną płatnością
	*/
	function addOrder($uid, $productId, $paymentId = -1) {
		trigger_error("addOrder(" . $uid . ', ' . $productId . ', ' . $paymentId . ")", E_USER_NOTICE);
		$ret = NULL;
		$code = getCode();
		// TODO: sprawdzic, czy kod nie powtarza sie
		// ile jest potrza żetonów?
		$coins = 0;
		$query = "SELECT coins";
		$query .= " FROM tab_product WHERE id=" . $productId;
		trigger_error("SQL: $query", E_USER_NOTICE);
		$result = mysql_query($query, $this->dbLink) or trigger_error('Query failed: ' . mysql_error() . "SQL: $query", E_USER_ERROR);
		if ($row = mysql_fetch_assoc($result)) {
			$coins = (isset ($row['coins']) ? $row['coins'] : 0);
		}
		mysql_free_result($result);
		if ($coins == 0) {
			trigger_error("addOrder(" . $uid . ', ' . $productId . ', ' . $paymentId . ") coins == 0", E_USER_NOTICE);
		}
		// czy podany payment jest wystarczający do pobrania produktu
		if ($paymentId != -1) {
			$query = "SELECT tab_paymenttype.coins - tab_payment.used_coins AS coins";
			$query .= " FROM tab_paymenttype, tab_payment";
			$query .= " FROM tab_product WHERE id=" . $productId;
			$query .= " AND tab_payment.id_paymenttype = tab_paymenttype.id";
			trigger_error("SQL: $query", E_USER_NOTICE);
			$result = mysql_query($query, $this->dbLink) or trigger_error('Query failed: ' . mysql_error() . "SQL: $query", E_USER_ERROR);
			if ($row = mysql_fetch_assoc($result)) {
				$coins_row = (isset ($row['coins']) ? $row['coins'] : 0);
				if ($coins_row < $coins)
					$paymentId = -1;
			}
			mysql_free_result($result);
		}
		if ($paymentId == -1) {
			$paymentId = $this->getPaymentId($uid, $coins);
		}
		if ($paymentId != -1) {
			// aktualizacja płatności
			$query = "UPDATE tab_payment";
			$query .= " SET used_coins = used_coins + " . $coins;
			$query .= " WHERE tab_payment.id = " . $paymentId;
			trigger_error("SQL: $query", E_USER_NOTICE);
			$result = mysql_query($query, $this->dbLink) or trigger_error('Query failed: ' . mysql_error() . "SQL: $query", E_USER_ERROR);
		}
		if ($paymentId != -1 || $coins == 0) {
			// Należy dodać zamówienie temu co ma płatność na produkt
			// a także temu co jej nie ma bo nie musi conis == 0			
			// dodanie zamówienia
			$query = "INSERT INTO tab_order (id_user, id_payment, id_product, code)";
			$query .= " VALUES($uid, $paymentId, $productId, '$code')";
			trigger_error("SQL: $query", E_USER_NOTICE);
			mysql_query($query, $this->dbLink) or trigger_error('Query failed: ' . mysql_error() . "SQL: $query", E_USER_ERROR);
			$ret = $code;
		}
		trigger_error("addOrder()" . $ret , E_USER_NOTICE);
		return $ret;
	}
	function getOrder($uid, $productId) {
		trigger_error("getOrder(" . $uid . ', ' . $productId . ")", E_USER_NOTICE);
		$ret = NULL;
		// TODO: sprawdzic, czy kod nie powtarza sie
		$query = "SELECT  id, id_user, id_product, code, added";
		$query .= " FROM tab_order WHERE id_user=$uid AND id_product=$productId";
		$result = mysql_query($query, $this->dbLink) or trigger_error('Query failed: ' . mysql_error() . "SQL: $query", E_USER_ERROR);
		if ($result != FALSE) {
			if ($row = mysql_fetch_assoc($result)) {
				$ret = $row;
			}
		}
		trigger_error("getOrder():" . $ret, E_USER_NOTICE);
		return $ret;
	}
	function addDownload($uid, $productId) {
		trigger_error("addDownload(" . $uid . ', ' . $productId . ")", E_USER_NOTICE);
		$ret = NULL;
		$browser = NULL;
		if (isset ($_SERVER['HTTP_USER_AGENT']))
			$browser = $_SERVER['HTTP_USER_AGENT'];
		$query = "INSERT INTO tab_download (id_user, id_product, browser)";
		$query .= " VALUES($uid, $productId, '$browser')";
		$ret = mysql_query($query, $this->dbLink) or trigger_error('Query failed: ' . mysql_error() . "SQL: $query", E_USER_ERROR);
		return $ret;
	}
	function getProvider($providerId) {
		trigger_error("getProvider(" . $providerId . ")", E_USER_NOTICE);
		$ret = NULL;
		$query = "SELECT added,name,password,outsource";
		$query .= " FROM tab_provider WHERE id=$providerId";
		$result = mysql_query($query, $this->dbLink) or trigger_error('Query failed: ' . mysql_error() . "SQL: $query", E_USER_ERROR);
		if ($result != FALSE) {
			if ($row = mysql_fetch_assoc($result)) {
				$ret = $row;
			}
		}
		trigger_error("getProvider():" . $ret, E_USER_NOTICE);
		return $ret;
	}
	function getPayment($code) {
		trigger_error("getPayment(" . $code . ")", E_USER_NOTICE);
		$ret = NULL;
		$query = "SELECT *";
		$query .= " FROM tab_payment WHERE code='" . $code . "'";
		$result = mysql_query($query, $this->dbLink) or trigger_error('Query failed: ' . mysql_error() . "SQL: $query", E_USER_ERROR);
		if ($result != FALSE) {
			if ($row = mysql_fetch_assoc($result)) {
				$ret = $row;
			}
		}
		trigger_error("getPayment():" . $ret, E_USER_NOTICE);
		return $ret;
	}
	function getPaymentId($userId, $coins) {
		trigger_error("getPayment(" . $userId . ", " . $coins . ")", E_USER_NOTICE);
		$ret = -1;
		$query = "SELECT tab_payment.id";
		$query .= " FROM tab_payment, tab_paymenttype";
		$query .= " WHERE tab_payment.id_user=" . $userId;
		$query .= " AND (tab_paymenttype.coins - tab_payment.used_coins) >=" . $coins;
		$query .= " AND tab_payment.id_paymenttype = tab_paymenttype.id";
		$query .= " ORDER BY tab_paymenttype.coins, tab_payment.activated DESC";
		$result = mysql_query($query, $this->dbLink) or trigger_error('Query failed: ' . mysql_error() . "SQL: $query", E_USER_ERROR);
		if ($result != FALSE) {
			if ($row = mysql_fetch_assoc($result)) {
				$ret = (isset ($row['id']) ? $row['id'] : -1);
			}
		}
		trigger_error("getPaymentId():" . $ret, E_USER_NOTICE);
		return $ret;
	}
	function getInvited($guestId) {
		trigger_error("getInvited(" . $guestId . ")", E_USER_NOTICE);
		$ret = NULL;
		$query = "SELECT *";
		$query .= " FROM tab_invited WHERE id_user_guest =" . $guestId;
		$result = mysql_query($query, $this->dbLink) or trigger_error('Query failed: ' . mysql_error() . "SQL: $query", E_USER_ERROR);
		if ($result != FALSE) {
			if ($row = mysql_fetch_assoc($result)) {
				$ret = $row;
			}
		}
		trigger_error("getInvited():" . $ret, E_USER_NOTICE);
		return $ret;
	}
	function addInvited($guestId, $hostId) {
		trigger_error("addInvited(" . $guestId . ', ' . $hostId . ")", E_USER_NOTICE);
		$ret = NULL;
		$query = "INSERT INTO tab_invited (id_user_guest, id_user_host)";
		$query .= " VALUES($guestId, $hostId)";
		$ret = mysql_query($query, $this->dbLink) or trigger_error('Query failed: ' . mysql_error() . "SQL: $query", E_USER_ERROR);
		return $ret;
	}
	function getLoginsSongs($login) {
		trigger_error("getUsersSongs(" . $login . ")", E_USER_NOTICE);
		$ret = NULL;
		$query = "SELECT tab_song.id, title, artist ";
		$query .= " FROM tab_song, tab_order, con_song_product, tab_user";
		$query .= " WHERE tab_user.login='" . $login . "'";
		$query .= " AND tab_order.id_user=tab_user.id";
		$query .= " AND tab_order.id_product = con_song_product.id_product AND con_song_product.id_song=tab_song.id";
		//trigger_error("SQL: $query", E_USER_NOTICE);
		$result = mysql_query($query, $this->dbLink) or trigger_error('Query failed: ' . mysql_error() . "SQL: $query", E_USER_ERROR);
		$i = 0;
		while ($row = mysql_fetch_assoc($result))
			$ret[$i++] = $row;
		mysql_free_result($result);
		trigger_error("getUsersSongs():" . $ret, E_USER_NOTICE);
		return $ret;
	}
	function getSong($songId) {
		trigger_error("getSong(" . $songId . ")", E_USER_NOTICE);
		$ret = NULL;
		$query = "SELECT tab_song.id, title, artist, music, lyrics ";
		$query .= " FROM tab_song";
		$query .= " WHERE tab_song.id=" . $songId . "";
		//trigger_error("SQL: $query", E_USER_NOTICE);
		$result = mysql_query($query, $this->dbLink) or trigger_error('Query failed: ' . mysql_error() . "SQL: $query", E_USER_ERROR);
		if ($result != FALSE) {
			if ($row = mysql_fetch_assoc($result)) {
				$ret = $row;
			}
		}
		mysql_free_result($result);
		trigger_error("getSong():" . $ret, E_USER_NOTICE);
		return $ret;
	}
	function getPartner($login, $partnerId) {
		trigger_error("getPartner(" . $login . ", " . $partnerId . ")", E_USER_NOTICE);
		$ret = NULL;
		$query = "SELECT * FROM tab_partner ";
		if ($partnerId != NULL)
			$query .= "  WHERE id=" . $partnerId;
		else
			$query .= "  WHERE login='" . $login . "'";
		$result = mysql_query($query, $this->dbLink) or trigger_error('Query failed: ' . mysql_error() . "SQL: $query", E_USER_ERROR);
		if ($result != FALSE) {
			if ($row = mysql_fetch_assoc($result))
				$ret = $row;
			mysql_free_result($result);
		}
		return $ret;
	}
	function getPartnerSoldCoins($partnerId) {
		trigger_error("getPartnerSoldCoins(" . $partnerId . ")", E_USER_NOTICE);
		$ret = NULL;
		$query = "SELECT tab_paymenttype.id, tab_paymenttype.name, tab_paymenttype.cost, COUNT(tab_payment.id) as sold";
		$query .= " FROM tab_payment, tab_paymenttype ";
		$query .= " WHERE tab_payment.id_partner=" . $partnerId;
		$query .= " AND tab_payment.id_paymenttype = tab_paymenttype.id";
		$query .= " AND tab_payment.id_user > 0";
		$query .= " GROUP BY tab_paymenttype.id";
		trigger_error("A3", E_USER_NOTICE);
		trigger_error("SQL: $query", E_USER_NOTICE);
		$result = mysql_query($query, $this->dbLink) or trigger_error('Query failed: ' . mysql_error() . "SQL: $query", E_USER_ERROR);
		$i = 0;
		while ($row = mysql_fetch_assoc($result))
			$ret[$i++] = $row;
		mysql_free_result($result);
		trigger_error("getPartnerSoldCoins():" . $ret, E_USER_NOTICE);
		return $ret;
	}
	function getPartnerSoldSongs($partnerId) {
		trigger_error("getPartnerSoldSongs(" . $partnerId . ")", E_USER_NOTICE);
		$ret = NULL;
		$query = "SELECT tab_paymenttype.id AS id, tab_paymenttype.name AS name, tab_paymenttype.cost AS cost, COUNT(tab_paymenttype.id) as sold, tab_paymenttype.coins AS coins  ";
		$query .= " FROM tab_payment, tab_paymenttype , tab_song, con_song_product, tab_product, tab_order ";
		$query .= " WHERE tab_song.id_partner=" . $partnerId;
		$query .= " AND con_song_product.id_song = tab_song.id";
		$query .= " AND con_song_product.id_product = tab_product.id";
		$query .= " AND tab_order.id_product = tab_product.id";
		$query .= " AND tab_order.id_payment = tab_payment.id";
		$query .= " AND tab_payment.id_paymenttype = tab_paymenttype.id";
		$query .= " GROUP BY tab_paymenttype.id";
		trigger_error("SQL: $query", E_USER_NOTICE);
		$result = mysql_query($query, $this->dbLink) or trigger_error('Query failed: ' . mysql_error() . "SQL: $query", E_USER_ERROR);
		$i = 0;
		while ($row = mysql_fetch_assoc($result))
			$ret[$i++] = $row;
		mysql_free_result($result);
		trigger_error("getPartnerSoldSongs():" . $ret, E_USER_NOTICE);
		return $ret;
	}
	function getPartnerServices($partnerId) {
		trigger_error("getPartnerServices(" . $partnerId . ")", E_USER_NOTICE);
		$ret = NULL;
		$query = 'SELECT id, web, css FROM tab_service WHERE id_partner = ' . $partnerId;
		$result = mysql_query($query, $this->dbLink) or trigger_error('Query failed: ' . mysql_error() . "SQL: $query", E_USER_ERROR);
		$i = 0;
		while ($row = mysql_fetch_assoc($result))
			$ret[$i++] = $row;
		mysql_free_result($result);
		trigger_error("getPartnerServices():" . $ret, E_USER_NOTICE);
		return $ret;
	}
	function getPartnerSongs($partnerId) {
		trigger_error("getPartnerSongs(" . $partnerId . ")", E_USER_NOTICE);
		$ret = NULL;
		// lista piosenek, ktor zostaly pobrane
		$query = "SELECT tab_song.* , COUNT(tab_song.id) as ordered";
		$query .= " ";
		$query .= " FROM tab_song,  con_song_product, tab_product, tab_order";
		$query .= " WHERE tab_song.id_partner = " . $partnerId;
		$query .= " AND con_song_product.id_song = tab_song.id";
		$query .= " AND con_song_product.id_product = tab_product.id ";
		$query .= " AND tab_order.id_product = tab_product.id";
		$query .= " GROUP BY  tab_song.id";
		$query .= " ORDER BY ordered DESC ";
		trigger_error("SQL: $query", E_USER_NOTICE);
		$result = mysql_query($query, $this->dbLink) or trigger_error('Query failed: ' . mysql_error() . "SQL: $query", E_USER_ERROR);
		$i = 0;
		while ($row = mysql_fetch_assoc($result))
			$ret[$i++] = $row;
		mysql_free_result($result);
		// uzupełnienie o listę piosenek nie pobranych		
		$query = "SELECT tab_song.* FROM tab_song WHERE id_partner = " . $partnerId;
		$result = mysql_query($query, $this->dbLink) or trigger_error('Query failed: ' . mysql_error() . "SQL: $query", E_USER_ERROR);
		while ($row = mysql_fetch_assoc($result)) {
			$id = (isset ($row['id']) ? $row['id'] : null);
			$isFound = false;
			foreach ($ret as $record) {
				$record_id = (isset ($record['id']) ? $record['id'] : null);
				if ($id == $record_id) {
					$isFound = true;
					break;
				}
			}
			if (!$isFound)
				$ret[$i++] = $row;
		}
		mysql_free_result($result);
		trigger_error("getPartnerSongs():" . $ret, E_USER_NOTICE);
		return $ret;
	}
	function getService($serviceId) {
		trigger_error("getService(" . $serviceId . ")", E_USER_NOTICE);
		$ret = NULL;
		$query = 'SELECT id, web, css FROM tab_service WHERE id = ' . $serviceId;
		$result = mysql_query($query, $this->dbLink) or trigger_error('Query failed: ' . mysql_error() . "SQL: $query", E_USER_ERROR);
		if ($result != FALSE) {
			if ($row = mysql_fetch_assoc($result))
				$ret = $row;
			mysql_free_result($result);
		}
		trigger_error("getService():" . $ret, E_USER_NOTICE);
		return $ret;
	}
	function getPartnerHistoryCashing($partnerId) {
		trigger_error("getPartnerHistoryCashing(" . $partnerId . ")", E_USER_NOTICE);
		$ret = NULL;
		$query = "SELECT id,ordered,realized,amount,`describe`";
		$query .= " FROM tab_cashing";
		$query .= " WHERE id_partner=" . $partnerId;
		$query .= " ORDER BY id DESC";
		trigger_error("SQL: $query", E_USER_NOTICE);
		$result = mysql_query($query, $this->dbLink) or trigger_error('Query failed: ' . mysql_error() . "SQL: $query", E_USER_ERROR);
		$i = 0;
		while ($row = mysql_fetch_assoc($result))
			$ret[$i++] = $row;
		mysql_free_result($result);
		return $ret;
	}
	function getPartnerHistoryMonthlyCoins($partnerId) {
		trigger_error("getPartnerHistoryMonthlyCoins(" . $partnerId . ")", E_USER_NOTICE);
		$ret = NULL;
		$query = "SELECT YEAR( tab_payment.activated ) AS year, MONTH( tab_payment.activated ) AS month , SUM( tab_paymenttype.coins ) AS coins ";
		$query .= " FROM tab_payment, tab_paymenttype";
		$query .= " WHERE tab_payment.id_partner=" . $partnerId;
		$query .= " AND tab_paymenttype.id = tab_payment.id_paymenttype";
		$query .= " AND tab_payment.activated >0";
		$query .= " GROUP BY month ORDER BY year DESC, month DESC";
		trigger_error("SQL: $query", E_USER_NOTICE);
		$result = mysql_query($query, $this->dbLink) or trigger_error('Query failed: ' . mysql_error() . "SQL: $query", E_USER_ERROR);
		$i = 0;
		while ($row = mysql_fetch_assoc($result))
			$ret[$i++] = $row;
		mysql_free_result($result);
		return $ret;
	}
	function getPartnerHistoryMonthlySongs($partnerId) {
		trigger_error("getPartnerHistoryMonthlySongs(" . $partnerId . ")", E_USER_NOTICE);
		$ret = NULL;
		$query = "SELECT YEAR( tab_order.added ) AS year, MONTH( tab_order.added) AS month , COUNT( tab_order.id ) AS orders";
		$query .= " FROM tab_order, con_song_product, tab_song";
		$query .= " WHERE tab_song.id_partner=" . $partnerId;
		$query .= " AND tab_order.id_product = con_song_product.id_product";
		$query .= " AND con_song_product.id_song = tab_song.id";
		$query .= " GROUP BY month ORDER BY year DESC, month DESC";
		trigger_error("SQL: $query", E_USER_NOTICE);
		$result = mysql_query($query, $this->dbLink) or trigger_error('Query failed: ' . mysql_error() . "SQL: $query", E_USER_ERROR);
		$i = 0;
		while ($row = mysql_fetch_assoc($result))
			$ret[$i++] = $row;
		mysql_free_result($result);
		return $ret;
	}

};
/*
SELECT tab_song.id, tab_song.title, COUNT(tab_song.id) AS count
FROM tab_song, tab_order, con_song_product
WHERE tab_song.id = con_song_product.id_song
AND con_song_product.id_product = tab_order.id_product
AND tab_order.added > '2005-01-01' AND tab_order.added < '2006-01-01'
GROUP BY tab_song.id
ORDER BY count DESC
 * 
SELECT tab_song.* , COUNT(tab_product.id) as ordered
FROM tab_song,  con_song_product, tab_product, tab_order
WHERE tab_song.id_partner = 1
AND con_song_product.id_song = tab_song.id
AND con_song_product.id_product = tab_product.id 
AND tab_order.id_product = tab_product.id
GROUP BY  tab_product.id
ORDER BY ordered DESC 
*/
?>