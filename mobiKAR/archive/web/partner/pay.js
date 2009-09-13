function init() {
	document.getElementById('mk_tab_nojs').style.display =  'none';
	document.getElementById('mk_tab_nojs').style.float = 'left';
	document.getElementById('mk_tab_nojs').style.position = 'absolute';
	clear();
	setTab('sms');
}
function clear(){
	document.getElementById('mk_tab_sms').className = 'mk_tab_content_hidden';
	document.getElementById('mk_tab_cheaper').className = 'mk_tab_content_hidden';
	document.getElementById('mk_tab_targets').className = 'mk_tab_content_hidden';
	document.getElementById('mk_tab_help').className = 'mk_tab_content_hidden';
	document.getElementById('mk_tab_menu_sms').style.color = '#ffffff';
	document.getElementById('mk_tab_menu_cheaper').style.color = '#ffffff';
	document.getElementById('mk_tab_menu_targets').style.color = '#ffffff';
	document.getElementById('mk_tab_menu_help').style.color = '#ffffff';
}
function setTab(tab){
	clear();
	if (tab == 'sms'){
		document.getElementById('mk_tab_menu_sms').style.color = '#ffcc00';
		document.getElementById('mk_tab_sms').className = 'mk_tab_content_visible';
	} else if (tab == 'cheaper'){
		document.getElementById('mk_tab_menu_cheaper').style.color = '#ffcc00';
		document.getElementById('mk_tab_cheaper').className = 'mk_tab_content_visible';
	} else if (tab == 'targets'){
		document.getElementById('mk_tab_menu_targets').style.color = '#ffcc00';
		document.getElementById('mk_tab_targets').className = 'mk_tab_content_visible';
	} else if (tab == 'help'){
		document.getElementById('mk_tab_menu_help').style.color = '#ffcc00';
		document.getElementById('mk_tab_help').className = 'mk_tab_content_visible';
	}
}
