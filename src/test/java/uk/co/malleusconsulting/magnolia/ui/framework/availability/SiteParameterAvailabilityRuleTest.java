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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.site.Site;
import info.magnolia.module.site.functions.SiteFunctions;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.test.RepositoryTestCase;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeItemId;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import uk.co.malleusconsulting.magnolia.ui.api.availability.SiteParameterAvailabilityRuleDefinition;

import com.google.common.collect.ImmutableMap;

public class SiteParameterAvailabilityRuleTest extends RepositoryTestCase {

	private static final String SITE_PARAMETER = "testParameter";

	private SiteParameterAvailabilityRuleDefinition definition;
	private SiteFunctions siteFunctions;
	private String nodeMappedToTrueSiteId;
	private String nodeMappedToFalseSiteId;
	private String nodeMappedToNotConfiguredSiteId;
	private String nodeMappedToMisconfiguredSiteId;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		Session session = MgnlContext.getJCRSession(RepositoryConstants.WEBSITE);
		Node nodeMappedToTrueSite = session.getRootNode().addNode("nodeMappedToTrueSite");
		Node nodeMappedToFalseSite = session.getRootNode().addNode("nodeMappedToFalseSite");
		Node nodeMappedToNotConfiguredSite = session.getRootNode().addNode("nodeMappedToNotConfiguredSite");
		Node nodeMappedToMisconfiguredSite = session.getRootNode().addNode("nodeMappedToMisconfiguredSite");

		final Site trueSite = mock(Site.class);
		when(trueSite.getParameters()).thenReturn(ImmutableMap.of(SITE_PARAMETER, (Object) true));

		final Site falseSite = mock(Site.class);
		when(falseSite.getParameters()).thenReturn(ImmutableMap.of(SITE_PARAMETER, (Object) false));

		final Site notConfiguredSite = mock(Site.class);
		when(notConfiguredSite.getParameters()).thenReturn(new HashMap<String, Object>());

		final Site misconfiguredSite = mock(Site.class);
		when(misconfiguredSite.getParameters()).thenReturn(ImmutableMap.of(SITE_PARAMETER, (Object) "aString"));

		nodeMappedToTrueSiteId = nodeMappedToTrueSite.getIdentifier();
		nodeMappedToFalseSiteId = nodeMappedToFalseSite.getIdentifier();
		nodeMappedToNotConfiguredSiteId = nodeMappedToNotConfiguredSite.getIdentifier();
		nodeMappedToMisconfiguredSiteId = nodeMappedToMisconfiguredSite.getIdentifier();

		siteFunctions = mock(SiteFunctions.class);

		when(siteFunctions.site(any(Node.class))).thenAnswer(new Answer<Site>() {
			Map<String, Site> mappings = ImmutableMap.of(
					nodeMappedToTrueSiteId, trueSite,
					nodeMappedToFalseSiteId, falseSite,
					nodeMappedToNotConfiguredSiteId, notConfiguredSite,
					nodeMappedToMisconfiguredSiteId, misconfiguredSite);

			public Site answer(InvocationOnMock invocation) {
				Node argNode = (Node) invocation.getArguments()[0];
				try {
					return mappings.get(argNode.getIdentifier());
				} catch (RepositoryException e) {
					throw new RuntimeException(e);
				}
			}
		});

		definition = mock(SiteParameterAvailabilityRuleDefinition.class);
		when(definition.getParameterName()).thenReturn(SITE_PARAMETER);
	}

	@Test
	public void ruleReturnsTrueWhenSiteParameterIsTrue() {
		JcrNodeItemId jcrNodeItemId = mock(JcrNodeItemId.class);
		when(jcrNodeItemId.getUuid()).thenReturn(nodeMappedToTrueSiteId);

		SiteParameterAvailabilityRule testRule = new SiteParameterAvailabilityRule(siteFunctions, definition);
		assertTrue(testRule.isAvailableForItem(jcrNodeItemId));
	}

	@Test
	public void ruleReturnsFalseWhenSiteParameterIsFalse() {
		JcrNodeItemId jcrNodeItemId = mock(JcrNodeItemId.class);
		when(jcrNodeItemId.getUuid()).thenReturn(nodeMappedToFalseSiteId);

		SiteParameterAvailabilityRule testRule = new SiteParameterAvailabilityRule(siteFunctions, definition);
		assertFalse(testRule.isAvailableForItem(jcrNodeItemId));
	}

	@Test
	public void ruleReturnsFalseWhenSiteParameterIsNotPresent() {
		JcrNodeItemId jcrNodeItemId = mock(JcrNodeItemId.class);
		when(jcrNodeItemId.getUuid()).thenReturn(nodeMappedToNotConfiguredSiteId);

		SiteParameterAvailabilityRule testRule = new SiteParameterAvailabilityRule(siteFunctions, definition);
		assertFalse(testRule.isAvailableForItem(jcrNodeItemId));
	}

	@Test
	public void ruleReturnsFalseWhenSiteParameterIsNotBoolean() {
		JcrNodeItemId jcrNodeItemId = mock(JcrNodeItemId.class);
		when(jcrNodeItemId.getUuid()).thenReturn(nodeMappedToMisconfiguredSiteId);

		SiteParameterAvailabilityRule testRule = new SiteParameterAvailabilityRule(siteFunctions, definition);
		assertFalse(testRule.isAvailableForItem(jcrNodeItemId));
	}

	@Test
	public void ruleReturnsFalseWhenNotGivenAJcrNodeItemId() {
		SiteParameterAvailabilityRule testRule = new SiteParameterAvailabilityRule(siteFunctions, definition);
		assertFalse(testRule.isAvailableForItem(new Object()));
	}

	@Test
	public void ruleReturnsFalseWhenRepositoryExceptionThrown() {
		JcrNodeItemId jcrNodeItemId = mock(JcrNodeItemId.class);
		when(jcrNodeItemId.getUuid()).thenReturn("an-invalid-guid");

		SiteParameterAvailabilityRule testRule = new SiteParameterAvailabilityRule(siteFunctions, definition);
		assertFalse(testRule.isAvailableForItem(jcrNodeItemId));
	}
}
