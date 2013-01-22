package org.eclipse.bpmn2.modeler.core.test.feature.move;

import static org.eclipse.bpmn2.modeler.core.layout.util.ConversionUtil.point;
import static org.eclipse.bpmn2.modeler.core.test.util.assertions.Bpmn2ModelAssertions.assertThat;
import static org.eclipse.bpmn2.modeler.core.test.util.operations.MoveShapeOperation.move;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.modeler.core.layout.util.LayoutUtil;
import org.eclipse.bpmn2.modeler.core.test.feature.AbstractFeatureTest;
import org.eclipse.bpmn2.modeler.core.test.util.DiagramResource;
import org.eclipse.bpmn2.modeler.core.test.util.Util;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.GraphicsUtil;
import org.eclipse.graphiti.datatypes.IRectangle;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.fest.assertions.api.Fail;
import org.junit.Test;

/**
 * 
 * @author Daniel Meyer
 * @author Nico Rehwaldt
 */
public class MoveFlowNodeFeatureTest extends AbstractFeatureTest {
	
	@Test
	@DiagramResource
	public void testMoveShapeOutOfContainer() {

		// find shapes
		Shape userTaskShape = Util.findShapeByBusinessObjectId(diagram, "UserTask_1");
		ContainerShape subProcessShape = (ContainerShape) Util.findShapeByBusinessObjectId(diagram, "SubProcess_1");
		ContainerShape processShape = (ContainerShape) diagram;
		BaseElement subProcessElement = BusinessObjectUtil.getFirstBaseElement(subProcessShape);

		// first, the usertask is contained in the subprocess
		assertThat(subProcessShape).hasChild(userTaskShape);
		assertThat(processShape).doesNotHaveChild(userTaskShape);
		assertThat(userTaskShape).hasParentModelElement(subProcessElement);

		// move the usertask out from under the subprocess into the process
		move(userTaskShape, diagramTypeProvider)
			.toContainer(processShape)
			.execute();

		// now the usertask is contained in the process
		assertThat(subProcessShape).doesNotHaveChild(userTaskShape);
		
		assertThat(processShape).hasChild(userTaskShape);
		assertThat(userTaskShape).isContainedIn(processShape);
	}
	
	@Test
	@DiagramResource
	public void testMoveShapeIntoContainer() {

		// find shapes
		Shape userTaskShape = Util.findShapeByBusinessObjectId(diagram, "UserTask_1");
		ContainerShape subProcessShape = (ContainerShape) Util.findShapeByBusinessObjectId(diagram, "SubProcess_1");
		ContainerShape processShape = (ContainerShape) diagram;
		BaseElement subProcessElement = BusinessObjectUtil.getFirstBaseElement(subProcessShape);

		// first, the usertask is contained in the process
		assertThat(subProcessShape).doesNotHaveChild(userTaskShape);
		assertThat(processShape).hasChild(userTaskShape);
		assertThat(userTaskShape).hasParentModelElement(subProcessElement.eContainer());

		// move the usertask into the subprocess
		move(userTaskShape, diagramTypeProvider)
			.toContainer(subProcessShape)
			.execute();

		// now the usertask is contained in the subprocess
		assertThat(subProcessShape).hasChild(userTaskShape);
		assertThat(processShape).doesNotHaveChild(userTaskShape);
		assertThat(userTaskShape).hasParentModelElement(subProcessElement);
	}

	@Test
	@DiagramResource
	public void testMoveOutOfParticipantNotAllowed() {

		// given
		Shape taskShape = Util.findShapeByBusinessObjectId(diagram, "Task_1");
		Shape participantShape = Util.findShapeByBusinessObjectId(diagram, "_Participant_3");

		// assume
		// taks shape is part of participant
		assertThat(taskShape)
			.isContainedIn(participantShape);
		
		// when 
		// trying to move the task shape
		move(taskShape, diagramTypeProvider)
			.toContainer(diagram)
			.by(300, 200)
			.execute();
		
		// then 
		// it should still be contained in participant
		// (movement not allowed)
		assertThat(participantShape)
			.hasChild(taskShape);
	}
	
	@Test
	@DiagramResource("org/eclipse/bpmn2/modeler/core/test/feature/move/MoveFlowNodeFeature.testMoveBetweenLanes.bpmn")
	public void testMoveEventToSiblingLane() {
		
		// given
		Shape eventShape = Util.findShapeByBusinessObjectId(diagram, "StartEvent_1");
		ContainerShape targetLaneShape = (ContainerShape) Util.findShapeByBusinessObjectId(diagram, "Lane_5");
		
		// when moving event to sibling lane
		move(eventShape, diagramTypeProvider)
			.toContainer(targetLaneShape)
			.execute();
	
		// then
		// event label should have been moved, too
		assertThat(eventShape)
			.isContainedIn(targetLaneShape);
		
		Shape labelShape = GraphicsUtil.getLabelShape(eventShape, getDiagram());
		
		assertThat(labelShape).isContainedIn(targetLaneShape);
	}
	
