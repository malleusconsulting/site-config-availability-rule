package uk.co.malleusconsulting.magnolia.ui.api.availability;

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

public abstract class SiteParameterAvailabilityRule extends
		AbstractAvailabilityRule {

	private static final Logger LOG = LoggerFactory
			.getLogger(SiteParameterAvailabilityRule.class);

	protected SiteFunctions siteFunctions;

	public SiteParameterAvailabilityRule(SiteFunctions siteFunctions) {
		this.siteFunctions = siteFunctions;
	}

	protected abstract String getSiteParameter();

	protected final boolean isAvailableForItem(Object itemId) {

		/**
		 * if the itemId is not a JcrNodeItemId, we cannot retrieve the node and
		 * identify the site
		 */
		if (!(itemId instanceof JcrNodeItemId)) {
			return false;
		}

		try {
			Node node = NodeUtil.getNodeByIdentifier(
					RepositoryConstants.WEBSITE,
					((JcrNodeItemId) itemId).getUuid());
			Site site = siteFunctions.site(node);
			if (site.getParameters().containsKey(getSiteParameter())
					&& site.getParameters().get(getSiteParameter()) instanceof String) {
				return Boolean.parseBoolean((String) site.getParameters().get(
						getSiteParameter()));
			}
		} catch (RepositoryException e) {
			LOG.error(
					"Unable to retrieve node "
							+ ((JcrNodeItemId) itemId).getUuid(), e);
		}
		return false;
	}
}
