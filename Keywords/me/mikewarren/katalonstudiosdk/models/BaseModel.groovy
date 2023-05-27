package me.mikewarren.katalonstudiosdk.models

public class BaseModel implements Cloneable	{
	protected long id;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public BaseModel clone() {
		BaseModel clonedModel = super.clone();

		this.metaClass.properties.findAll { MetaProperty property -> return property.field }
			.each { MetaProperty field ->
				final String fieldName = field.getName();
				Object fieldValue = this."${fieldName}"
	
				if (fieldValue instanceof Cloneable)
					clonedModel."${fieldName}" = fieldValue.clone()
			}

		return clonedModel;
	}
}
