var btnChecked="";
function setChecked(i){
        if(btnChecked==i){
        document.getElementsByTagName("input")[i-1].checked = false;
        btnChecked = 0;
        Android.setChecked(-1);
    }
    else{
        btnChecked = i;
        Android.setChecked(i);
    }
}