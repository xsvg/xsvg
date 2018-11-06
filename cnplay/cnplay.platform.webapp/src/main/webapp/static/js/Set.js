/**
 * javascript set.js
 */
function Set()
{
	this.entry = new Array();

	// add
	this.add = function(value)
	{
		if(!this.contains(value))
		{
			this.entry.push(value);
		}
	}

	// get
	this.get = function(index)
	{
		if (index < this.entry.length)
		{
			return this.entry[index];
		}
		else
		{
			return null;
		}
	}

	// delete delete this.entry[index];
	this.remove = function(value)
	{
		for (var index in this.entry)
		{
			if (this.entry[index] == value)
			{
				for(var i = index;i < this.entry.length-1;)
				{
					this.entry[i]=this.entry[++i];
				}
				this.entry.length-=1;
				return;
			}
		}	
	}

	// contains
	this.contains = function(value)
	{
		for ( var index in this.entry)
		{
			if (this.entry[index] == value)
			{
				return true;
			}
		}
		return false;
	}

	// get all values
	this.values = function()
	{
		return this.entry;
	}

	// empties the set of all values
	this.clear = function()
	{
		this.entry = new Array();
		return this;
	};

	// returns true if the set is empty, false otherwise
	this.isEmpty = function()
	{

		return this.entry.length > 0 ? false : true;
	};

	// size
	this.size = function()
	{
		return this.entry.length;
	}
}