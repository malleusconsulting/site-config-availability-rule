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
package uk.co.malleusconsulting.magnolia.ui.framework.availability;

import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.module.site.Site;
import info.magnolia.module.site.functions.SiteFunctions;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.ui.api.availability.AbstractAvailabilityRule;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeItemId;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.malleusconsulting.magnolia.ui.api.availability.SiteParameterAvailabilityRuleDefinition;

public class SiteParameterAvailabilityRule extends AbstractAvailabilityRule {

	private static final Logger LOG = LoggerFactory.getLogger(SiteParameterAvailabilityRule.class);

	protected SiteFunctions siteFunctions;
	protected String parameterName;

	public SiteParameterAvailabilityRule(SiteFunctions siteFunctions, SiteParameterAvailabilityRuleDefinition definition) {
		this.siteFunctions = siteFunctions;
		this.parameterName = definition.getParameterName();
	}

	@Override
	public boolean isAvailableForItem(Object itemId) {

		/**
		 * if the itemId is not a JcrNodeItemId, we cannot retrieve the node and
		 * identify the site
		 */
		if (!(itemId instanceof JcrNodeItemId)) {
			return false;
		}

		try {
			/**
			 * Retrieves the node from the website workspace. The workspace can
			 * be taken from the itemId but the site mappings only refer to
			 * website node paths
			 */
			Node node = NodeUtil.getNodeByIdentifier(RepositoryConstants.WEBSITE, ((JcrNodeItemId) itemId).getUuid());

			Site site = siteFunctions.site(node);
			if (site.getParameters().containsKey(parameterName)
					&& site.getParameters().get(parameterName) instanceof Boolean) {
				return (Boolean) site.getParameters().get(parameterName);
			}

		} catch (RepositoryException e) {
			LOG.error("Unable to retrieve node " + ((JcrNodeItemId) itemId).getUuid(), e);
		}
		return false;
	}
}
