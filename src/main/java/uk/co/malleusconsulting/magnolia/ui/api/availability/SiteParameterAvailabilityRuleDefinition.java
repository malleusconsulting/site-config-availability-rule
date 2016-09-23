/**
 * This file Copyright 2016 Malleus Consulting Ltd.
 * All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.malleusconsulting.magnolia.ui.api.availability;

import uk.co.malleusconsulting.magnolia.ui.framework.availability.SiteParameterAvailabilityRule;
import info.magnolia.ui.api.availability.ConfiguredAvailabilityRuleDefinition;

public class SiteParameterAvailabilityRuleDefinition extends ConfiguredAvailabilityRuleDefinition {

	private String parameterName;

	public SiteParameterAvailabilityRuleDefinition() {
		super.setImplementationClass(SiteParameterAvailabilityRule.class);
	}

	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}
}
