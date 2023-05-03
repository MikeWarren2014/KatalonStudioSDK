package me.mikewarren.katalonstudiosdk.models

public class BaseModel implements Cloneable	{
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
