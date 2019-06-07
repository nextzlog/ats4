@(id: String = "")

$('#@id').change(function() {
	$('#@{id}-name').html($(this).val().split(/[\\\/]+/).pop());
});

document.getElementById('@{id}-button').onclick = function() {
	$('#@id').click();
};
