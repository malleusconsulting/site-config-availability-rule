# site-config-availability-rule
Provides a content app action availability rule that references site configuration parameters.

**Note** This code is provided as a jar archive and makes no check of Magnolia's licence state. However, multi-site configuration requires the [Multisite Module](https://documentation.magnolia-cms.com/display/DOCS/Multisite+module) which is only available with an Enterprise Pro licence. This project does not provide multi-site functionality on its own.


##Installation
This module is available from [the central Maven repository](http://repo1.maven.org/maven2/uk/co/malleusconsulting/magnolia/ui/api/availability/site-config-availability-rule/1.0.0/) and can be installed using:

```xml
<dependency>
  <groupId>uk.co.malleusconsulting.magnolia.ui.api.availability</groupId>
  <artifactId>site-config-availability-rule</artifactId>
  <version>1.0.0</version>
</dependency>
```

## Description
This class, although implemented for flexibility, was written to meet a specific use case:
> Having extended the page creation dialog in the Pages app, I only want the new dialog to be shown for specific sites.
> When adding a page to Site A, the extended dialog should be shown.
> However, when adding a page to Site B, the original, non-extended, dialog should still be shown.

The extended dialog was created to present additional fields to an editor when a new page was created. In this case, to force categorisation of a page from a list of pre-configured categories. While all sites required this dialog, it was reasonable to modify the dialog found at `/modules/pages/dialogs/createPage`.

In order to meet this new requirement with the best possible editor experience, two "add" action definitions are required in the Pages browser subapp: the original action referencing the original dialog will be used for site B and a modified version, referencing a dialog that extends the original dialog, for site A.

<img src="https://raw.githubusercontent.com/malleusconsulting/site-config-availability-rule/gh_pages/extended_add_page_action_with_custom_dialog.png" width="474" height="187" title="Extended add page action referencing a custom dialog" />

If availability rules were [applied to the actions](https://documentation.magnolia-cms.com/display/DOCS/Action+definition#Actiondefinition-Actionavailability), the editor would see both at all times but one would be disabled depending on the site config. This is sub-optimal and can be improved by [configuring two action bar sections](https://documentation.magnolia-cms.com/display/DOCS/Action+bar+definition). 

Upon opening the Pages browser or selecting a node within it, the appropriate action bar is found by iterating through all those configured and checking all the rules return `true`. In this case, because we want the custom action bar to appear for some sites but to default to the original we **must ensure** the customised action bar appears first in the repository. Then, if the new rule fails to pass, Magnolia will find the original and load that.

<img src="https://raw.githubusercontent.com/malleusconsulting/site-config-availability-rule/gh_pages/extended_action_bar_section_definition.png" width="417" height="311" title="Extended action bar section definition replacing add actions" />

## Implementation
When adding an availability rule to an an action bar section, the default definition used is [info.magnolia.ui.api.availability.ConfiguredAvailabilityRuleDefinition](https://git.magnolia-cms.com/projects/PLATFORM/repos/ui.pub/browse/magnolia-ui-api/src/main/java/info/magnolia/ui/api/availability/ConfiguredAvailabilityRuleDefinition.java). This can be replaced with a subclass by setting a property of the rule node called `class`. The Node2Bean mechanism will ensure a definition of the given class is instantiated and any additional node properties are used to populate the definition via setter methods.

<img src="https://raw.githubusercontent.com/malleusconsulting/site-config-availability-rule/gh_pages/configured_rule.png" width="861" height="156" title="Configured SiteParameterAvailabilityRuleDefinition" />

The above screenshot shows that the `implementationClass` property has been removed. As the new definition class has been written specifically for use with the new availability rule, this has been moved into [the definition's code](https://github.com/malleusconsulting/site-config-availability-rule/blob/master/src/main/java/uk/co/malleusconsulting/magnolia/ui/api/availability/SiteParameterAvailabilityRuleDefinition.java) for simpler configuration.

Magnolia will create an instance of the new rule and, via Guice, inject the configured definition object. The rule will then use this to find the configured parameter name and configure itself.

To find which action bar to display, Magnolia will call the [rule](https://github.com/malleusconsulting/site-config-availability-rule/blob/master/src/main/java/uk/co/malleusconsulting/magnolia/ui/framework/availability/SiteParameterAvailabilityRule.java)'s implementation of `isAvailableForItem(Object itemId)` method from [info.magnolia.ui.api.availability.AbstractAvailabilityRule](https://git.magnolia-cms.com/projects/PLATFORM/repos/ui.pub/browse/magnolia-ui-api/src/main/java/info/magnolia/ui/api/availability/AbstractAvailabilityRule.java). As the custom rule is also provided with the [info.magnolia.module.site.functions.SiteFunctions](https://git.magnolia-cms.com/projects/MODULES/repos/site/browse/magnolia-site/src/main/java/info/magnolia/module/site/functions/SiteFunctions.java) helper class it can use the provided item ID to find which site a page node is mapped to and then examine the site's parameters.

## Usage
As well as the configuration described above, the site definition must also be updated to include the desired parameter. The rule expects this to be a Boolean value so **the type must be set to 'Boolean'**. In the above example, the rule has been configured (via *parameterName*) to look for a parameter called *useCustomPageDialog*. Therefore the desired site configuration looks like this:

<img src="https://raw.githubusercontent.com/malleusconsulting/site-config-availability-rule/gh_pages/site_configuration_with_parameter.png" width="629" height="249" title="Site configuration with Boolean parameter" />
