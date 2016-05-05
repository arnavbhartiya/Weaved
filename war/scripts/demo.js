
	/**
	*
	*	Demo 1: Elements
	*
	*/
	
	/*// Event Listener for when the dragged element is over the drop zone.
	dropZoneOne.addEventListener('dragover', function(e) {
		if (e.preventDefault) {
			e.preventDefault();
		}

		e.dataTransfer.dropEffect = 'move';

		return false;
	});

	// Event Listener for when the dragged element enters the drop zone.
	dropZoneOne.addEventListener('dragenter', function(e) {
		this.className = "over";
	});

	// Event Listener for when the dragged element leaves the drop zone.
	dropZoneOne.addEventListener('dragleave', function(e) {
		this.className = "";
	});

	// Event Listener for when the dragged element dropped in the drop zone.
	dropZoneOne.addEventListener('drop', function(e) {
		if (e.preventDefault) e.preventDefault(); 
		if (e.stopPropagation) e.stopPropagation(); 
  	
		this.className = "";
		this.innerHTML = "Dropped " + e.dataTransfer.getData('text');

		// Remove the element from the list.
		document.querySelector('#drag-elements').removeChild(elementDragged);
		elementDragged = null;

		return false;
	});
	*/
