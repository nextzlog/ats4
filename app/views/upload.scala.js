@(id: String = "")

$('#@id').change(function() {
	$('#@{id}-name').html($(this).val().split(/[\\\/]+/).pop())
})
