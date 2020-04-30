function getSelectedText() {
    var selection = null;

    if (window.getSelection) {
        selection = window.getSelection();
    } else if (typeof document.selection != "undefined") {
        selection = document.selection;
    }

    var selectedRange = selection.getRangeAt(0);

    Android.setSelectedText(selectedRange.toString());
}