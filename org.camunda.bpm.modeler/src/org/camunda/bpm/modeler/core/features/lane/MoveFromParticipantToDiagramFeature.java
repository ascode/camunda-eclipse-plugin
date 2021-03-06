/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 *  All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 *
 * @author Ivar Meikas
 ******************************************************************************/
package org.camunda.bpm.modeler.core.features.lane;

import java.io.IOException;

import org.camunda.bpm.modeler.core.Activator;
import org.camunda.bpm.modeler.core.ModelHandler;
import org.camunda.bpm.modeler.core.model.Bpmn2ModelerFactory;
import org.camunda.bpm.modeler.core.utils.FeatureSupport;
import org.camunda.bpm.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.LaneSet;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.Process;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IMoveShapeContext;

public class MoveFromParticipantToDiagramFeature extends MoveLaneFeature {

	public MoveFromParticipantToDiagramFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canMoveShape(IMoveShapeContext context) {
		
		// FIXME: Disabled for now
		return false;
	}

	@Override
	protected void internalMove(IMoveShapeContext context) {
		modifyModelStructure(context);
		FeatureSupport.redrawLaneSet(context.getSourceContainer(), getFeatureProvider());
	}

	private void modifyModelStructure(IMoveShapeContext context) {
		Lane movedLane = getMovedLane(context);
		Participant sourceParticipant = (Participant) getBusinessObjectForPictogramElement(context.getSourceContainer());
		Participant internalParticipant = null;

		ModelHandler handler = ModelHandler.getInstance(getDiagram());
		internalParticipant = handler.getInternalParticipant();
		handler.moveLane(movedLane, internalParticipant);
		
		LaneSet laneSet = null;
		for (LaneSet set : sourceParticipant.getProcessRef().getLaneSets()) {
			if (set.getLanes().contains(movedLane)) {
				laneSet = set;
				break;
			}
		}

		if (laneSet != null) {
			laneSet.getLanes().remove(movedLane);
			if (laneSet.getLanes().isEmpty()) {
				sourceParticipant.getProcessRef().getLaneSets().remove(laneSet);
			}

			Process process = internalParticipant.getProcessRef();
			if (process.getLaneSets().isEmpty()) {
				LaneSet createLaneSet = Bpmn2ModelerFactory.create(LaneSet.class);
//				createLaneSet.setId(EcoreUtil.generateUUID());
				process.getLaneSets().add(createLaneSet);
				ModelUtil.setID(createLaneSet);
			}
			process.getLaneSets().get(0).getLanes().add(movedLane);
		}
	}
}
