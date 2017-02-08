package org.marley.mae.actuator

import grails.util.Holders
import org.springframework.beans.BeansException
import org.springframework.context.ApplicationContext
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.support.LiveBeansView
import org.springframework.util.Assert

import javax.annotation.PostConstruct

/**
 * Exposes JSON view of Spring beans. If the Environment contains a key setting
 * the {@link LiveBeansView#MBEAN_DOMAIN_PROPERTY_NAME} then all application contexts in
 * the JVM will be shown (and the corresponding MBeans will be registered per the standard
 *  behavior of LiveBeansView). Otherwise only the current application context hierarchy.
 *
 *  Based on code from Spring Boot's BeansEndpoint.java by Dave Syer/Andy Wilkinson
 *
 * @author jsampson
 */
class BeanService {

    private final HierarchyAwareLiveBeansView liveBeansView = new HierarchyAwareLiveBeansView();

    @PostConstruct
    private void init(){
        if (Holders.applicationContext.getEnvironment()
                .getProperty(LiveBeansView.MBEAN_DOMAIN_PROPERTY_NAME) == null) {
            this.liveBeansView.leafContext = Holders.applicationContext;
        }
    }

    public String collectBeans() {
        return this.liveBeansView.getSnapshotAsJson();
    }

    private static class HierarchyAwareLiveBeansView extends LiveBeansView {

        private ConfigurableApplicationContext leafContext;

        private void setLeafContext(ApplicationContext leafContext) {
            this.leafContext = asConfigurableContext(leafContext);
        }

        @Override
        public String getSnapshotAsJson() {
            return generateJson(getContextHierarchy());
        }

        private ConfigurableApplicationContext asConfigurableContext(
                ApplicationContext applicationContext) {
            Assert.isTrue(applicationContext instanceof ConfigurableApplicationContext,
                    "'" + applicationContext
                            + "' does not implement ConfigurableApplicationContext");
            return (ConfigurableApplicationContext) applicationContext;
        }

        private Set<ConfigurableApplicationContext> getContextHierarchy() {
            Set<ConfigurableApplicationContext> contexts = new LinkedHashSet<ConfigurableApplicationContext>();
            ApplicationContext context = this.leafContext;
            while (context != null) {
                contexts.add(asConfigurableContext(context));
                context = context.getParent();
            }
            return contexts;
        }

    }
}
