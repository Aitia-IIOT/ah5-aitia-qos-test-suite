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
package ai.aitia.arrowhead.qos.test.jpa.entity;

import java.time.ZonedDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class ExperimentData {

	//=================================================================================================
	// members
	
	@Id
	@Column(nullable = false, unique = true, length = 36)
	private String id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "experimentId", referencedColumnName = "id", nullable = false)
	private Experiment experiment;
	
	@Column(nullable = false, length = 63)
	private String provider;
	
	@Column(nullable = false)
	private ZonedDateTime start;
	
	@Column(nullable = true)
	private ZonedDateTime end;
	
	//=================================================================================================
	// members
	
	//-------------------------------------------------------------------------------------------------
	public ExperimentData() {
	}
	
	//-------------------------------------------------------------------------------------------------
	public ExperimentData(final String id, final Experiment experiment, final String provider, final ZonedDateTime start) {
		this.id = id;
		this.experiment = experiment;
		this.provider = provider;
		this.start = start;
	}
	
	//-------------------------------------------------------------------------------------------------
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		final ExperimentData other = (ExperimentData) obj;
		return id.equals(other.id);
	}
	
	//=================================================================================================
	// boilerplate

	//-------------------------------------------------------------------------------------------------
	public String getId() {
		return id;
	}

	//-------------------------------------------------------------------------------------------------
	public void setId(final String id) {
		this.id = id;
	}

	//-------------------------------------------------------------------------------------------------
	public Experiment getExperiment() {
		return experiment;
	}

	//-------------------------------------------------------------------------------------------------
	public void setExperiment(final Experiment experiment) {
		this.experiment = experiment;
	}

	//-------------------------------------------------------------------------------------------------
	public String getProvider() {
		return provider;
	}

	//-------------------------------------------------------------------------------------------------
	public void setProvider(final String provider) {
		this.provider = provider;
	}

	//-------------------------------------------------------------------------------------------------
	public ZonedDateTime getStart() {
		return start;
	}

	//-------------------------------------------------------------------------------------------------
	public void setStart(final ZonedDateTime start) {
		this.start = start;
	}

	//-------------------------------------------------------------------------------------------------
	public ZonedDateTime getEnd() {
		return end;
	}

	//-------------------------------------------------------------------------------------------------
	public void setEnd(final ZonedDateTime end) {
		this.end = end;
	}
}