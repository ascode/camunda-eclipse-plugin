package org.eclipse.bpmn2.modeler.core.layout;

import org.eclipse.bpmn2.modeler.core.di.DIUtils;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;

/**
 * Single point of entry for connecting and layouting shapes. 
 * 
 * @author nico.rehwaldt
 */
public class ConnectionService {

	public static final String RECONNECT_AFTER_MOVE = "ConnectionService.RECONNECT_AFTER_MOVE";

	public static void reconnectShapeAfterMove(PictogramElement shape) {
		new BpmnElementReconnectionContext().reconnect(shape);
	}
	
	public static void reconnectShapeAfterResize(Shape shape) {
		new BpmnElementReconnectionContext().reconnect(shape);
	}

	public static void reconnectConnectionAfterCreate(Connection connection) {
		reconnectConnection(connection, true, true);
	}

	public static void reconnectConnectionAfterMove(Connection connection) {
		reconnectConnection(connection, false, true);
	}
	
	public static void reconnectConnectionAfterConnectionEndChange(Connection connection) {
		reconnectConnection(connection, true, false);
	}
	
	protected static void reconnectConnection(Connection connection, boolean forceLayout, boolean relayoutOnRepairFail) {
	
		// check if new anchor point is neccessary
		AnchorContainer startAnchorContainer = connection.getStart().getParent();
		AnchorContainer endAnchorContainer = connection.getEnd().getParent();
		
		if (startAnchorContainer instanceof Shape && endAnchorContainer instanceof Shape) {
			new ConnectionReconnectionContext(connection, forceLayout, relayoutOnRepairFail).reconnect();
		} else {
			throw new LayoutingException("Cannot handle connection: " + connection);
		}
		
		DIUtils.updateDIEdge(connection);
	}

	public static void reconnectContainerAfterMove(ContainerShape container) {
		reconnectShapeAfterMove(container);
	}
}
