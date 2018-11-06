/**
 * javascript 实现的java之Map var map = new Map(); map.put("key","value");
 * map.this.size(); map.toString(); map.values(); map.keys() var map = new
 * Map(); var value = {id:'id',callback:function aa(result){ alert(result); }};
 * map.put('key',value); map.get('key').callback('test');
 * 
 * @returns {Map}
 */
function Map()
{
	this.length = 0;

	this.entry = new Object();

	// add
	this.put = function(key, value)
	{
		if (!this.containsKey(key))
		{
			this.length++;
		}
		this.entry[key] = value;
	}

	// get
	this.get = function(key)
	{
		if (this.containsKey(key))
		{
			return this.entry[key];
		}
		else
		{
			return null;
		}
	}

	// delete
	this.remove = function(key)
	{
		if (delete this.entry[key])
		{
			this.length--;
		}
	}

	// containsKey
	this.containsKey = function(key)
	{
		return (key in this.entry);
	}

	// containsValue
	this.containsValue = function(value)
	{
		for ( var prop in this.entry)
		{
			if (this.entry[prop] == value)
			{
				return true;
			}
		}
		return false;
	}

	// get all values
	this.values = function()
	{
		var values = new Array(this.length);
		for ( var prop in this.entry)
		{
			values.push(this.entry[prop]);
		}
		return values;
	}

	// get all keys
	this.keys = function()
	{
		var keys = new Array(this.length);
		for ( var prop in this.entry)
		{
			keys.push(prop);
		}
		return keys;
	}

	// this.size
	this.size = function()
	{
		return this.length;
	}
}