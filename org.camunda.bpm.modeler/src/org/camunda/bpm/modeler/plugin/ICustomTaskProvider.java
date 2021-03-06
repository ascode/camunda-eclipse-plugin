package org.camunda.bpm.modeler.plugin;

import org.camunda.bpm.modeler.core.features.api.container.IFeatureContainer;
import org.camunda.bpm.modeler.plugin.palette.IPaletteIntegration;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.views.properties.tabbed.ISection;

/**
 * Implementations of this interface provide custom tasks to the
 * modeler.
 * 
 * @author nico.rehwaldt
 */
public interface ICustomTaskProvider {

	/**
	 * Returns the id of the custom task provider
	 * @return
	 */
	String getId();
	
	/**
	 * Returns the name of the custom task
	 * @return
	 */
	String getTaskName();

	/**
	 * <p>Returns true if the given provider applies to the 
	 * specified {@link EObject}.</p>
	 * 
	 * If a {@link IFeatureContainer} is provided by this implementation via {@link #getFeatureContainer()}
	 * this method should behave in the same way as {@link IFeatureContainer#canApplyTo(Object)}. 
	 * 
	 * @param eObject
	 * 
	 * @return
	 */
	boolean appliesTo(EObject eObject);
	
	/**
	 * Returns a {@link IFeatureContainer} that provides custom task features
	 * 
	 * @return
	 */
	IFeatureContainer getFeatureContainer();
	
	/**
	 * Returns a custom tab section for the task or <code>null</code> if no custom tab section should be created.
	 * 
	 * A custom tab section may contain specific properties for the task type.
	 * 
	 * @return
	 */
	ISection getTabSection();

	/**
	 * Returns the integration of the custom task feature into the palette.
	 * 
	 * Returning <code>null</code> will result in no integration.
	 * 
	 * @return
	 */
	IPaletteIntegration getPaletteIntegration();
}
