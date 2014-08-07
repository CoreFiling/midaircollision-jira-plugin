package com.corefiling.jira.midaircollision;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Set;

import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueFactory;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.customfields.impl.AbstractCustomFieldType;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.issue.customfields.view.CustomFieldParams;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.util.ErrorCollection;

public class MidAirCollisionField extends AbstractCustomFieldType
{
	IssueManager issueManager;
	IssueFactory issueFactory;
	public MidAirCollisionField(IssueManager issueManager, IssueFactory issueFactory)
	{
		this.issueManager = issueManager;
		this.issueFactory = issueFactory;
	}
	
	public void createValue(CustomField field, Issue issue, Object value)
	{
		// Do nothing
	}
	public void updateValue(CustomField field, Issue issue, Object value)
	{
		// Do nothing
	}
	public void validateFromParams(CustomFieldParams relevantParams,
			ErrorCollection errorCollectionToAddTo, FieldConfig config)
	{
		// Check whether issue has been updated more recently than when this
		// field was written out.
		String issueKey = (String)relevantParams.getFirstValueForKey("issue");
		String timestamp = (String)relevantParams.getFirstValueForNullKey();
		if (issueKey != null && timestamp != null)
		{
			try
			{
				GenericValue issueGV = issueManager.getIssue(issueKey);
				if (issueGV != null)
				{
					Issue issue = issueFactory.getIssue(issueGV);
					if (issue != null && issue.getUpdated() != null)
					{
						long timeNow = issue.getUpdated().getTime();
						long then = Long.parseLong(timestamp);
						
						if (timeNow > then)
						{
							errorCollectionToAddTo.addErrorMessage("Mid-air collision detected! Cannot submit your edits because they would overwrite other changes.  Please go back to the bug and take a look at those changes before making yours again."); 
						}
					}
				}
			}
			catch (GenericEntityException e)
			{
				e.printStackTrace();
			}
		}
	}

	public String getChangelogValue(CustomField field, Object value) {
		return null;
	}

	public Object getDefaultValue(FieldConfig fieldConfig) {
		return null;
	}

	public Object getSingularObjectFromString(String string)
			throws FieldValidationException
	{
		return (string != null && !string.equals("")) ? Long.decode(string) : null;
	}

	public String getStringFromSingularObject(Object value) {
		return value != null ? value.toString() : "";
	}

	public Object getStringValueFromCustomFieldParams(
			CustomFieldParams parameters)
	{
		return parameters.getFirstValueForNullKey();
	}

	public Object getValueFromCustomFieldParams(CustomFieldParams parameters)
			throws FieldValidationException
	{
		Object o = parameters.getFirstValueForKey(null);
        return getSingularObjectFromString((String) o);
	}

	public Object getValueFromIssue(CustomField field, Issue issue) {
		return new Long(issue.getUpdated().getTime());
	}

	public Set remove(CustomField field) {
		return Collections.EMPTY_SET;
	}

	public void setDefaultValue(FieldConfig fieldConfig, Object value) {
		// Do nothing
	}
}
