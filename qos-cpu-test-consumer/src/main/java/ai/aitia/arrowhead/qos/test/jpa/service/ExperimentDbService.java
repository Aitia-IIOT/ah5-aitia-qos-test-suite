/*******************************************************************************
 *
 * Copyright (c) 2026 AITIA
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 *
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *  	AITIA - implementation
 *  	Arrowhead Consortia - conceptualization
 *
 *******************************************************************************/
package ai.aitia.arrowhead.qos.test.jpa.service;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ai.aitia.arrowhead.qos.test.jpa.entity.Experiment;
import ai.aitia.arrowhead.qos.test.jpa.entity.ExperimentData;
import ai.aitia.arrowhead.qos.test.jpa.repository.ExperimentDataRepository;
import ai.aitia.arrowhead.qos.test.jpa.repository.ExperimentRepository;
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.common.exception.DataNotFoundException;
import eu.arrowhead.common.exception.InternalServerError;

@Service
public class ExperimentDbService {

	//=================================================================================================
	// members

	@Autowired
	private ExperimentRepository expRepo;

	@Autowired
	private ExperimentDataRepository expDataRepo;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Transactional(rollbackFor = ArrowheadException.class)
	public void startExperiment(
			final UUID experimentId,
			final String comment,
			final boolean qosEnabled,
			final String qosDetails,
			final int iteration,
			final long wait,
			final int cpuStressPower,
			final long cpuStressLength,
			final long timeoutThreshold) {
		final Experiment exp = new Experiment(
				experimentId.toString(),
				comment,
				qosEnabled,
				qosDetails,
				iteration,
				wait,
				cpuStressPower,
				cpuStressLength,
				timeoutThreshold);

		try {
			expRepo.saveAndFlush(exp);
		} catch (final Exception ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
			throw new InternalServerError("Database operation error");
		}
	}

	//-------------------------------------------------------------------------------------------------
	@Transactional(rollbackFor = ArrowheadException.class)
	public void addExperimentStep(final UUID experimentId, final UUID stepId, final String provider, final ZonedDateTime start) {
		try {
			final Optional<Experiment> expOpt = expRepo.findById(experimentId.toString());
			if (expOpt.isEmpty()) {
				throw new DataNotFoundException("Experiment not found");
			}

			final ExperimentData data = new ExperimentData(stepId.toString(), expOpt.get(), provider, start);
			expDataRepo.saveAndFlush(data);
		} catch (final Exception ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
			throw new InternalServerError("Database operation error");
		}
	}
}