	@Test
	@DiagramResource("org/eclipse/bpmn2/modeler/core/test/feature/move/MoveFlowNodeFeature.testTaskWithBoundaryEvent.bpmn")
	public void testMoveTaskWithBoundaryEvent() {

		// given
		Shape taskShape = Util.findShapeByBusinessObjectId(diagram, "UserTask_1");

		IRectangle boundsBeforeMove = LayoutUtil.getAbsoluteBounds(taskShape);
		
		// when moving event to the parents sibling lane
		move(taskShape, diagramTypeProvider)
			.by(30, 24)
			.execute();

		IRectangle boundsAfterMove = LayoutUtil.getAbsoluteBounds(taskShape);

		Point posBeforeMove = point(boundsBeforeMove.getX(), boundsBeforeMove.getY());
		Point posAfterMove = point(boundsAfterMove.getX(), boundsAfterMove.getY());
		Point expectedPosAfterMove = point(posBeforeMove.getX() + 30, posBeforeMove.getY() + 24);
		
		// then
		// task should have moved (no NPE)
		assertThat(posAfterMove).isEqualTo(expectedPosAfterMove);
	}
	
	@Test
	@DiagramResource("org/eclipse/bpmn2/modeler/core/test/feature/move/MoveFlowNodeFeature.testMoveBetweenLanes.bpmn")
	public void testMoveEventToParentsSiblingLane() {

		// given
		Shape eventShape = Util.findShapeByBusinessObjectId(diagram, "StartEvent_1");
		ContainerShape targetLaneShape = (ContainerShape) Util.findShapeByBusinessObjectId(diagram, "Lane_3");

		// when moving event to the parents sibling lane
		move(eventShape, diagramTypeProvider)
			.toContainer(targetLaneShape)
			.execute();

		// then
		// event label should have been moved, too
		assertThat(eventShape)
			.isContainedIn(targetLaneShape);
		
		Shape labelShape = GraphicsUtil.getLabelShape(eventShape, getDiagram());
		
		assertThat(labelShape).isContainedIn(targetLaneShape);
	}
	
	@Test
	@DiagramResource("org/eclipse/bpmn2/modeler/core/test/feature/move/MoveFlowNodeFeature.testMoveBetweenLanes.bpmn")
	public void testMoveGatewayToSiblingLane() {

		// given
		Shape gatewayShape = Util.findShapeByBusinessObjectId(diagram, "ExclusiveGateway_1");
		ContainerShape targetLaneShape = (ContainerShape) Util.findShapeByBusinessObjectId(diagram, "Lane_3");

		// when gateway event to sibling lane
		move(gatewayShape, diagramTypeProvider)
			.toContainer(targetLaneShape)
			.execute();
	
		// then 
		// gateway label should have been moved, too
		assertThat(gatewayShape)
			.isContainedIn(targetLaneShape);
		
		
		Shape labelShape = GraphicsUtil.getLabelShape(gatewayShape, getDiagram());
		
		assertThat(labelShape).isContainedIn(targetLaneShape);
	}

	@Test
	@DiagramResource("org/eclipse/bpmn2/modeler/core/test/feature/move/MoveFlowNodeFeature.testMoveBetweenLanes.bpmn")
	public void testMoveGatewayToParticipantWithLanesNotAllowed() {

		// given
		Shape gatewayShape = Util.findShapeByBusinessObjectId(diagram, "ExclusiveGateway_1");
		
		ContainerShape preMoveLaneShape = (ContainerShape) Util.findShapeByBusinessObjectId(diagram, "Lane_6");
		ContainerShape targetParticipantShape = (ContainerShape) Util.findShapeByBusinessObjectId(diagram, "_Participant_2");
		
		Shape labelShape = GraphicsUtil.getLabelShape(gatewayShape, getDiagram());
		
		// when gateway event to sibling lane
		move(gatewayShape, diagramTypeProvider)
			.toContainer(targetParticipantShape)
			.execute();
	
		// then 
		// gateway label should still be contained in original lane
		assertThat(gatewayShape)
			.isContainedIn(preMoveLaneShape);
		
		// same applies to label
		assertThat(labelShape).isContainedIn(preMoveLaneShape);
	}
	
	@Test
	@DiagramResource("org/eclipse/bpmn2/modeler/core/test/feature/move/MoveFlowNodeFeature.testMoveBetweenLanes.bpmn")
	public void testMoveTaskWithBoundaryEventAttachedToSiblingLane() {
		
		// given
		Shape taskShape = Util.findShapeByBusinessObjectId(diagram, "Task_1");
		Shape boundaryEventShape = Util.findShapeByBusinessObjectId(diagram, "BoundaryEvent_1");
		
		ContainerShape targetLaneShape = (ContainerShape) Util.findShapeByBusinessObjectId(diagram, "Lane_3");
		
		// when moving task to sibling lane
		move(taskShape, diagramTypeProvider)
			.toContainer(targetLaneShape)
			.execute();

		// then
		// boundary event shapes label should have moved, too
		assertThat(boundaryEventShape)
			.isContainedIn(targetLaneShape);
		
		Shape labelShape = GraphicsUtil.getLabelShape(boundaryEventShape, getDiagram());
		
		assertThat(labelShape).isContainedIn(targetLaneShape);
	}
}