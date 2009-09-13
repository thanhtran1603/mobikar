function init() {
	document.getElementById('mk_tab_nojs').style.display = 'none';
	clear();
	setTab('load');
}
function clear(){
	document.getElementById('mk_tab_load').className = 'mk_tab_content_hidden';
	document.getElementById('mk_tab_edit').className = 'mk_tab_content_hidden';
	document.getElementById('mk_tab_getit').className = 'mk_tab_content_hidden';
	document.getElementById('mk_tab_help').className = 'mk_tab_content_hidden';
	document.getElementById('mk_tab_menu_load').style.color = '#ffffff';
	document.getElementById('mk_tab_menu_edit').style.color = '#ffffff';
	document.getElementById('mk_tab_menu_getit').style.color = '#ffffff';
	document.getElementById('mk_tab_menu_help').style.color = '#ffffff';
}
function setTab(tab){
	clear();
	if (tab == 'load'){
		document.getElementById('mk_tab_menu_load').style.color = '#ffcc00';
		document.getElementById('mk_tab_load').className = 'mk_tab_content_visible';
	} else if (tab == 'edit'){
		document.getElementById('mk_tab_menu_edit').style.color = '#ffcc00';
		document.getElementById('mk_tab_edit').className = 'mk_tab_content_visible';
	} else if (tab == 'getit'){
		document.getElementById('mk_tab_menu_getit').style.color = '#ffcc00';
		document.getElementById('mk_tab_getit').className = 'mk_tab_content_visible';
	} else if (tab == 'help'){
		document.getElementById('mk_tab_menu_help').style.color = '#ffcc00';
		document.getElementById('mk_tab_help').className = 'mk_tab_content_visible';
	}
}
function load_advanced(){
	if (document.getElementById('mk_tab_load_adv').style.display == 'none'){
		document.getElementById('mk_tab_load_adv').style.display = 'block';
	}
	else{
		//setTab('edit');
		document.getElementById('mk_tab_load_adv').style.display = 'none';
	}
}