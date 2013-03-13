package org.eclipse.bpmn2.modeler.core.test.feature.add;

import static org.eclipse.bpmn2.modeler.core.layout.util.ConversionUtil.point;
import static org.eclipse.bpmn2.modeler.core.test.util.assertions.Bpmn2ModelAssertions.assertThat;
import static org.eclipse.bpmn2.modeler.core.test.util.assertions.Bpmn2ModelAssertions.elementOfType;
import static org.eclipse.bpmn2.modeler.core.test.util.operations.AddBoundaryEventOperation.addBoundaryEvent;

import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.modeler.core.layout.util.LayoutUtil;
import org.eclipse.bpmn2.modeler.core.test.feature.AbstractFeatureTest;
import org.eclipse.bpmn2.modeler.core.test.util.DiagramResource;
import org.eclipse.bpmn2.modeler.core.test.util.Util;
import org.eclipse.bpmn2.modeler.core.utils.GraphicsUtil;
import org.eclipse.bpmn2.modeler.core.utils.LabelUtil;
import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.datatypes.IRectangle;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.junit.Test;

public class AddBoundaryEventFeatureTest extends AbstractFeatureTest {

	@Test
	@DiagramResource("org/eclipse/bpmn2/modeler/core/test/feature/add/AddBoundaryEventFeatureTest.testBase.bpmn")
	public void testCorrectPositioningOnAddToTask() throws Exception {

		int EVENT_WIDTH = GraphicsUtil.getEventSize(getDiagram()).getWidth();
		int EVENT_HEIGHT = GraphicsUtil.getEventSize(getDiagram()).getHeight();
		
		// given diagram
		// with task shape
		ContainerShape taskShape = (ContainerShape) Util.findShapeByBusinessObjectId(diagram, "Task_1");
		IRectangle shapeBounds = LayoutUtil.getAbsoluteBounds(taskShape);
		
		Point boundaryAttachPosition = point(60, 70); // relative positioning
		Point expectedBoundaryMidpoint = point(
				shapeBounds.getX() + boundaryAttachPosition.getX(), 
				shapeBounds.getY() + 80); // snapping
		
		// default label is 3 * 3 px wide
		
		Point expectedBoundaryLabelPosition = point(
				expectedBoundaryMidpoint.getX() - 3, 
				expectedBoundaryMidpoint.getY() + EVENT_HEIGHT / 2 + 5); 
		
		// when
		// element is added to it
		addBoundaryEvent(diagramTypeProvider)
			.atLocation(boundaryAttachPosition)
			.toContainer(taskShape)
			.execute();
		
		// then
		EList<Shape> diagramChildren = diagram.getChildren();
		
		Shape createdShape = diagramChildren.get(diagramChildren.size() - 2); // last element diagramChildren.size() - 1 is label

		// shape should have been created
		assertThat(createdShape)
			.isLinkedTo(elementOfType(BoundaryEvent.class))
			.midPoint()
				.isEqualTo(expectedBoundaryMidpoint);
		
		// and label is positioned correctly, too
		Shape boundaryLabelShape = LabelUtil.getLabelShape(createdShape, getDiagram());
		
		assertThat(boundaryLabelShape)
			.position()
				.isEqualTo(expectedBoundaryLabelPosition);
	}
	

	@Test
	@DiagramResource("org/eclipse/bpmn2/modeler/core/test/feature/add/AddBoundaryEventFeatureTest.testBase.bpmn")
	public void testCorrectPositioningOnAddToSubprocess() throws Exception {

		int EVENT_WIDTH = GraphicsUtil.getEventSize(getDiagram()).getWidth();
		int EVENT_HEIGHT = GraphicsUtil.getEventSize(getDiagram()).getHeight();
		
		// given diagram
		// with subprocess shape
		ContainerShape subprocessShape = (ContainerShape) Util.findShapeByBusinessObjectId(diagram, "SubProcess_1");
		IRectangle shapeBounds = LayoutUtil.getAbsoluteBounds(subprocessShape);
		
		Point boundaryAttachPosition = point(230, 290); // relative positioning
		Point expectedBoundaryMidpoint = point(
				shapeBounds.getX() + boundaryAttachPosition.getX(), 
				shapeBounds.getY() + 300); // snapping
		
		// default label is 3 * 3 px wide
		
		Point expectedBoundaryLabelPosition = point(
				expectedBoundaryMidpoint.getX() - 3, 
				expectedBoundaryMidpoint.getY() + EVENT_HEIGHT / 2 + 5);
		
		// when
		// element is added to it
		addBoundaryEvent(diagramTypeProvider)
			.atLocation(boundaryAttachPosition) 
			.toContainer(subprocessShape)
			.execute();
		
		// then
		EList<Shape> diagramChildren = diagram.getChildren();
		
		Shape createdShape = diagramChildren.get(diagramChildren.size() - 2); // last element diagramChildren.size() - 1 is label
		
		// shape should have been created
		assertThat(createdShape)
			.isLinkedTo(elementOfType(BoundaryEvent.class))
			.midPoint()
				.isEqualTo(expectedBoundaryMidpoint);
		
		// and label is positioned correctly, too
		Shape boundaryLabelShape = LabelUtil.getLabelShape(createdShape, getDiagram());
		
		assertThat(boundaryLabelShape)
			.position()
				.isEqualTo(expectedBoundaryLabelPosition);
	}
}